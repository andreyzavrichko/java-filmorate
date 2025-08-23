package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NotBlank(message = "Email не может быть пустым", groups = OnCreate.class)
    @Email(message = "Некорректный формат email", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(message = "Логин не может быть пустым или содержать пробелы", groups = OnCreate.class)
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем",
            groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;
}
