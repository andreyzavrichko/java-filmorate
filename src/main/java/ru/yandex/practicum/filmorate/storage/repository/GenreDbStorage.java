package ru.yandex.practicum.filmorate.storage.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT genre_id, name FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")));
    }

    @Override
    public Optional<Genre> getById(int id) {
        String sql = "SELECT genre_id, name FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")), id);
        return genres.stream().findFirst();
    }
}
