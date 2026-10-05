package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;

// для входа в консоль http://localhost:8080/h2-console
@Repository("userDb")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String FIND_FRIENDS_QUERY =
            "SELECT " +
                    "u.id, " +
                    "u.email, " +
                    "u.login, " +
                    "u.name, " +
                    "u.birthday " +
                    "FROM users u " +
                    "WHERE u.id IN(" +
                    "    SELECT user2_id" +
                    "    FROM friends" +
                    "    WHERE user1_id= ?" +
                    "    )" +
                    "OR u.id IN(" +
                    "    SELECT user1_id" +
                    "    FROM friends" +
                    "    WHERE user2_id=?" +
                    "    )";


    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User findById(Long userId) {
        return findOne(FIND_BY_ID_QUERY, userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
    }

    @Override
    public User addUser(User user) {
        long id = insert(
                INSERT_QUERY,
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
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getId()
        );
        return user;
    }


    @Override
    public List<User> findFriends(Long id) {
        return findMany(FIND_FRIENDS_QUERY, id, id);
    }

    @Override
    public boolean containsUser(Long id) {
        return findById(id) != null;
    }

    @Override
    public void deleteUser(Long id) {
        if (!delete(DELETE_QUERY, id))
            throw new NotFoundException("Пользователя с id: " + id + " не существует");
    }

    @Override
    public void clear() {
        clear("users");
    }
}
