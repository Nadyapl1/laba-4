
package com.example.laba1.repository;

import com.example.laba1.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {
}
