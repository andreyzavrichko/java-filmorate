package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserDbStorage.class)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void testCreateAndFindUserById() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.add(user);

        Optional<User> userOptional = userStorage.getById(created.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(found ->
                        assertThat(found)
                                .hasFieldOrPropertyWithValue("id", created.getId())
                                .hasFieldOrPropertyWithValue("email", "test@test.com")
                                .hasFieldOrPropertyWithValue("login", "testLogin")
                );
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("old@test.com");
        user.setLogin("oldLogin");
        user.setName("Old User");
        user.setBirthday(LocalDate.of(1980, 5, 5));
        User created = userStorage.add(user);

        created.setEmail("new@test.com");
        created.setLogin("newLogin");
        created.setName("New User");

        User updated = userStorage.update(created);

        assertThat(updated.getEmail()).isEqualTo("new@test.com");
        assertThat(updated.getLogin()).isEqualTo("newLogin");
        assertThat(updated.getName()).isEqualTo("New User");
    }

    @Test
    void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("u1@test.com");
        user1.setLogin("u1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1991, 1, 1));
        userStorage.add(user1);

        User user2 = new User();
        user2.setEmail("u2@test.com");
        user2.setLogin("u2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1992, 2, 2));
        userStorage.add(user2);

        assertThat(userStorage.getAll()).hasSize(2);
    }
}
