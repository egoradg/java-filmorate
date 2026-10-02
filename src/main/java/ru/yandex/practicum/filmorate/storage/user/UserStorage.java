package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User findById(Long id);

    List<User> findFriends(Long id);

    boolean containsUser(final Long id);

    User addUser(final User user);

    void deleteUser(final Long id);

    User updateUser(final User newUser);

    void clear();
}
