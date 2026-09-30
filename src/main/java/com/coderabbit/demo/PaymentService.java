package com.coderabbit.demo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {

    private final UserService userService;
    private final AtomicLong sequence = new AtomicLong(1);
    private final List<Payment> payments = new CopyOnWriteArrayList<>();

    public PaymentService(UserService userService) {
        this.userService = userService;
    }

    public Payment pay(Long userId, PaymentRequest request) {
        if (userService.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (request == null || request.amount() == null || request.amount().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        String currency = request.currency() == null || request.currency().isBlank() ? "JPY" : request.currency();
        Payment payment = new Payment(sequence.getAndIncrement(), userId, request.amount(), currency, "COMPLETED");
        payments.add(payment);
        return payment;
    }

    public List<Payment> listByUser(Long userId) {
        if (userService.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return payments.stream().filter(payment -> payment.userId().equals(userId)).toList();
    }

    public Payment cancel(Long userId, Long paymentId) {
        Payment payment = payments.stream()
                .filter(item -> item.id().equals(paymentId) && item.userId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if ("CANCELLED".equals(payment.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Payment cancelled = new Payment(payment.id(), payment.userId(), payment.amount(), payment.currency(), "CANCELLED");
        payments.remove(payment);
        payments.add(cancelled);
        return cancelled;
    }
}
