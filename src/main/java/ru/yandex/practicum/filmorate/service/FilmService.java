package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;

    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDb") FilmStorage storage, @Qualifier("userDb") UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return storage.findAll();
    }

    public Film findById(Long id) {
        if (storage.containsFilm(id))
            return storage.findById(id);
        printNotFoundException("Фильма с id = " + id + " нет");
        return null;
    }

    public Film addFilm(NewFilmRequest request) {
        log.info("Попытка добавить новый фильм");

        if (request.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            printException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        Film film = FilmMapper.mapToFilm(request);
        film = storage.addFilm(film);
        log.info("Добавлен новый фильм");
        if (!storage.containsFilm(film.getId())) {
            printNotFoundException("Фильма с id = " + film.getId() + " нет");
        } else System.out.println("Фильм с id = " + film.getId() + " есть");
        return film;
    }

    public Film updateFilm(Long id, UpdateFilmRequest request) {
        log.info("Попытка изменить фильм");
        if (id == null) {
            printException("Id не может быть пустым");
        }
        if (!storage.containsFilm(id)) {
            printNotFoundException("Фильма с id = " + id + " нет");
        }

        Film updatedFilm = FilmMapper.updateFilmFields(storage.findById(id), request);
        updatedFilm = storage.updateFilm(updatedFilm);

        log.info("Данные фильма успешно изменены");
        return updatedFilm;
    }

    public void clear() {
        storage.clear();
    }

    private void printException(String message) throws ValidationException {
        log.warn(message);
        throw new ValidationException(message);
    }

    private void printNotFoundException(String message) throws NotFoundException {
        log.warn(message);
        throw new NotFoundException(message);
    }

    public void deleteFilm(Long id) {
        storage.deleteFilm(id);
    }

    public void likeFilm(Long id, Long userId) {
        if (!storage.containsFilm(id)) {
            printNotFoundException("Фильма с id = " + id + " нет");
        }

        if (!userStorage.containsUser(userId))
            printNotFoundException("Пользователя с id = " + id + " нет");

        Film film = storage.findById(id);
        Long likes = storage.addLike(id, userId);
        if (likes != null) {
            film.setLikes(likes);
            log.info("Лайк поставлен");
        } else
            printException("Вы уже поставили лайк этому фильму");
    }

    public void deleteLike(Long id, Long userId) {
        if (!storage.containsFilm(id))
            printNotFoundException("Фильма с id = " + id + " нет");

        if (!userStorage.containsUser(userId))
            printNotFoundException("Пользователя с id = " + id + " нет");

        Film film = storage.findById(id);
        Long likes = storage.deleteLike(id, userId);
        if (likes != null) {
            film.setLikes(likes);
            log.info("Лайк удалён");
        } else
            printException("Вы не ставили лайк этому фильму");
    }

    public List<Film> getPopularFilms(Long count) {
        log.info("Попытка получить популярные фильмы");
        if (count == null)
            count = 10L;
        if (count <= 0) {
            printException("Параметр count должен быть больше нуля");
        }
        return storage.getPopularFilms(count);
    }
}
