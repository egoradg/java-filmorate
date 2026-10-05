package ru.yandex.practicum.filmorate.storage.genre;

public class GenreSQL {
    public final static String FIND_ALL_QUERY = "SELECT * FROM genre";
    public final static String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
}
