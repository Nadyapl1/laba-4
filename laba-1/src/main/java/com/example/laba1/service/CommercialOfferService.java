
package com.example.laba1.service;

import com.example.laba1.model.CommercialOffer;
import com.example.laba1.model.PurchaseOrder;
import com.example.laba1.repository.CommercialOfferRepository;
import com.example.laba1.repository.PurchaseRequestRepository;
import com.example.laba1.repository.SupplierRepository;
import com.example.laba1.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommercialOfferService {

    private final CommercialOfferRepository repository;
    private final PurchaseRequestRepository requestRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository orderRepository;

    public CommercialOfferService(
            CommercialOfferRepository repository,
            PurchaseRequestRepository requestRepository,
            SupplierRepository supplierRepository,
            PurchaseOrderRepository orderRepository) {
        this.repository = repository;
        this.requestRepository = requestRepository;
        this.supplierRepository = supplierRepository;
        this.orderRepository = orderRepository;
    }

    public List<CommercialOffer> findAll() {
        return repository.findAll();
    }

    public CommercialOffer findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Коммерческое предложение не найдено: " + id));
    }

    public CommercialOffer create(CommercialOffer offer) {
        validateReferences(offer);

        if (offer.getValidUntil() == null) {
            throw new IllegalArgumentException(
                    "Срок действия предложения обязателен");
        }

        offer.setId(null);

        return repository.save(offer);
    }

    public CommercialOffer update(Long id, CommercialOffer changes) {
        CommercialOffer offer = findById(id);

        if (changes.getPurchaseRequestId() != null) {
            offer.setPurchaseRequestId(changes.getPurchaseRequestId());
        }

        if (changes.getSupplierId() != null) {
            offer.setSupplierId(changes.getSupplierId());
        }

        if (changes.getPrice() != null) {
            offer.setPrice(changes.getPrice());
        }

        if (changes.getDeliveryDays() != null) {
            offer.setDeliveryDays(changes.getDeliveryDays());
        }

        validateReferences(offer);

        return repository.save(offer);
    }

    private void validateReferences(CommercialOffer offer) {
        if (offer.getPurchaseRequestId() == null
                || !requestRepository.existsById(
                        offer.getPurchaseRequestId())) {
            throw new NoSuchElementException("Заявка не найдена");
        }

        if (offer.getSupplierId() == null
                || !supplierRepository.existsById(
                        offer.getSupplierId())) {
            throw new NoSuchElementException("Поставщик не найден");
        }
    }

    public void delete(Long id) {
        findById(id);

        List<Long> orderIds = orderRepository.findAll().stream()
                .filter(order ->
                        id.equals(order.getCommercialOfferId()))
                .map(PurchaseOrder::getId)
                .toList();

        orderIds.forEach(orderRepository::deleteById);

        repository.deleteById(id);
    }
}
