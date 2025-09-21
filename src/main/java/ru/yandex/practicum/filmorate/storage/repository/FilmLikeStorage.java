package ru.yandex.practicum.filmorate.storage.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmLikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }


    public Set<Integer> getLikesByFilmId(int filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> userIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId);
        return new HashSet<>(userIds);
    }
}
