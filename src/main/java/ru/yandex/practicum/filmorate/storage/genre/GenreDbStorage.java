package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> getGenres() {
        System.out.println("try");
        List<Genre> g =
                findMany(GenreSQL.FIND_ALL_QUERY);
        System.out.println(g);
        return g;
    }

    @Override
    public Genre getGenre(long id) {
        return findOne(GenreSQL.FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Жанр с id: " + id + " не найден"));
    }
}
