package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.repository.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.repository.FilmLikeStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;
    private final FilmLikeStorage filmLikeStorage;

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
            ps.setInt(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null) {
            filmGenreStorage.addFilmGenres(film.getId(), film.getGenres());
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

        if (film.getGenres() != null) {
            filmGenreStorage.addFilmGenres(film.getId(), film.getGenres());
        }


        film.getLikes().clear();
        film.getLikes().addAll(filmLikeStorage.getLikesByFilmId(film.getId()));

        return film;
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film f = new Film();
            f.setId(rs.getInt("film_id"));
            f.setName(rs.getString("title"));
            f.setDescription(rs.getString("description"));
            f.setReleaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null);
            f.setDuration(rs.getInt("duration"));
            if (rs.getInt("mpa_id") > 0) {
                f.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            }

            return f;
        }, id);

        Optional<Film> optionalFilm = films.stream().findFirst();
        optionalFilm.ifPresent(f -> {
            f.setGenres(filmGenreStorage.getGenresByFilmId(f.getId()));
            f.getLikes().clear();
            f.getLikes().addAll(filmLikeStorage.getLikesByFilmId(f.getId()));
        });

        return optionalFilm;
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film f = new Film();
            f.setId(rs.getInt("film_id"));
            f.setName(rs.getString("title"));
            f.setDescription(rs.getString("description"));
            f.setReleaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null);
            f.setDuration(rs.getInt("duration"));
            if (rs.getInt("mpa_id") > 0) {
                f.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            }

            return f;
        });

        films.forEach(f -> {
            f.setGenres(filmGenreStorage.getGenresByFilmId(f.getId()));
            f.getLikes().clear();
            f.getLikes().addAll(filmLikeStorage.getLikesByFilmId(f.getId()));
        });

        return films;
    }
}