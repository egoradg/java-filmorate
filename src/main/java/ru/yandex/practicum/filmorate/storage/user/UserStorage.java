package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();
    boolean containsUser(final Long id);
    void addUser(final User user);
    void deleteUser(final Long id);
    User updateUser(final User newUser);
    void clear();
}
