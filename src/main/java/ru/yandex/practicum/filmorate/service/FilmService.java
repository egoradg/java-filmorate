package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;

    public FilmService(InMemoryFilmStorage storage) {
        this.storage = storage;
    }

    public List<Film> findAll() {
        return storage.findAll();
    }

    public Film addFilm(Film film) {
        log.info("Попытка добавить новый фильм");

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            printException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        storage.addFilm(film);
        log.info("Добавлен новый фильм");
        return film;
    }

    public Film updateFilm(Film newFilm) {
        log.info("Попытка изменить фильм");
        if (newFilm.getId() == null) {
            printException("Id не может быть пустым");
        }
        if (!storage.containsFilm(newFilm.getId())) {
            printException("Фильма с id = " + newFilm.getId() + " нет");
        }

        if (newFilm.getName().isBlank()) {
            newFilm.setName(null);
        }

        if (newFilm.getDescription() != null) {
            if (newFilm.getDescription().isBlank()) {
                newFilm.setDescription(null);
            }
            if (newFilm.getDescription().length() > 200) {
                printException("Максимальная длина описания — 200 символов");
            }
        }

        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            printException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (newFilm.getDuration() != null && newFilm.getDuration() < 0) {
            printException("Продолжительность фильма должна быть положительным числом");
        }

        Film updatedFilm = storage.updateFilm(newFilm);

        log.info("Данные фильма успешно изменены");
        return updatedFilm;
    }

    public void clear(){
        storage.clear();
    }

    private void printException(String message) throws ValidationException {
        log.warn(message);
        throw new ValidationException(message);
    }

    public void deleteFilm(Long id) {
        storage.deleteFilm(id);
    }
}
