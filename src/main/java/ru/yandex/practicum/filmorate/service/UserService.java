package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.getById(user.getId())
                .map(u -> userStorage.update(user))
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с id " + user.getId() + " не найден"));
    }

    public List<User> findAll() {
        return userStorage.getAll();
    }

    public User findById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public void addFriend(int userId, int friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(int userId) {
        User user = findById(userId);
        return user.getFriends().stream()
                .map(this::findById)
                .toList();
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = findById(userId);
        User other = findById(otherId);
        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(this::findById)
                .toList();
    }
}
