package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage storage;

    public UserService(@Qualifier("userDb") UserStorage storage) {
        this.storage = storage;
    }

    public List<UserDto> findAll() {
        return storage.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    public UserDto findById(Long id) {
        return UserMapper.mapToUserDto(storage.findById(id));
    }

    public UserDto addUser(NewUserRequest request) {
        log.info("Попытка добавить нового пользователя");

        if (request.getLogin().contains(" ")) {
            printException("Логин не может содержать пробелы");
        }

        if (request.getName() == null || request.getName().isEmpty()) {
            request.setName(request.getLogin());
        }

        User user = UserMapper.mapToUser(request);
        user = storage.addUser(user);
        log.info("Добавлен новый пользователь");

        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        log.info("Попытка изменить пользователя");
        checkContainsUser(request.getId());

        if (request.getLogin() != null && request.getLogin().contains(" ")) {
            printException("Логин не может содержать пробелы");
        }

        User updatedUser = UserMapper.updateUserFields(storage.findById(request.getId()), request);
        updatedUser = storage.updateUser(updatedUser);
        log.info("Данные пользователя успешно изменены");
        return UserMapper.mapToUserDto(updatedUser);
    }


    public void deleteUser(Long id) {
        checkContainsUser(id);
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

    private void checkContainsUser(Long id) throws NotFoundException {
        storage.containsUser(id);
    }

    public List<UserDto> findFriends(Long id) {
        checkContainsUser(id);
        log.info("Попытка найти друзей");
        return storage.findFriends(id).stream()
                .map(this::findById)
                .toList();
    }

    public List<UserDto> findCommonFriends(Long id, Long otherId) {
        log.info("Попытка найти одинаковых друзей");

        checkContainsUser(id);
        checkContainsUser(otherId);

        User user1 = storage.findById(id);
        User user2 = storage.findById(otherId);
        return getCommonIds(
                user1.getFriends(),
                user2.getFriends()
        ).stream()
                .map(storage::findById)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    private List<Long> getCommonIds(Set<Long> friends1, Set<Long> friends2) {
        return friends1.stream()
                .filter(friends2::contains)
                .toList();
    }

    public void addFriend(Long id, Long friendId) {
        log.info("Попытка добавить друга");
        if (id == friendId) {
            printException("Нельзя добавить себя в друзья");
        }
        checkContainsUser(id);
        checkContainsUser(friendId);
        User user = storage.findById(id);
        if (user.getFriends().add(friendId) && storage.addFriend(id, friendId)) {
            log.info("Друг успешно добавлен");
        } else {
            printException("Вы уже являетесь друзьями");
        }
    }

    public void deleteFriend(Long id, Long friendId) {
        checkContainsUser(id);
        checkContainsUser(friendId);

        User user = storage.findById(id);

        if (user.getFriends().remove(friendId) && storage.removeFriend(id, friendId)) {
            log.info("Пользователь удалён из друзей");
        } else {
            log.warn("Вы и так не были друзьями");
        }
    }
}
