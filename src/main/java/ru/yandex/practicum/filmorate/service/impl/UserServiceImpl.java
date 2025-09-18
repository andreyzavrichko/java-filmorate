package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
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

        User user = findById(userId);
        User friend = findById(friendId);
        FriendshipStatus userToFriend = user.getFriends().get(friendId);
        FriendshipStatus friendToUser = friend.getFriends().get(userId);

        if (userToFriend == FriendshipStatus.UNCONFIRMED && friendToUser == FriendshipStatus.UNCONFIRMED) {
            user.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
            friend.getFriends().put(userId, FriendshipStatus.CONFIRMED);
            return;
        }
        if (friendToUser == FriendshipStatus.CONFIRMED && userToFriend == FriendshipStatus.CONFIRMED) {
            return;
        }

        user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);

        friend.getFriends().putIfAbsent(userId, FriendshipStatus.UNCONFIRMED);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = findById(userId);
        return user.getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(entry -> findById(entry.getKey()))
                .toList();
    }


    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        User user = findById(userId);
        User other = findById(otherId);

        var userConfirmed = user.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        return other.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .filter(userConfirmed::contains)
                .map(this::findById)
                .toList();
    }

    @Override
    public User findById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
