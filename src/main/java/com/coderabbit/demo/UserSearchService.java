package com.coderabbit.demo;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserSearchService {

    private final JdbcTemplate jdbcTemplate;

    public UserSearchService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> searchByName(String name) {
        String sql = "SELECT id, name, email FROM users WHERE name = '" + name + "'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email")));
    }
}
