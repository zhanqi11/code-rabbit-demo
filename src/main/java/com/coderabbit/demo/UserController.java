package com.coderabbit.demo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserSearchService userSearchService;
    private final PaymentService paymentService;

    public UserController(UserService userService, UserSearchService userSearchService, PaymentService paymentService) {
        this.userService = userService;
        this.userSearchService = userSearchService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<User> list() {
        return userService.list();
    }

    @GetMapping("/search")
    public List<User> search(@RequestParam String name) {
        return userSearchService.searchByName(name);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PostMapping
    public User create(@RequestBody UserRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody UserRequest request) {
        return userService.update(id, request);
    }

    @PostMapping("/{userId}/payments")
    public Payment pay(@PathVariable Long userId, @RequestBody PaymentRequest request) {
        return paymentService.pay(userId, request);
    }

    @GetMapping("/{userId}/payments")
    public List<Payment> payments(@PathVariable Long userId) {
        return paymentService.listByUser(userId);
    }

    @PostMapping("/{userId}/payments/{paymentId}/cancel")
    public Payment cancel(@PathVariable Long userId, @PathVariable Long paymentId) {
        return paymentService.cancel(userId, paymentId);
    }
}
