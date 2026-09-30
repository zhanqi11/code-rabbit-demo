package com.coderabbit.demo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PaymentService paymentService;

    public UserController(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<User> list() {
        return userService.list();
    }

    @PostMapping("/{userId}/payments")
    public Payment pay(@PathVariable Long userId, @RequestBody PaymentRequest request) {
        return paymentService.pay(userId, request);
    }
}
