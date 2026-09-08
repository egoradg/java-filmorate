package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public List<Film> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return service.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        return service.updateFilm(newFilm);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id){
        service.deleteFilm(id);
    }

    public void clear() {
        service.clear();
    }
}
