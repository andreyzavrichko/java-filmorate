package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;
import ru.yandex.practicum.filmorate.storage.repository.FilmLikeStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final FilmLikeStorage filmLikeStorage;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           @Qualifier("userDbStorage") UserStorage userStorage,
                           MpaService mpaService,
                           GenreService genreService,
                           FilmLikeStorage filmLikeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.filmLikeStorage = filmLikeStorage;
    }

    @Override
    public Film create(Film film) {

        if (film.getMpa() != null && film.getMpa().getId() > 0) {
            mpaService.getById(film.getMpa().getId());
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() > 0) {
                    genreService.getById(genre.getId());
                }
            }
        }
        return filmStorage.add(film);
    }

    @Override
    public Film update(Film film) {
        if (filmStorage.getById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        if (film.getMpa() != null && film.getMpa().getId() > 0) {
            mpaService.getById(film.getMpa().getId());
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() > 0) {
                    genreService.getById(genre.getId());
                }
            }
        }
        return filmStorage.update(film);
    }

    @Override
    public List<Film> findAll() {
        return filmStorage.getAll();
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = findById(filmId);
        if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        filmLikeStorage.addLike(filmId, userId);
        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = findById(filmId);
        if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        filmLikeStorage.removeLike(filmId, userId);
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film findById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }
}