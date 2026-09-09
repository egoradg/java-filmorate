package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
        if (storage.containsUser(id))
            return storage.findById(id);
        printNotFoundException("Пользователя с id = " + id + " нет");
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
            printNotFoundException("Пользователя с id = " + newUser.getId() + " нет");
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

    public void addFriend(Long id, Long friendId) {
        log.info("Попытка добавить друга");
        if (id == friendId) {
            printException("Нельзя добавить себя в друзья");
        }
        if (!storage.containsUser(id))
            printNotFoundException("Пользователя с id = " + id + " нет");
        if (!storage.containsUser(friendId))
            printNotFoundException("Пользователя с id = " + friendId + " нет");

        User user = storage.findById(id);
        User friend = storage.findById(friendId);
        if (user.getFriends().add(friendId) && friend.getFriends().add(id)) {
            log.info("Друг успешно добавлен");
        } else {
            printException("Вы уже являетесь друзьями");
        }
    }

    public void deleteFriend(Long id, Long friendId) {
        if (!storage.containsUser(id))
            printNotFoundException("Пользователя с id = " + id + " нет");
        if (!storage.containsUser(friendId))
            printNotFoundException("Пользователя с id = " + friendId + " нет");

        User user = storage.findById(id);
        User friend = storage.findById(friendId);
        if (user.getFriends().remove(friendId) && friend.getFriends().remove(id)) {
            log.info("Пользователь удалён из друзей");
        } else {
            log.warn("Вы и так не были друзьями");
        }
    }

    public void deleteUser(Long id) {
        if (!storage.containsUser(id))
            printNotFoundException("Пользователя с id = " + id + " нет");
        storage.deleteUser(id);
        log.info("Данные пользователя успешно удалены");
    }

    public void clear() {
        storage.clear();
    }

    private void printException(String message) throws ValidationException {
        log.warn(message);
        throw new ValidationException(message);
    }

    private void printNotFoundException(String message) throws NotFoundException {
        log.warn(message);
        throw new NotFoundException(message);
    }

    public List<User> findFriends(Long id) {
        if (!storage.containsUser(id))
            printNotFoundException("Пользователя с id = " + id + " нет");
        log.info("Попытка найти друзей");
        return storage.findFriends(id);
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        log.info("Попытка найти одинаковых друзей");
        if (!storage.containsUser(id))
            printNotFoundException("Пользователя с id = " + id + " нет");
        if (!storage.containsUser(otherId))
            printNotFoundException("Пользователя с id = " + otherId + " нет");
        User user1 = storage.findById(id);
        User user2 = storage.findById(otherId);
        return getCommonIds(user1.getFriends(), user2.getFriends())
                .stream()
                .map(storage::findById)
                .toList();
    }

    private List<Long> getCommonIds(Set<Long> friends1, Set<Long> friends2) {
        return friends1.stream()
                .filter(friends2::contains)
                .toList();
    }
}
