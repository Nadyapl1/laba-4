
package com.example.laba1.controller;

import com.example.laba1.model.PurchaseRequest;
import com.example.laba1.service.PurchaseRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-requests")
public class PurchaseRequestController {

    private final PurchaseRequestService service;

    public PurchaseRequestController(PurchaseRequestService service) {
        this.service = service;
    }

    private boolean canManageAll(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_MANAGER")
                                || role.equals("ROLE_ADMIN"));
    }

    @GetMapping
    public List<PurchaseRequest> findAll(
            Authentication authentication) {

        return service.findAllForUser(
                authentication.getName(),
                canManageAll(authentication));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or "
            + "@purchaseRequestSecurity.isOwner(#id, authentication)")
    public PurchaseRequest findById(
            @PathVariable Long id,
            Authentication authentication) {

        return service.findByIdForUser(
                id,
                authentication.getName(),
                canManageAll(authentication));
    }

    @PostMapping
    public ResponseEntity<PurchaseRequest> create(
            @RequestBody PurchaseRequest request,
            Authentication authentication) {

        PurchaseRequest created = service.createForUser(
                request,
                authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or "
            + "@purchaseRequestSecurity.isOwner(#id, authentication)")
    public PurchaseRequest update(
            @PathVariable Long id,
            @RequestBody PurchaseRequest changes,
            Authentication authentication) {

        return service.updateForUser(
                id,
                changes,
                authentication.getName(),
                canManageAll(authentication));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit-for-approval")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or "
            + "@purchaseRequestSecurity.isOwner(#id, authentication)")
    public PurchaseRequest submitForApproval(
            @PathVariable Long id,
            Authentication authentication) {

        return service.submitForApprovalForUser(
                id,
                authentication.getName(),
                canManageAll(authentication));
    }
}
