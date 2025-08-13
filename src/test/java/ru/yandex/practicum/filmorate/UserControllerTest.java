package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Email пустой — ошибка 400")
    void shouldReturnBadRequestWhenEmailEmpty() throws Exception {
        String userJson = "{ \"email\": \"\", \"login\": \"user1\", \"name\": \"User One\", \"birthday\": \"2000-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON) // <-- добавили Accept с JSON
                        .characterEncoding("UTF-8")
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email не может быть пустым")));
    }

    @Test
    @DisplayName("Логин с пробелами — ошибка 400")
    void shouldReturnBadRequestWhenLoginContainsSpaces() throws Exception {
        String userJson = "{ \"email\": \"user@mail.com\", \"login\": \"bad login\", \"name\": \"User One\", \"birthday\": \"2000-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Логин не может быть пустым или содержать пробелы")));
    }

    @Test
    @DisplayName("Дата рождения в будущем — ошибка 400")
    void shouldReturnBadRequestWhenBirthdayInFuture() throws Exception {
        String futureDate = LocalDate.now().plusDays(1).toString();
        String userJson = "{ \"email\": \"user@mail.com\", \"login\": \"user1\", \"name\": \"User One\", \"birthday\": \"%s\" }".formatted(futureDate);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Дата рождения не может быть в будущем")));
    }
}
