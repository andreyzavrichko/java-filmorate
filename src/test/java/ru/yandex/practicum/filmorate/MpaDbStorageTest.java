package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.repository.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaDbStorage.class)
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaDbStorage;

    @Test
    void testGetAllMpa() {
        List<Mpa> mpa = mpaDbStorage.getAll();

        assertThat(mpa).isNotEmpty();
        assertThat(mpa.getFirst()).hasFieldOrProperty("id").hasFieldOrProperty("name");
    }

    @Test
    void testGetMpaById() {
        Optional<Mpa> mpaOptional = mpaDbStorage.getById(1);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                );
    }
}
