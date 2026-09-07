package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

//@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (!newFilm.getName().isEmpty() && !newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
            }

            if (!newFilm.getDescription().isEmpty() && !newFilm.getDescription().isBlank()) {
                if (newFilm.getDescription().length() > 200) {
                    //printException("Максимальная длина описания — 200 символов");
                }
                oldFilm.setDescription(newFilm.getDescription());
            }

            if (newFilm.getReleaseDate() != null) {
                if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                    //printException("Дата релиза — не раньше 28 декабря 1895 года");
                }
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }

            if (newFilm.getDuration() != null) {
                if (newFilm.getDuration() < 0) {
                    //printException("Продолжительность фильма должна быть положительным числом");
                }
                oldFilm.setDuration(newFilm.getDuration());
            }

            //log.info("Данные фильма успешно изменены");
            return oldFilm;
        }
        //log.warn("Фильма с id = " + newFilm.getId() + " нет");
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
}
