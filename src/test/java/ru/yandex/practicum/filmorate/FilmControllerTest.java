package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Пустое название фильма — ошибка 400")
    void shouldReturnBadRequestWhenNameIsEmpty() throws Exception {
        String filmJson = "{ \"name\": \"\", \"description\": \"Test film\", \"releaseDate\": \"2000-01-01\", \"duration\": 120 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Название фильма не может быть пустым")));
    }

    @Test
    @DisplayName("Дата релиза до 28.12.1895 — ошибка 400")
    void shouldReturnBadRequestWhenReleaseDateTooEarly() throws Exception {
        String filmJson = "{ \"name\": \"Old movie\", \"description\": \"Test film\", \"releaseDate\": \"1800-01-01\", \"duration\": 120 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Дата релиза не может быть раньше")));
    }

    @Test
    @DisplayName("Продолжительность отрицательная — ошибка 400")
    void shouldReturnBadRequestWhenDurationNegative() throws Exception {
        String filmJson = "{ \"name\": \"Test film\", \"description\": \"Test film\", \"releaseDate\": \"2000-01-01\", \"duration\": -10 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Продолжительность фильма должна быть положительной")));
    }
}
