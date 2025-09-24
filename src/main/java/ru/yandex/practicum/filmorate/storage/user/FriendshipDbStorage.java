package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorage;

import java.util.List;

@Repository
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(int userId, int friendId, String statusName) {
        int statusId = getStatusIdByName(statusName);
        String sql = "INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, statusId);
    }

    @Override
    public void update(int userId, int friendId, String statusName) {
        int statusId = getStatusIdByName(statusName);
        String sql = "UPDATE friendships SET status_id = ? WHERE user_id = ? AND friend_id = ?";
        int updated = jdbcTemplate.update(sql, statusId, userId, friendId);
        if (updated == 0) {
            throw new NotFoundException("Запись дружбы userId=%d, friendId=%d не найдена".formatted(userId, friendId));
        }
    }

    @Override
    public void delete(int userId, int friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<Integer> getFriends(int userId, String statusFilter) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        Object[] params;

        if (statusFilter != null) {
            int statusId = getStatusIdByName(statusFilter);
            sql += " AND status_id = ?";
            params = new Object[]{userId, statusId};
        } else {
            params = new Object[]{userId};
        }

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), params);
    }

    @Override
    public boolean exists(int userId, int friendId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    private int getStatusIdByName(String statusName) {
        String sql = "SELECT status_id FROM friendship_statuses WHERE name = ?";
        List<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("status_id"), statusName);
        if (ids.isEmpty()) {
            throw new NotFoundException("Friendship status '%s' not found".formatted(statusName));
        }
        return ids.getFirst();
    }
}
