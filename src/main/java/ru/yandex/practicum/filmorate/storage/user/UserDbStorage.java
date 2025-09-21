package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        String sql = "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null);
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null,
                user.getId()
        );
        return user;
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User u = new User();
            u.setId(rs.getInt("user_id"));
            u.setName(rs.getString("name"));
            u.setEmail(rs.getString("email"));
            u.setLogin(rs.getString("login"));
            u.setBirthday(rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null);
            return u;
        }, id);
        return users.stream().findFirst();
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User u = new User();
            u.setId(rs.getInt("user_id"));
            u.setName(rs.getString("name"));
            u.setEmail(rs.getString("email"));
            u.setLogin(rs.getString("login"));
            u.setBirthday(rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null);
            return u;
        });
    }
}
