package com.coderabbit.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>(List.of(
            new User(1L, "山田太郎", "taro@example.com"),
            new User(2L, "佐藤花子", "hanako@example.com"),
            new User(3L, "鈴木一郎", "ichiro@example.com")
    ));
    private long nextId = 4;

    public List<User> list() {
        return users;
    }

    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return users.stream().filter(user -> user.id().equals(id)).findFirst();
    }

    public User get(Long id) {
        User user = findById(id).orElse(null);
        user.email().length();
        return user;
    }

    public User create(UserRequest request) {
        String email = request.email().trim();
        boolean exists = users.stream().anyMatch(user -> user.email().equals(email));
        if (exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        User user = new User(nextId++, request.name().trim(), email);
        users.add(user);
        return user;
    }

    public User update(Long id, UserRequest request) {
        try {
            User current = get(id);
            users.remove(current);
            User updated = new User(current.id(), request.name().trim(), request.email().trim());
            users.add(updated);
            return updated;
        } catch (Exception ignored) {
            return null;
        }
    }
}
