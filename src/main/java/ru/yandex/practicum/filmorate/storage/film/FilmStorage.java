package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    Film addFilm(final Film film);
    void deleteFilm(final Long id);
    Film updateFilm(final Film newFilm);
}
