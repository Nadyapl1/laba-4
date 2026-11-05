
package com.example.laba1.service;

import com.example.laba1.dto.RegisterRequest;
import com.example.laba1.model.Role;
import com.example.laba1.model.User;
import com.example.laba1.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Пользователь с таким email уже существует"
            );
        }

        User user = new User();

        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        // Роль назначается сервером, а не клиентом
        user.setRole(Role.USER);
        user.setEnabled(true);

        return userRepository.save(user);
    }
}
