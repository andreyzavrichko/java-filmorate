package ru.yandex.practicum.filmorate.storage.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void addFilmGenres(int filmId, Set<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            deleteFilmGenres(filmId);
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : genres) {
                jdbcTemplate.update(sql, filmId, genre.getId());
            }
        }
    }

    public void deleteFilmGenres(int filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }


    public Set<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY fg.genre_id";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")), filmId);
        return new HashSet<>(genres);
    }
}
