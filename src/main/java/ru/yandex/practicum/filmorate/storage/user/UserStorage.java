package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    List<User> findAll();

    User findById(Long id);

    Set<Long> findFriends(Long id);

    boolean containsUser(final Long id);

    User addUser(final User user);

    void deleteUser(final Long id);

    User updateUser(final User newUser);

    void clear();

    boolean addFriend(Long id, Long friendId);

    boolean removeFriend(Long id, Long friendId);
}
