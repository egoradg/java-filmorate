package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("filmDb")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private final MpaService mpaService;
    private final GenreService genreService;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, MpaService mpaService, GenreService genreService) {
        super(jdbc, mapper);
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = findMany(FilmSQL.FIND_ALL_QUERY);
        films.forEach(f -> f.setGenre(loadGenres(f.getId())));
        return films;
    }

    private List<Genre> loadGenres(Long filmId) {
        return jdbc.queryForList(FilmSQL.FIND_GENRES_BY_FILM, Long.class, filmId).stream()
                .map(genreService::getById)
                .toList();
    }

    private void addGenres(long film_id, long genre_id) {
        try {
            jdbc.queryForObject(
                    "SELECT name FROM genre WHERE id = ?",
                    String.class,
                    genre_id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("жанра с id: " + genre_id + " нет");
        }
        jdbc.update(FilmSQL.ADD_GENRE, film_id, genre_id);
    }

    @Override
    public Film findById(Long id) {
        Film film = findOne(FilmSQL.FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + id + " не найден"));
        film.setGenre(loadGenres(film.getId()));
        return film;
    }

    @Override
    public boolean containsFilm(Long id) {
        findById(id);
        return true;
    }

    @Override
    public Film addFilm(Film film) {
        long id;
        if (film.getRating() != null) {
            try {
                jdbc.queryForObject(
                        "SELECT name FROM ratingMPA WHERE id = ?",
                        String.class,
                        film.getRating().getId().toString()
                );
            } catch (EmptyResultDataAccessException e) {
                throw new NotFoundException("рейтинга с id: " + film.getRating().getId().toString() + " нет");
            }
            id = insert(
                    FilmSQL.INSERT_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getRating().getId()
            );
        } else {
            id = insert(
                    FilmSQL.INSERT_QUERY_WITHOUT_RATING,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration());
        }
        film.setId(id);
        insertGenres(film);
        return film;
    }

    private void insertGenres(Film film) {
        if (film.getGenre() == null || film.getGenre().isEmpty()) return;
        Set<Long> ids = film.getGenre().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        ids.forEach(genre -> addGenres(film.getId(), genre));
    }

    @Override
    public void deleteFilm(Long filmId) {
        delete(FilmSQL.DELETE_QUERY, filmId);
        delete(FilmSQL.DELETE_GENRES, filmId);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(
                FilmSQL.UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getRating().getId(),
                newFilm.getId()
        );
        if (newFilm.getGenre() != null && !newFilm.getGenre().isEmpty()) {
            delete(FilmSQL.DELETE_GENRES, newFilm.getId());
            insertGenres(newFilm);
        }
        return newFilm;
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        List<Film> films = findMany(FilmSQL.FIND_POPULAR, count);
        films.forEach(f -> f.setGenre(loadGenres(f.getId())));
        return films;
    }

    @Override
    public void clear() {
        genreService.clear();
        clear("films");
    }

    public Long addLike(long filmId, long userId) {
        jdbc.update(FilmSQL.ADD_LIKE, filmId, userId);
        Long count = jdbc.queryForObject(FilmSQL.COUNT_LIKES, Long.class, filmId);
        return count == null ? 0L : count;
    }

    @Override
    public Long deleteLike(long filmId, long userId) {
        delete(FilmSQL.DELETE_LIKE, filmId, userId);
        Long count = jdbc.queryForObject(FilmSQL.COUNT_LIKES, Long.class, filmId);
        return count == null ? 0L : count;
    }
}