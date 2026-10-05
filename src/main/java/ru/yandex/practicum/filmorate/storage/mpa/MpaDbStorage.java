package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {
    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Mpa> getAll() {
        return findMany(MpaSQL.FIND_ALL_QUERY);
    }

    @Override
    public Mpa getById(Long id) {
        return findOne(MpaSQL.FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Жанр с id: " + id + " не найден"));
    }
}
