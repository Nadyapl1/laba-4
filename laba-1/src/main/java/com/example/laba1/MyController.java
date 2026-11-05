
package com.example.laba1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    // Первая ссылка: http://localhost:8080/api/hello
    // Возвращает ФИО и группу студента
    @GetMapping("/api/hello")
    public String hello() {
        return "Плетнева Надежда Михайловна, БАС2402";
    }

    // Вторая ссылка: http://localhost:8080/api/numbers
    // Возвращает число 985
    @GetMapping("/api/numbers")
    public int numbers() {
        return 985;
    }
}
