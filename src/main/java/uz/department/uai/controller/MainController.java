package uz.department.uai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String home() {
        return "Salom, Utkir! Spring Boot ilovang muvaffaqiyatli ishga tushdi 🚀";
//        b691bb3d-2243-468c-b537-80527348df6f

    }
}
