package ru.yandex.practicum.filmorate.storage.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT mpa_id, name FROM mpa_ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name")));
    }

    @Override
    public Optional<Mpa> getById(int id) {
        String sql = "SELECT mpa_id, name FROM mpa_ratings WHERE mpa_id = ?";
        List<Mpa> ratings = jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name")), id);
        return ratings.stream().findFirst();
    }
}

