package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmDbStorage.class,
        FilmRowMapper.class,
        MpaService.class,
        MpaDbStorage.class,
        MpaRowMapper.class,
        GenreService.class,
        GenreDbStorage.class,
        GenreRowMapper.class,
        UserDbStorage.class,
        UserRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTests {
    private final FilmDbStorage storage;
    private final UserDbStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        storage.clear();
    }

    @Test
    public void testFindFilms() {

        List<Film> films = storage.findAll();

        assertTrue(films.isEmpty());
    }

    @Test
    public void testFindFilmById() {
        Film film = Film.builder()
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .likes(0L)
                .genre(new ArrayList<>())
                .build();
        film = storage.addFilm(film);


        Film filmInStorage = storage.findById(1L);

        assertEquals(film, filmInStorage);
    }

    @Test
    public void testAddFilm() {
        Film film = Film.builder()
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .likes(0L)
                .genre(new ArrayList<>())
                .build();
        Film filmInStorage = storage.addFilm(film);

        assertEquals(film, filmInStorage);
    }

    @Test
    public void testUpdateFilm() {
        Film film = Film.builder()
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .likes(0L)
                .genre(new ArrayList<>())
                .build();
        film = storage.addFilm(film);

        Film filmToUpdate = Film.builder()
                .id(film.getId())
                .name("Star wars 2")
                .description("Continue of film about space wars")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(140L)
                .likes(0L)
                .genre(new ArrayList<>())
                .rating(new Mpa(2L, null))
                .build();

        film = storage.updateFilm(filmToUpdate);

        assertEquals(filmToUpdate, film);
    }

    @Test
    public void testDeleteFilm() {
        Film film = Film.builder()
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .likes(0L)
                .genre(new ArrayList<>())
                .build();
        film = storage.addFilm(film);
        List<Film> films = storage.findAll();

        assertEquals(film, films.getFirst());
        assertEquals(1, films.size());

        storage.deleteFilm(film.getId());

        films = storage.findAll();

        assertEquals(0, films.size());
    }

    @Test
    public void testAddLike() {
        User user = User.builder()
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();
        user = userStorage.addUser(user);

        Film film = Film.builder()
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .likes(0L)
                .genre(new ArrayList<>())
                .build();

        storage.addFilm(film);

        long likes = storage.addLike(film.getId(), user.getId());
        film = storage.findById(film.getId());

        assertEquals(1, likes);
        assertEquals(1, film.getLikes());
    }

    @Test
    public void testDeleteLike() {
        User user = User.builder()
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();
        user = userStorage.addUser(user);

        Film film = Film.builder()
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .likes(0L)
                .genre(new ArrayList<>())
                .build();

        storage.addFilm(film);

        long likes = storage.addLike(film.getId(), user.getId());
        assertEquals(1, likes);

        likes = storage.deleteLike(film.getId(), user.getId());
        assertEquals(0, likes);

        film = storage.findById(film.getId());
        assertEquals(0, film.getLikes());
    }

    @Test
    public void testFindPopular() {
        User user1 = User.builder()
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        User user2 = User.builder()
                .email("asd@qwe.com")
                .login("asd")
                .name("asd")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        User user3 = User.builder()
                .email("zxc@qwe.com")
                .login("zxc")
                .name("zxc")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        user1 = userStorage.addUser(user1);
        user2 = userStorage.addUser(user2);
        user3 = userStorage.addUser(user3);

        Film film1 = Film.builder()
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .likes(0L)
                .genre(new ArrayList<>())
                .build();

        Film film2 = Film.builder()
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .likes(0L)
                .genre(new ArrayList<>())
                .build();

        Film film3 = Film.builder()
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .likes(0L)
                .genre(new ArrayList<>())
                .build();

        storage.addFilm(film1);
        storage.addFilm(film2);
        storage.addFilm(film3);

        long likes = storage.addLike(film1.getId(), user1.getId());
        film1.setLikes(1L);
        assertEquals(1, likes);

        storage.addLike(film2.getId(), user1.getId());
        storage.addLike(film2.getId(), user2.getId());
        likes = storage.addLike(film2.getId(), user3.getId());
        film2.setLikes(3L);
        assertEquals(3, likes);

        storage.addLike(film3.getId(), user1.getId());
        likes = storage.addLike(film3.getId(), user2.getId());
        film3.setLikes(2L);
        assertEquals(2, likes);

        List<Film> popularFilms = storage.getPopularFilms(3L);
        assertEquals(film2, popularFilms.getFirst());
        assertEquals(film3, popularFilms.get(1));
        assertEquals(film1, popularFilms.getLast());
    }
}
