
package com.example.laba1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Имя обязательно")
        @Size(max = 100, message = "Имя слишком длинное")
        String name,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный email")
        String email,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 72,
                message = "Пароль должен содержать от 8 до 72 символов")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "Пароль должен содержать строчную, заглавную букву, цифру и специальный символ"
        )
        String password
) {
}
