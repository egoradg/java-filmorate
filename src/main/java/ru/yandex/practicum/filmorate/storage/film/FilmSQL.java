package ru.yandex.practicum.filmorate.storage.film;

public class FilmSQL {
    public static final String FIND_ALL_QUERY = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   COUNT(l.user_id) AS likes,
                   r.id AS rating_id
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            JOIN ratingMPA r ON f.rating_id = r.id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.name
            """;
    public static final String FIND_BY_ID_QUERY = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   COUNT(l.user_id) AS likes,
                   r.id AS rating_id
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            JOIN ratingMPA r ON f.rating_id = r.id
            WHERE f.id = ?
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.name
            """;
    public static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    public static final String INSERT_QUERY_WITHOUT_RATING = "INSERT INTO films (name, description, release_date, duration) " +
            "VALUES (?, ?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id  = ? WHERE id = ?";
    public static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    public static final String FIND_POPULAR = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   COUNT(l.user_id) AS likes,
                   r.id AS rating_id
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            LEFT JOIN ratingMPA r ON f.rating_id = r.id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.name
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """;
    public static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    public static final String COUNT_LIKES = "SELECT COUNT(user_id) AS likes from likes WHERE film_id = ?";
    public static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    public static final String FIND_GENRES_BY_FILM = """
            SELECT genre_id
            FROM film_genre
            WHERE film_id = ?
            """;
    public static final String ADD_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    public static final String DELETE_GENRES = "DELETE FROM film_genre WHERE film_id = ?";
    public static final String SELECT_GENRE = "SELECT name FROM genre WHERE id = ?";
}
