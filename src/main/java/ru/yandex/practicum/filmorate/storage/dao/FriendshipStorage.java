package ru.yandex.practicum.filmorate.storage.dao;

import java.util.List;

public interface FriendshipStorage {
    void add(int userId, int friendId, String status);

    void update(int userId, int friendId, String status);

    void delete(int userId, int friendId);

    List<Integer> getFriends(int userId, String statusFilter);

    boolean exists(int userId, int friendId);
}

