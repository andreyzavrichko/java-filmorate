package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.repository.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.repository.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.repository.GenreDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmGenreStorage.class, FilmLikeStorage.class, GenreDbStorage.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    void testCreateAndFindFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmStorage.add(film);

        Optional<Film> filmOptional = filmStorage.getById(created.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(found ->
                        assertThat(found)
                                .hasFieldOrPropertyWithValue("id", created.getId())
                                .hasFieldOrPropertyWithValue("name", "Test Film")
                );
    }
}