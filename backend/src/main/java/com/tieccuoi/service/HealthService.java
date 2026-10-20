package com.tieccuoi.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {
    private final JdbcTemplate jdbcTemplate;

    public HealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countHalls() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SANH", Integer.class);
        return count == null ? 0 : count;
    }
}
