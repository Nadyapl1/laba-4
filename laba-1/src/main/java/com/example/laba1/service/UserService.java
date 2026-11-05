
package com.example.laba1.service;

import com.example.laba1.model.User;
import com.example.laba1.model.Role;
import com.example.laba1.model.PurchaseRequest;
import com.example.laba1.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository repository;
    private final PurchaseRequestService requestService;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            PurchaseRequestService requestService,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.requestService = requestService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь не найден: " + id));
    }

    @Transactional
    public User create(User user) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Пароль обязателен");
        }

        user.setId(null);

        String email = user.getEmail().trim().toLowerCase(Locale.ROOT);

        if (repository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Пользователь с таким email уже существует");
        }

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return repository.save(user);
    }

    @Transactional
    public User update(Long id, User changes) {
        User user = findById(id);

        if (changes.getName() != null) {
            user.setName(changes.getName().trim());
        }

        if (changes.getEmail() != null) {
            String email = changes.getEmail().trim().toLowerCase(Locale.ROOT);

            repository.findByEmail(email).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Пользователь с таким email уже существует");
                }
            });

            user.setEmail(email);
        }

        return repository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);

        List<Long> requestIds = requestService.findAll()
                .stream()
                .filter(request -> id.equals(request.getAuthorId()))
                .map(PurchaseRequest::getId)
                .toList();

        requestIds.forEach(requestService::delete);
        repository.deleteById(id);
    }
}
