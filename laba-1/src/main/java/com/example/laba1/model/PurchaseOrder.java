
package com.example.laba1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_request_id", nullable = false)
    private Long purchaseRequestId;

    @Column(name = "commercial_offer_id", nullable = false)
    private Long commercialOfferId;

    @Column(nullable = false)
    private String status;

    public PurchaseOrder() {
    }

    public PurchaseOrder(Long id, Long purchaseRequestId,
                         Long commercialOfferId, String status) {
        this.id = id;
        this.purchaseRequestId = purchaseRequestId;
        this.commercialOfferId = commercialOfferId;
        this.status = status;
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

    public Long getCommercialOfferId() {
        return commercialOfferId;
    }

    public void setCommercialOfferId(Long commercialOfferId) {
        this.commercialOfferId = commercialOfferId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
