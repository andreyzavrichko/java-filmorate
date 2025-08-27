package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.getById(film.getId())
                .map(f -> filmStorage.update(film))
                .orElseThrow(() -> new NotFoundException(
                        "Фильм с id " + film.getId() + " не найден"));
    }

    public List<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film findById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public void addLike(int filmId, int userId) {
        Film film = findById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = findById(filmId);
        if (!film.getLikes().remove(userId)) {
            throw new NotFoundException("Лайк от пользователя " + userId + " не найден для фильма " + filmId);
        }
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }
}
