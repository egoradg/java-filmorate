package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
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
    public List<FilmDto> findAll() {
        return service.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable Long id) {
        return FilmMapper.mapToFilmDto(service.findById(id));
    }

    @PostMapping
    public FilmDto addFilm(@Valid @RequestBody NewFilmRequest request) {
        return FilmMapper.mapToFilmDto(service.addFilm(request));
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody UpdateFilmRequest request) {
        return FilmMapper.mapToFilmDto(service.updateFilm(request.getId(), request));
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        service.deleteFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        service.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(required = false) Long count) {
        return service.getPopularFilms(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public void clear() {
        service.clear();
    }
}
