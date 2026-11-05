package com.example.laba1.service;

import com.example.laba1.model.PurchaseOrder;
import com.example.laba1.model.PurchaseRequest;
import com.example.laba1.model.CommercialOffer;
import com.example.laba1.model.Supplier;
import com.example.laba1.repository.PurchaseOrderRepository;
import com.example.laba1.repository.PurchaseRequestRepository;
import com.example.laba1.repository.CommercialOfferRepository;
import com.example.laba1.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository repository;
    private final PurchaseRequestRepository requestRepository;
    private final CommercialOfferRepository offerRepository;
    private final SupplierRepository supplierRepository;

    public PurchaseOrderService(
            PurchaseOrderRepository repository,
            PurchaseRequestRepository requestRepository,
            CommercialOfferRepository offerRepository,
            SupplierRepository supplierRepository) {
        this.repository = repository;
        this.requestRepository = requestRepository;
        this.offerRepository = offerRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<PurchaseOrder> findAll() {
        return repository.findAll();
    }

    public PurchaseOrder findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Закупочный заказ не найден: " + id));
    }

    public PurchaseOrder create(PurchaseOrder order) {
        validateReferences(order);
        order.setId(null);
        return repository.save(order);
    }

    public PurchaseOrder update(Long id, PurchaseOrder changes) {
        PurchaseOrder order = findById(id);

        if (changes.getPurchaseRequestId() != null) {
            order.setPurchaseRequestId(changes.getPurchaseRequestId());
        }
        if (changes.getCommercialOfferId() != null) {
            order.setCommercialOfferId(changes.getCommercialOfferId());
        }
        if (changes.getStatus() != null) {
            order.setStatus(changes.getStatus());
        }

        validateReferences(order);
        return repository.save(order);
    }

    private void validateReferences(PurchaseOrder order) {
        if (order.getPurchaseRequestId() == null
                || !requestRepository.existsById(
                        order.getPurchaseRequestId())) {
            throw new IllegalArgumentException("Заявка не найдена");
        }

        CommercialOffer offer = offerRepository
                .findById(order.getCommercialOfferId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Коммерческое предложение не найдено"));

        if (!offer.getPurchaseRequestId()
                .equals(order.getPurchaseRequestId())) {
            throw new IllegalArgumentException(
                    "Предложение не относится к указанной заявке");
        }
    }

    @Transactional
    public PurchaseOrder acceptOffer(Long offerId) {
        // 1. Находим коммерческое предложение.
        CommercialOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Коммерческое предложение не найдено: " + offerId));

        // 2. Проверяем срок действия.
        if (offer.getValidUntil() == null
                || !offer.getValidUntil().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Срок действия коммерческого предложения истёк");
        }

        // 3. Находим заявку, к которой относится предложение.
        PurchaseRequest request = requestRepository
                .findById(offer.getPurchaseRequestId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Заявка не найдена"));

        // 4. Принимать можно только открытую заявку.
       if (!"PENDING_APPROVAL".equalsIgnoreCase(request.getStatus())) {
        throw new IllegalStateException(
            "Заявка не отправлена на согласование");
}

        // 5. Проверяем бюджет.
        if (offer.getPrice() == null
                || request.getMaxAmount() == null
                || offer.getPrice().compareTo(request.getMaxAmount()) > 0) {
            throw new IllegalStateException(
                    "Цена предложения превышает бюджет заявки");
        }

        // 6. Проверяем, что поставщик существует и одобрен.
        Supplier supplier = supplierRepository
                .findById(offer.getSupplierId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Поставщик не найден"));

        if (!Boolean.TRUE.equals(supplier.getApproved())) {
            throw new IllegalStateException(
                    "Поставщик не одобрен");
        }

        // 7. Не допускаем повторного создания заказа для заявки.
        boolean orderAlreadyExists = repository.findAll().stream()
                .anyMatch(order -> request.getId().equals(
                        order.getPurchaseRequestId()));

        if (orderAlreadyExists) {
            throw new IllegalStateException(
                    "Для этой заявки уже существует заказ");
        }

        // 8. Создаём заказ.
        PurchaseOrder order = new PurchaseOrder();
        order.setPurchaseRequestId(request.getId());
        order.setCommercialOfferId(offer.getId());
        order.setStatus("CREATED");

        PurchaseOrder savedOrder = repository.save(order);

        // 9. Меняем статус заявки.
        request.setStatus("ACCEPTED");
        requestRepository.save(request);

        return savedOrder;
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}