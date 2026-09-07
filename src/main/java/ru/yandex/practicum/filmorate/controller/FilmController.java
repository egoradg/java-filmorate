package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LocalDateAdapter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public String findAll() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        return gson.toJson(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Попытка добавить новый фильм");

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            printException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм");

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("Попытка изменить фильм");
        if (newFilm.getId() == null) {
            printException("Id не может быть пустым");
        }
        if (!films.containsKey(newFilm.getId())) {
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

        films.put(newFilm.getId(), newFilm);

        log.info("Данные фильма успешно изменены");
        return newFilm;
    }

    private Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void printException(String message) throws ValidationException {
        log.warn(message);
        throw new ValidationException(message);
    }

    public void clear() {
        films.clear();
    }
}
