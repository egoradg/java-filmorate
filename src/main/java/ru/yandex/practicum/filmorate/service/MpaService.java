package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage storage;

    public MpaService(MpaStorage storage) {
        this.storage = storage;
    }

    public List<Mpa> getAll() {
        return storage.getAll();
    }

    public Mpa getById(Long id) {
        return storage.getById(id);
    }
}
