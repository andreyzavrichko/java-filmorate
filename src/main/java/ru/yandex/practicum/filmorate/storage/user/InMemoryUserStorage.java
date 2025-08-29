package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private int idCounter = 1;

    @Override
    public User add(User user) {
        if (emails.contains(user.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует: " + user.getEmail());
        }
        user.setId(idCounter++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NoSuchElementException("Пользователь с id " + user.getId() + " не найден");
        }

        Optional<User> conflict = users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()) && u.getId() != user.getId())
                .findFirst();

        if (conflict.isPresent()) {
            throw new IllegalArgumentException("Email уже используется другим пользователем: " + user.getEmail());
        }

        String oldEmail = users.get(user.getId()).getEmail();
        if (!oldEmail.equals(user.getEmail())) {
            emails.remove(oldEmail);
            emails.add(user.getEmail());
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
