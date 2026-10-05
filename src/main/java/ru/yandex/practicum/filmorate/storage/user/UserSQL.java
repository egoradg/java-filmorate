package ru.yandex.practicum.filmorate.storage.user;

public class UserSQL {
    public static final String FIND_ALL_QUERY = "SELECT * FROM users";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    public static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ? WHERE id = ?";
    public static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    public static final String FIND_FRIENDS_QUERY = """
            SELECT * FROM users
            WHERE id IN(
                SELECT user2_id
                FROM friends
                WHERE user1_id = ?
                )
            """;
    public static final String FIND_COMMONS_FRIENDS_QUERY = """
            SELECT * FROM users
            WHERE
            -- Получение списка id друзей 1 пользователя
            id IN(
                SELECT user2_id
                FROM users
                WHERE user1_id = ? --id1
                )
            AND
            -- Получение списка id друзей 2 пользователя
            id IN(
                SELECT user2_id
                FROM users
                WHERE user1_id = ? --id2
                )
            """;
    public static final String INSERT_FRIEND_QUERY = "INSERT INTO friends (user1_id, user2_id)" +
            "VALUES (?, ?)";
    public static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE user1_id = ? AND user2_id = ?";
}
