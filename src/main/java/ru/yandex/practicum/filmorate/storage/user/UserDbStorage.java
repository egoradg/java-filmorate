package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.stream.Collectors;

// для входа в консоль http://localhost:8080/h2-console
@Repository("userDb")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        List<User> users = findMany(UserSQL.FIND_ALL_QUERY);
        users.forEach(u -> u.setFriends(
                        findFriends(u.getId()).stream()
                                .map(User::getId)
                                .collect(Collectors.toSet())
                )
        );
        return users;
    }

    @Override
    public User findById(Long userId) {
        User user = findOne(UserSQL.FIND_BY_ID_QUERY, userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        user.setFriends(findFriends(user.getId()).stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return user;
    }

    @Override
    public boolean containsUser(Long id) {
        findById(id);
        return true;
    }

    @Override
    public User addUser(User user) {
        long id = insert(
                UserSQL.INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(
                UserSQL.UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getId()
        );
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (!delete(UserSQL.DELETE_QUERY, id))
            throw new NotFoundException("Пользователя с id: " + id + " не существует");
    }

    @Override
    public List<User> findFriends(Long id) {
        List<User> users = findMany(UserSQL.FIND_FRIENDS_QUERY, id);
        users.forEach(u -> u.setFriends(
                        findFriends(u.getId()).stream()
                                .map(User::getId)
                                .collect(Collectors.toSet())
                )
        );
        return users;
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        System.out.println("storage");
        jdbc.update(UserSQL.INSERT_FRIEND_QUERY, userId, friendId);
        return true;
    }

    @Override
    public boolean removeFriend(Long id, Long friendId) {
        System.out.println("storage");
        return delete(UserSQL.DELETE_FRIEND_QUERY, id, friendId);
    }

    @Override
    public void clear() {
        clear("users");
    }
}
