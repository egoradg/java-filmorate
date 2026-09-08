package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public List<User> findAll() {
        return storage.findAll();
    }

    public User findById(Long id) {
        if(storage.containsUser(id))
            return storage.findById(id);
        printException("Пользователя с id = " + id + " нет");
        return null;
    }

    public User addUser(User user) {
        log.info("Попытка добавить нового пользователя");

        if (user.getLogin().contains(" ")) {
            printException("Логин не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        storage.addUser(user);
        log.info("Добавлен новый пользователь");

        return user;
    }

    public User updateUser(User newUser) {
        log.info("Попытка изменить пользователя");

        if (newUser.getId() == null) {
            printException("Id не может быть пустым");
        }
        if (!storage.containsUser(newUser.getId())) {
            printException("Пользователя с id = " + newUser.getId() + " нет");
        }

        if (newUser.getEmail() != null) {
            if (newUser.getEmail().isBlank()) {
                newUser.setEmail(null);
            } else if (!newUser.getEmail().contains("@")) {
                printException("Имейл должен содержать символ '@'");
            }
        }

        if (newUser.getLogin() != null) {
            if (newUser.getLogin().isBlank()) {
                newUser.setLogin(null);
            } else if (newUser.getLogin().contains(" ")) {
                printException("Логин не может содержать пробелы");
            }
        }

        if (newUser.getName().isBlank()) {
            newUser.setName(null);
        }

        if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(LocalDate.now())) {
            printException("дата рождения не может быть в будущем");
        }

        User updatedUser = storage.updateUser(newUser);
        log.info("Данные пользователя успешно изменены");
        return updatedUser;
    }

    public void deleteUser(Long id) {
        storage.deleteUser(id);
    }

    public void clear(){
        storage.clear();
    }

    private void printException(String message) throws ValidationException {
        log.warn(message);
        throw new ValidationException(message);
    }
}
