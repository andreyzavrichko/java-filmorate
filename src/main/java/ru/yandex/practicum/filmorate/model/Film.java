package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private int id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    private int duration;

    private final Set<Integer> likes = new HashSet<>();

    private Set<Genre> genres = new HashSet<>();

    private Mpa mpa;

    @AssertTrue(message = "Дата релиза не может быть раньше 28.12.1895")
    public boolean isReleaseDateValid() {
        return releaseDate == null || !releaseDate.isBefore(CINEMA_BIRTHDAY);
    }
}
