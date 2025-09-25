package ru.yandex.practicum.filmorate.storage.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

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

    public int getLikesCount(int filmId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
        String sql = """
                    SELECT f.film_id,
                           f.title,
                           f.description,
                           f.release_date,
                           f.duration,
                           m.mpa_id,
                           m.name AS mpa_name,
                           COUNT(l.user_id) AS like_count
                    FROM films f
                    JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
                    LEFT JOIN likes l ON f.film_id = l.film_id
                    GROUP BY f.film_id, f.title, f.description, f.release_date, f.duration, m.mpa_id, m.name
                    ORDER BY like_count DESC
                    LIMIT ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            return film;
        }, count);
    }
}
