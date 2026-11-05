package com.example.laba1.service;

import com.example.laba1.model.Supplier;
import com.example.laba1.model.CommercialOffer;
import com.example.laba1.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SupplierService {

    private final SupplierRepository repository;
    private final CommercialOfferService offerService;

    public SupplierService(
            SupplierRepository repository,
            CommercialOfferService offerService) {
        this.repository = repository;
        this.offerService = offerService;
    }

    public List<Supplier> findAll() {
        return repository.findAll();
    }

    public Supplier findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Поставщик не найден: " + id));
    }

    public Supplier create(Supplier supplier) {
        if (supplier.getName() == null
                || supplier.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Название поставщика обязательно");
        }

        if (supplier.getCategory() == null
                || supplier.getCategory().isBlank()) {
            throw new IllegalArgumentException(
                    "Категория поставщика обязательна");
        }

        supplier.setId(null);
        return repository.save(supplier);
    }

    public Supplier update(Long id, Supplier changes) {
        Supplier supplier = findById(id);

        if (changes.getName() != null) {
            supplier.setName(changes.getName());
        }

        if (changes.getCategory() != null) {
            supplier.setCategory(changes.getCategory());
        }

        if (changes.getApproved() != null) {
            supplier.setApproved(changes.getApproved());
        }

        return repository.save(supplier);
    }

    public Supplier approve(Long id) {
        Supplier supplier = findById(id);

        if (Boolean.TRUE.equals(supplier.getApproved())) {
            throw new IllegalStateException(
                    "Поставщик уже одобрен");
        }

        supplier.setApproved(true);
        return repository.save(supplier);
    }

    public void delete(Long id) {
        findById(id);

        List<Long> offerIds = offerService.findAll().stream()
                .filter(offer -> id.equals(offer.getSupplierId()))
                .map(CommercialOffer::getId)
                .toList();

        offerIds.forEach(offerService::delete);
        repository.deleteById(id);
    }
}