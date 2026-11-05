
package com.example.laba1.repository;

import com.example.laba1.model.CommercialOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommercialOfferRepository
        extends JpaRepository<CommercialOffer, Long> {
}
