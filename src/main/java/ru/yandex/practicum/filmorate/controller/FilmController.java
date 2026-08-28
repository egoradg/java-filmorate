package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll(){
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film){
        if(film.getName().isEmpty() || film.getName().isBlank()){
            throw new ValidationException("Название не может быть пустым");
        }

        if(film.getDescription().length()>200){
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))){
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if(film.getDuration().toMinutes()<0){
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        film.setId(getNextId());

        films.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm){

        if(newFilm.getId()==null){
            throw new ValidationException("Id не может быть пустым");
        }

        if(films.containsKey(newFilm.getId())){
            Film oldFilm = films.get(newFilm.getId());
            if(!newFilm.getName().isEmpty() && !newFilm.getName().isBlank()){
                oldFilm.setName(newFilm.getName());
            }

            if(!newFilm.getDescription().isEmpty() && !newFilm.getDescription().isBlank()){
                if(newFilm.getDescription().length()>200){
                    throw new ValidationException("Максимальная длина описания — 200 символов");
                }
                oldFilm.setDescription(newFilm.getDescription());
            }

            if(newFilm.getReleaseDate()!=null){
                if(newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))){
                    throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
                }
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }

            if(newFilm.getDuration()!=null){
                if(newFilm.getDuration().toMinutes()<0){
                    throw new ValidationException("Продолжительность фильма должна быть положительным числом");
                }
                oldFilm.setDuration(newFilm.getDuration());
            }
            return oldFilm;
        }

        throw new ValidationException("Фильма с id = " + newFilm.getId() + " нет");
    }

    private Long getNextId(){
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
