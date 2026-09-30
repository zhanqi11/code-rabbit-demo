package com.coderabbit.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
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
}
