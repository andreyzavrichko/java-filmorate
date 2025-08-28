package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {

    private final FilmServiceImpl filmServiceImpl;

    @Autowired
    public FilmController(FilmServiceImpl filmServiceImpl) {
        this.filmServiceImpl = filmServiceImpl;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmServiceImpl.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmServiceImpl.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmServiceImpl.findAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return filmServiceImpl.findById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmServiceImpl.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmServiceImpl.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmServiceImpl.getPopular(count);
    }
}
