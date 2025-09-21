package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorage;

import java.util.List;

@Repository
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStatusDbStorage statusStorage;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate, FriendshipStatusDbStorage statusStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.statusStorage = statusStorage;
    }

    @Override
    public void add(int userId, int friendId, String statusName) {
        FriendshipStatus status = statusStorage.findByName(statusName)
                .orElseThrow(() -> new NotFoundException("Friendship status '" + statusName + "' not found"));
        int statusId = status.getId();
        String sql = "INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, statusId);
    }

    @Override
    public void update(int userId, int friendId, String statusName) {
        FriendshipStatus status = statusStorage.findByName(statusName)
                .orElseThrow(() -> new NotFoundException("Friendship status '" + statusName + "' not found"));
        int statusId = status.getId();
        String sql = "UPDATE friendships SET status_id = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, statusId, userId, friendId);
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
            FriendshipStatus status = statusStorage.findByName(statusFilter)
                    .orElseThrow(() -> new NotFoundException("Friendship status '" + statusFilter + "' not found"));
            int statusId = status.getId();
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
        return count > 0;
    }
}