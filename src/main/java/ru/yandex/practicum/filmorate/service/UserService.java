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
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendsStatus;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        log.info("Попытка изменить пользователя");
        checkContainsUser(id);

        if (request.getLogin() != null && request.getLogin().contains(" ")) {
            printException("Логин не может содержать пробелы");
        }

        User updatedUser = UserMapper.updateUserFields(storage.findById(id), request);
        updatedUser = storage.updateUser(updatedUser);
        log.info("Данные пользователя успешно изменены");
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void addFriend(Long id, Long friendId) {
        log.info("Попытка добавить друга");
        if (id == friendId) {
            printException("Нельзя добавить себя в друзья");
        }
        checkContainsUser(id);
        checkContainsUser(friendId);

        User user = storage.findById(id);
        User userFriend = storage.findById(friendId);
        Friend friend1 = new Friend(userFriend.getId(), FriendsStatus.UNCONFIRMED);
        Friend friend2 = new Friend(user.getId(), FriendsStatus.UNCONFIRMED);
        if (user.getFriends().add(friend1) && userFriend.getFriends().add(friend2)) {
            log.info("Друг успешно добавлен");
        } else {
            printException("Вы уже являетесь друзьями");
        }
    }

    public void deleteFriend(Long id, Long friendId) {
        checkContainsUser(id);
        checkContainsUser(friendId);

        User user = storage.findById(id);
        Friend friend1 = user.getFriends().stream()
                .filter(friend -> friend.getFriendId() == friendId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Вы и так не были друзьями"));

        User friend = storage.findById(friendId);
        Friend friend2 = user.getFriends().stream()
                .filter(fr -> fr.getFriendId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Вы и так не были друзьями"));

        if (user.getFriends().remove(friend1) && friend.getFriends().remove(friend2)) {
            log.info("Пользователь удалён из друзей");
        } else {
            log.warn("Вы и так не были друзьями");
        }
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
        if (!storage.containsUser(id)) {
            log.warn("Пользователя с id = " + id + " нет");
            throw new NotFoundException("Пользователя с id = " + id + " нет");
        }
    }

    public List<UserDto> findFriends(Long id) {
        checkContainsUser(id);
        log.info("Попытка найти друзей");
        return storage.findFriends(id).stream().map(UserMapper::mapToUserDto).toList();
    }

    public List<UserDto> findCommonFriends(Long id, Long otherId) {
        log.info("Попытка найти одинаковых друзей");

        checkContainsUser(id);
        checkContainsUser(otherId);

        User user1 = storage.findById(id);
        User user2 = storage.findById(otherId);
        return getCommonIds(
                user1.getFriends().stream()
                        .map(Friend::getFriendId)
                        .collect(Collectors.toSet()),
                user2.getFriends().stream()
                        .map(Friend::getFriendId)
                        .collect(Collectors.toSet())
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
}
