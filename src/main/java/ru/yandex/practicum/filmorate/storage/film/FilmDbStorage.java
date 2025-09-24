package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (title, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null);
            ps.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        // сохраним жанры одним запросом
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            batchInsertFilmGenres(film.getId(), film.getGenres());
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        // обновляем жанры (удаляем старые, вставляем новые)
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            batchInsertFilmGenres(film.getId(), film.getGenres());
        }

        return getById(film.getId()).orElseThrow(() ->
                new NotFoundException("Фильм с id " + film.getId() + " не найден после обновления"));
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.getFirst();
        film.setGenres(loadGenresForFilms(List.of(film.getId())).getOrDefault(film.getId(), new LinkedHashSet<>()));

        return Optional.of(film);
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs));

        // подгружаем жанры одним запросом для всех фильмов
        Map<Integer, Set<Genre>> filmGenres = loadGenresForFilms(
                films.stream().map(Film::getId).toList()
        );

        films.forEach(f -> f.setGenres(filmGenres.getOrDefault(f.getId(), new LinkedHashSet<>())));

        return films;
    }

    public List<Film> getPopular(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS like_count " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id, f.title, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), count);

        Map<Integer, Set<Genre>> filmGenres = loadGenresForFilms(
                films.stream().map(Film::getId).toList()
        );

        films.forEach(f -> f.setGenres(filmGenres.getOrDefault(f.getId(), new LinkedHashSet<>())));

        return films;
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("title"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null);
        film.setDuration(rs.getInt("duration"));

        int mpaId = rs.getInt("mpa_id");
        if (mpaId > 0) {
            film.setMpa(new Mpa(mpaId, rs.getString("mpa_name")));
        }

        return film;
    }

    private void batchInsertFilmGenres(int filmId, Set<Genre> genres) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> params = genres.stream()
                .map(g -> new Object[]{filmId, g.getId()})
                .toList();
        jdbcTemplate.batchUpdate(sql, params);
    }

    private Map<Integer, Set<Genre>> loadGenresForFilms(List<Integer> filmIds) {
        if (filmIds.isEmpty()) {
            return Map.of();
        }

        String sql = """
                SELECT fg.film_id, g.genre_id, g.name
                FROM film_genres fg
                JOIN genres g ON fg.genre_id = g.genre_id
                WHERE fg.film_id IN (%s)
                ORDER BY g.genre_id
                """.formatted(
                filmIds.stream().map(id -> "?").collect(Collectors.joining(", "))
        );

        Map<Integer, Set<Genre>> result = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            int filmId = rs.getInt("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
            result.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
        }, filmIds.toArray());

        return result;
    }
}
