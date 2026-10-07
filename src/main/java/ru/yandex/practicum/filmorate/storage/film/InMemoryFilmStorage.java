package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("memoryFilm")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public List<Film> findAll() {
        return films.values().stream().toList();
    }

    @Override
    public Film findById(Long id) {
        return films.get(id);
    }

    @Override
    public boolean containsFilm(Long id) {
        return films.containsKey(id);
    }

    @Override
    public void clear() {
        films.clear();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        return films.put(film.getId(), film);
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        return oldFilm;
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        if (count > films.size())
            count = (long) films.size();
        return films.values().stream()
                .sorted((o1, o2) -> {
                    if (o1.getLikes() == o2.getLikes())
                        return 0;
                    return o1.getLikes() > o2.getLikes() ? -1 : 1;
                })
                .limit(count)
                .toList();
    }

    private Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Long addLike(long filmId, long userId) {
        return null;
    }

    @Override
    public Long deleteLike(long filmId, long userId) {
        return null;
    }
}
