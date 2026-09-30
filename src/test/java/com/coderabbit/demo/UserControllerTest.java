package com.coderabbit.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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

    @Test
    void getUser() {
        User user = client()
                .get()
                .uri("/users/1")
                .retrieve()
                .body(User.class);

        assertThat(user.name()).isEqualTo("山田太郎");
        assertThat(user.email()).isEqualTo("taro@example.com");
    }

    @Test
    void createUser() {
        User user = client()
                .post()
                .uri("/users")
                .body(new UserRequest("高橋美咲", "misaki@example.com"))
                .retrieve()
                .body(User.class);

        assertThat(user.id()).isEqualTo(4L);
        assertThat(user.name()).isEqualTo("高橋美咲");
        assertThat(user.email()).isEqualTo("misaki@example.com");
    }

    @Test
    void updateUser() {
        User user = client()
                .put()
                .uri("/users/1")
                .body(new UserRequest("山田次郎", "jiro@example.com"))
                .retrieve()
                .body(User.class);

        assertThat(user.id()).isEqualTo(1L);
        assertThat(user.name()).isEqualTo("山田次郎");
        assertThat(user.email()).isEqualTo("jiro@example.com");
    }

    @Test
    void searchUserByName() {
        User[] users = client()
                .get()
                .uri("/users/search?name={name}", "山田太郎")
                .retrieve()
                .body(User[].class);

        assertThat(users).hasSize(1);
        assertThat(users[0].id()).isEqualTo(1L);
    }

    @Test
    void listAndCancelPayment() {
        Payment payment = client()
                .post()
                .uri("/users/1/payments")
                .body(new PaymentRequest(new BigDecimal("500"), "JPY"))
                .retrieve()
                .body(Payment.class);

        Payment[] payments = client()
                .get()
                .uri("/users/1/payments")
                .retrieve()
                .body(Payment[].class);
        assertThat(payments).hasSize(1);

        Payment cancelled = client()
                .post()
                .uri("/users/1/payments/" + payment.id() + "/cancel")
                .retrieve()
                .body(Payment.class);
        assertThat(cancelled.status()).isEqualTo("CANCELLED");
    }

    private RestClient client() {
        return RestClient.create("http://localhost:" + port);
    }
}
