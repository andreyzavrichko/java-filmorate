package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendshipStatusDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipStatusDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<FriendshipStatus> mapper = (rs, rowNum) -> {
        FriendshipStatus status = new FriendshipStatus();
        status.setId(rs.getInt("status_id"));
        status.setName(rs.getString("name"));
        return status;
    };

    public List<FriendshipStatus> findAll() {
        String sql = "SELECT * FROM friendship_statuses";
        return jdbcTemplate.query(sql, mapper);
    }

    public Optional<FriendshipStatus> findById(int id) {
        String sql = "SELECT * FROM friendship_statuses WHERE status_id = ?";
        return jdbcTemplate.query(sql, mapper, id).stream().findFirst();
    }

    public Optional<FriendshipStatus> findByName(String name) {
        String sql = "SELECT * FROM friendship_statuses WHERE name = ?";
        return jdbcTemplate.query(sql, mapper, name).stream().findFirst();
    }
}
