
package com.example.laba1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commercial_offers")
public class CommercialOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_request_id", nullable = false)
    private Long purchaseRequestId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "delivery_days", nullable = false)
    private Integer deliveryDays;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    public CommercialOffer() {
    }

    public CommercialOffer(Long id, Long purchaseRequestId, Long supplierId,
                           BigDecimal price, Integer deliveryDays,
                           LocalDateTime validUntil) {
        this.id = id;
        this.purchaseRequestId = purchaseRequestId;
        this.supplierId = supplierId;
        this.price = price;
        this.deliveryDays = deliveryDays;
        this.validUntil = validUntil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPurchaseRequestId() {
        return purchaseRequestId;
    }

    public void setPurchaseRequestId(Long purchaseRequestId) {
        this.purchaseRequestId = purchaseRequestId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(Integer deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
}
