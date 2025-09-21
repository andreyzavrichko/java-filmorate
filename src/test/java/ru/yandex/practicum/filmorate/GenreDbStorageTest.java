package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.repository.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(GenreDbStorage.class)
class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage genreDbStorage;

    @Test
    void testGetAllGenres() {
        List<Genre> genres = genreDbStorage.getAll();

        assertThat(genres).isNotEmpty();
        assertThat(genres.getFirst()).hasFieldOrProperty("id").hasFieldOrProperty("name");
    }

    @Test
    void testGetGenreById() {
        Optional<Genre> genreOptional = genreDbStorage.getById(1);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
                );
    }
}
