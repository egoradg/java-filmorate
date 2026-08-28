package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Имейл должен содержать символ '@'");
        }

        if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }

        user.setId(getNextId());

        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateFilm(@RequestBody User newUser) {

        if (newUser.getId() == null) {
            throw new ValidationException("Id не может быть пустым");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (!newUser.getEmail().isEmpty() && !newUser.getEmail().isBlank()) {
                if (!newUser.getEmail().contains("@")) {
                    throw new ValidationException("Имейл должен содержать символ '@'");
                }
                oldUser.setName(newUser.getName());
            }

            if (!newUser.getLogin().isEmpty() && !newUser.getLogin().isBlank()) {
                if (newUser.getEmail().contains(" ")) {
                    throw new ValidationException("Логин не может содержать пробелы");
                }
                oldUser.setLogin(newUser.getLogin());
            }

            if (newUser.getBirthday() != null) {
                if (newUser.getBirthday().isAfter(LocalDate.now())) {
                    throw new ValidationException("дата рождения не может быть в будущем");
                }
                oldUser.setBirthday(newUser.getBirthday());
            }
            return oldUser;
        }

        throw new ValidationException("Пользователя с id = " + newUser.getId() + " нет");
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
