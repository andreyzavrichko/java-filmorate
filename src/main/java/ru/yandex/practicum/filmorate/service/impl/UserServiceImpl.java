package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    @Override
    public User create(User user) {
        return userStorage.add(user);
    }

    @Override
    public User update(User user) {
        if (userStorage.getById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        return userStorage.update(user);
    }

    @Override
    public List<User> findAll() {
        return userStorage.getAll();
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }

        findById(userId);
        findById(friendId);

        if (!friendshipStorage.exists(userId, friendId)) {
            friendshipStorage.add(userId, friendId, "UNCONFIRMED");
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        findById(userId);
        findById(friendId);

        friendshipStorage.delete(userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        findById(userId);

        List<Integer> friendIds = friendshipStorage.getFriends(userId, null);
        if (friendIds.isEmpty()) {
            return List.of();
        }

        return userStorage.getByIds(friendIds);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        findById(userId);
        findById(otherId);

        Set<Integer> userFriends = new HashSet<>(friendshipStorage.getFriends(userId, null));
        Set<Integer> otherFriends = new HashSet<>(friendshipStorage.getFriends(otherId, null));

        userFriends.retainAll(otherFriends);

        if (userFriends.isEmpty()) {
            return List.of();
        }

        return userStorage.getByIds(userFriends.stream().toList());
    }

    @Override
    public User findById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
