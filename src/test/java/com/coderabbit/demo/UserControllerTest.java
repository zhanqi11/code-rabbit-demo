package com.coderabbit.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Test
    void listUsers() {
        User[] users = RestClient.create("http://localhost:" + port)
                .get()
                .uri("/users")
                .retrieve()
                .body(User[].class);

        assertThat(users).hasSize(3);
        assertThat(users[0].id()).isEqualTo(1L);
        assertThat(users[0].name()).isEqualTo("山田太郎");
        assertThat(users[0].email()).isEqualTo("taro@example.com");
    }

    @Test
    void payForExistingUser() {
        Payment payment = client()
                .post()
                .uri("/users/1/payments")
                .body(new PaymentRequest(new BigDecimal("1200"), "JPY"))
                .retrieve()
                .body(Payment.class);

        assertThat(payment.id()).isEqualTo(1L);
        assertThat(payment.userId()).isEqualTo(1L);
        assertThat(payment.amount()).isEqualByComparingTo("1200");
        assertThat(payment.currency()).isEqualTo("JPY");
        assertThat(payment.status()).isEqualTo("COMPLETED");
    }

    @Test
    void payForUnknownUser() {
        assertThatThrownBy(() -> client()
                .post()
                .uri("/users/999/payments")
                .body(new PaymentRequest(new BigDecimal("100"), "JPY"))
                .retrieve()
                .body(Payment.class))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void payRejectsNonPositiveAmount() {
        assertThatThrownBy(() -> client()
                .post()
                .uri("/users/1/payments")
                .body(new PaymentRequest(BigDecimal.ZERO, "JPY"))
                .retrieve()
                .body(Payment.class))
                .isInstanceOf(HttpClientErrorException.class)
                .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private RestClient client() {
        return RestClient.create("http://localhost:" + port);
    }
}
