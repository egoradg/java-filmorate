package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

//@Service
public class FilmService {
    private final InMemoryFilmStorage storage;

    public FilmService(InMemoryFilmStorage storage) {
        this.storage = storage;
    }
}
