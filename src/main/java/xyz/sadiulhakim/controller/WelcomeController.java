package xyz.sadiulhakim.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class WelcomeController {

    @GetMapping("/")
    ResponseEntity<?> welcome() {

        return ResponseEntity.ok(
                Map.of("message", "Welcome, Back!")
        );
    }

    @GetMapping("/admin/greeting")
    ResponseEntity<?> adminGreeting() {

        return ResponseEntity.ok(
                Map.of("message", "Hi, This is admin!")
        );
    }

    @GetMapping("/user/greeting")
    ResponseEntity<?> userGreeting() {

        return ResponseEntity.ok(
                Map.of("message", "Hi, This is norman user!")
        );
    }

    @PostMapping("/test-csrf")
    ResponseEntity<?> testCsrf() {

        return ResponseEntity.ok(
                Map.of("message", "Csrf is ok!")
        );
    }
}
