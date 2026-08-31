package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmsTests {
    @LocalServerPort
    private int port;

    private String baseUrl;

    private static HttpClient client;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    private static final Film filmOfNulls =Film.builder()
            .id(null)
            .name(null)
            .description(null)
            .releaseDate(null)
            .duration(null)
            .build();

    @Autowired
    private FilmController filmController;

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    @BeforeEach
    void beforeEach(){
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void afterEach(){
        filmController.clear();
    }

    @Test
    void testGetFilmsEmptyMap() throws IOException, InterruptedException {
        HttpRequest req1 = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/films"))
                .GET()
                .build();

        HttpResponse<String> resp1 =
                client.send(req1, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, resp1.statusCode(), "GET /films должен вернуть 200");

        List<Film> films = gson.fromJson(resp1.body(), new ListOfFilmsTypeToken().getType());

        assertTrue(films.isEmpty());
    }

    @Test
    void testAddNewFilm() throws IOException, InterruptedException {
        Film film = Film.builder()
                .id(1L)
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .build();
        Film responseFilm = addFilm(film);
        assertEquals(film, responseFilm);

        List<Film> films = getFilms();
        assertEquals(film, films.getFirst());
    }

    @Test
    void testAddFilmsWithBadName() throws IOException, InterruptedException {
        //название не может быть пустым
        Film film = Film.builder()
                .id(1L)
                .name("")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .build();
        Film responseFilm = addFilm(film);
        assertEquals(filmOfNulls, responseFilm);

        film = Film.builder()
                .id(1L)
                .name(null)
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .build();
        responseFilm = addFilm(film);
        assertEquals(filmOfNulls, responseFilm);

        List<Film> films = getFilms();
        assertEquals(0, films.size());
    }

    @Test
    void testAddFilmsWithBadDescription() throws IOException, InterruptedException {
        //максимальная длина описания — 200 символов;
        Film film = Film.builder()
                .id(1L)
                .name("Star wars")
                .description("*".repeat(201))
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .build();
        Film responseFilm = addFilm(film);
        assertEquals(filmOfNulls, responseFilm);

        film = Film.builder()
                .id(1L)
                .name("Star wars")
                .description("")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .build();
        responseFilm = addFilm(film);
        assertEquals(filmOfNulls, responseFilm);

        film = Film.builder()
                .id(1L)
                .name("Star wars")
                .description(null)
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(120L)
                .build();
        responseFilm = addFilm(film);
        assertEquals(filmOfNulls, responseFilm);

        List<Film> films = getFilms();
        assertEquals(0, films.size());
    }

    @Test
    void testAddFilmsWithBadReleaseDate() throws IOException, InterruptedException {
        //дата релиза — не раньше 28 декабря 1895 года
        Film film = Film.builder()
                .id(1L)
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(120L)
                .build();
        Film responseFilm = addFilm(film);
        assertEquals(filmOfNulls, responseFilm);

        List<Film> films = getFilms();
        assertEquals(0, films.size());
    }

    @Test
    void testAddFilmsWithBadDuration() throws IOException, InterruptedException {
        //название не может быть пустым
        Film film = Film.builder()
                .id(1L)
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(-120L)
                .build();
        Film responseFilm = addFilm(film);
        assertEquals(filmOfNulls, responseFilm);

        film = Film.builder()
                .id(1L)
                .name("Star wars")
                .description("Film about space wars")
                .releaseDate(LocalDate.of(1997, 1, 12))
                .duration(null)
                .build();
        responseFilm = addFilm(film);
        assertEquals(filmOfNulls, responseFilm);

        List<Film> films = getFilms();
        assertEquals(0, films.size());
    }

    private List<Film> getFilms() throws IOException, InterruptedException {
        String baseUrl = "http://localhost:" + port;
        HttpRequest req1 = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/films"))
                .GET()
                .build();

        HttpResponse<String> resp1 =
                client.send(req1, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return gson.fromJson(resp1.body(), new ListOfFilmsTypeToken().getType());
    }

    private Film addFilm(Film film) throws IOException, InterruptedException {
        HttpRequest create = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/films"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(film), StandardCharsets.UTF_8))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();
        HttpResponse<String> response =
                client.send(create, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return gson.fromJson(response.body(), Film.class);
    }
}
