package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("Попытка добавить нового пользователя");
        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            printException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            printException("Имейл должен содержать символ '@'");
        }

        if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            printException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            printException("Логин не может содержать пробелы");
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            printException("Дата рождения не может быть в будущем");
        }

        user.setId(getNextId());

        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь");

        return user;
    }

    @PutMapping
    public User updateFilm(@RequestBody User newUser) {
        log.info("Попытка изменить пользователя");
        if (newUser.getId() == null) {
            printException("Id не может быть пустым");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (!newUser.getEmail().isEmpty() && !newUser.getEmail().isBlank()) {
                if (!newUser.getEmail().contains("@")) {
                    printException("Имейл должен содержать символ '@'");
                }
                oldUser.setName(newUser.getName());
            }

            if (!newUser.getLogin().isEmpty() && !newUser.getLogin().isBlank()) {
                if (newUser.getEmail().contains(" ")) {
                    printException("Логин не может содержать пробелы");
                }
                oldUser.setLogin(newUser.getLogin());
            }

            if (newUser.getBirthday() != null) {
                if (newUser.getBirthday().isAfter(LocalDate.now())) {
                    printException("дата рождения не может быть в будущем");
                }
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("Данные пользователя успешно изменены");
            return oldUser;
        }

        log.warn("Пользователя с id = " + newUser.getId() + " нет");
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

    private void printException(String message) throws ValidationException {
        log.warn(message);
        throw new ValidationException(message);
    }
}
