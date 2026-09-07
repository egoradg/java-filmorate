package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public boolean containsUser(Long id) {
        return users.containsKey(id);
    }

    @Override
    public void addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
    }

    @Override
    public User updateUser(User newUser) {
        User oldUser = users.get(newUser.getId());
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }
        return oldUser;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public void clear() {
        users.clear();
    }

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
