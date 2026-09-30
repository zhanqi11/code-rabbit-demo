package com.coderabbit.demo;

import java.math.BigDecimal;

public record Payment(Long id, Long userId, BigDecimal amount, String currency, String status) {
}
