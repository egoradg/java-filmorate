package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film findById(Long id);

    boolean containsFilm(final Long id);

    Film addFilm(final Film film);

    void deleteFilm(final Long id);

    Film updateFilm(final Film newFilm);

    List<Film> getPopularFilms(Long count);

    void clear();
}
