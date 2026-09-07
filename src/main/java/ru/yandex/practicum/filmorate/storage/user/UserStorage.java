package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    User addUser(final User user);
    void deleteUser(final Long id);
    User updateUser(final User newUser);
}
