package com.coderabbit.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final List<User> users = List.of(
            new User(1L, "山田太郎", "taro@example.com"),
            new User(2L, "佐藤花子", "hanako@example.com"),
            new User(3L, "鈴木一郎", "ichiro@example.com")
    );

    public List<User> list() {
        return users;
    }

    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return users.stream().filter(user -> user.id().equals(id)).findFirst();
    }
}
