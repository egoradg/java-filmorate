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

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (!newFilm.getName().isEmpty() && !newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
            }

            if (!newFilm.getDescription().isEmpty() && !newFilm.getDescription().isBlank()) {
                if (newFilm.getDescription().length() > 200) {
                    printException("Максимальная длина описания — 200 символов");
                }
                oldFilm.setDescription(newFilm.getDescription());
            }

            if (newFilm.getReleaseDate() != null) {
                if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                    printException("Дата релиза — не раньше 28 декабря 1895 года");
                }
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }

            if (newFilm.getDuration() != null) {
                if (newFilm.getDuration() < 0) {
                    printException("Продолжительность фильма должна быть положительным числом");
                }
                oldFilm.setDuration(newFilm.getDuration());
            }

            log.info("Данные фильма успешно изменены");
            return oldFilm;
        }
        log.warn("Фильма с id = " + newFilm.getId() + " нет");
        throw new ValidationException("Фильма с id = " + newFilm.getId() + " нет");
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

    public void clear(){
        films.clear();
    }
}
