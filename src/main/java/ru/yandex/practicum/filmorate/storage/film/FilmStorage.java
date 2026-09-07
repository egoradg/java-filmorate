package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();
    boolean containsFilm(final Long id);
    void addFilm(final Film film);
    void deleteFilm(final Long id);
    void updateFilm(final Film newFilm);
    void clear();
}
