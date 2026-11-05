
package com.example.laba1.security;

import com.example.laba1.model.PurchaseRequest;
import com.example.laba1.model.User;
import com.example.laba1.repository.PurchaseRequestRepository;
import com.example.laba1.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("purchaseRequestSecurity")
public class PurchaseRequestSecurity {

    private final PurchaseRequestRepository requestRepository;
    private final UserRepository userRepository;

    public PurchaseRequestSecurity(
            PurchaseRequestRepository requestRepository,
            UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public boolean isOwner(Long requestId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return false;
        }

        PurchaseRequest request = requestRepository.findById(requestId)
                .orElse(null);

        return request != null
                && request.getAuthorId().equals(user.getId());
    }
}
