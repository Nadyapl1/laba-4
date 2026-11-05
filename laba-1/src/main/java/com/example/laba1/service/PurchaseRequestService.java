
package com.example.laba1.service;

import com.example.laba1.model.PurchaseRequest;
import com.example.laba1.model.CommercialOffer;
import com.example.laba1.model.PurchaseOrder;
import com.example.laba1.model.User;
import com.example.laba1.repository.PurchaseRequestRepository;
import com.example.laba1.repository.UserRepository;
import com.example.laba1.repository.CommercialOfferRepository;
import com.example.laba1.repository.PurchaseOrderRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PurchaseRequestService {

    private final PurchaseRequestRepository repository;
    private final UserRepository userRepository;
    private final CommercialOfferRepository offerRepository;
    private final PurchaseOrderRepository orderRepository;

    public PurchaseRequestService(
            PurchaseRequestRepository repository,
            UserRepository userRepository,
            CommercialOfferRepository offerRepository,
            PurchaseOrderRepository orderRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NoSuchElementException("Пользователь не найден"));
    }
    public List<PurchaseRequest> findAll() {
        return repository.findAll();
    }
    private void checkOwner(
            PurchaseRequest request,
            User user,
            boolean canManageAll) {

        if (!canManageAll
                && !user.getId().equals(request.getAuthorId())) {
            throw new AccessDeniedException(
                    "Нет доступа к чужой заявке");
        }
    }

    public List<PurchaseRequest> findAllForUser(
            String email,
            boolean canViewAll) {

        if (canViewAll) {
            return repository.findAll();
        }

        User user = getUser(email);

        return repository.findAll().stream()
                .filter(request ->
                        user.getId().equals(request.getAuthorId()))
                .toList();
    }

    public PurchaseRequest findByIdForUser(
            Long id,
            String email,
            boolean canManageAll) {

        User user = getUser(email);
        PurchaseRequest request = findById(id);

        checkOwner(request, user, canManageAll);

        return request;
    }

    public PurchaseRequest findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Заявка не найдена: " + id));
    }

    public PurchaseRequest createForUser(
            PurchaseRequest request,
            String email) {

        if (request.getDescription() == null
                || request.getDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "Описание заявки обязательно");
        }

        if (request.getMaxAmount() == null
                || request.getMaxAmount().signum() <= 0) {
            throw new IllegalArgumentException(
                    "Бюджет заявки должен быть больше нуля");
        }

        User user = getUser(email);

        request.setId(null);
        request.setAuthorId(user.getId());
        request.setStatus("OPEN");

        return repository.save(request);
    }

    public PurchaseRequest updateForUser(
            Long id,
            PurchaseRequest changes,
            String email,
            boolean canManageAll) {

        User user = getUser(email);
        PurchaseRequest request = findById(id);

        checkOwner(request, user, canManageAll);

        if (changes.getDescription() != null) {
            if (changes.getDescription().isBlank()) {
                throw new IllegalArgumentException(
                        "Описание заявки обязательно");
            }
            request.setDescription(changes.getDescription());
        }

        if (changes.getMaxAmount() != null) {
            if (changes.getMaxAmount().signum() <= 0) {
                throw new IllegalArgumentException(
                        "Бюджет заявки должен быть больше нуля");
            }
            request.setMaxAmount(changes.getMaxAmount());
        }

        // authorId и status из запроса клиента игнорируются.
        return repository.save(request);
    }

    public void delete(Long id) {
        findById(id);

        List<CommercialOffer> offers = offerRepository.findAll()
                .stream()
                .filter(o -> o.getPurchaseRequestId() != null
                        && o.getPurchaseRequestId().equals(id))
                .toList();

        for (CommercialOffer offer : offers) {
            orderRepository.findAll().stream()
                    .filter(order -> order.getCommercialOfferId() != null
                            && order.getCommercialOfferId()
                                    .equals(offer.getId()))
                    .map(PurchaseOrder::getId)
                    .toList()
                    .forEach(orderRepository::deleteById);

            offerRepository.deleteById(offer.getId());
        }

        orderRepository.findAll().stream()
                .filter(order -> order.getPurchaseRequestId() != null
                        && order.getPurchaseRequestId().equals(id))
                .map(PurchaseOrder::getId)
                .toList()
                .forEach(orderRepository::deleteById);

        repository.deleteById(id);
    }

    public PurchaseRequest submitForApprovalForUser(
            Long id,
            String email,
            boolean canManageAll) {

        User user = getUser(email);
        PurchaseRequest request = findById(id);

        checkOwner(request, user, canManageAll);

        if (!"OPEN".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException(
                    "На согласование можно отправить только открытую заявку");
        }

        if (request.getDescription() == null
                || request.getDescription().isBlank()) {
            throw new IllegalStateException(
                    "У заявки должно быть описание");
        }

        if (request.getMaxAmount() == null
                || request.getMaxAmount().signum() <= 0) {
            throw new IllegalStateException(
                    "Бюджет заявки должен быть больше нуля");
        }

        request.setStatus("PENDING_APPROVAL");

        return repository.save(request);
    }
}
