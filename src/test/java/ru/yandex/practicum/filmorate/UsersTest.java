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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersTest {
    @LocalServerPort
    private int port;

    private String baseUrl;

    private static HttpClient client;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    private static final User userOfNulls = User.builder()
            .id(null)
            .email(null)
            .login(null)
            .name(null)
            .birthday(null)
            .build();

    @Autowired
    private UserController userController;

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
        userController.clear();
    }

    @Test
    void testGetUsersEmptyMap() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .GET()
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, resp.statusCode(), "GET /users должен вернуть 200");

        List<User> users = gson.fromJson(resp.body(), new ListOfUsersTypeToken().getType());

        assertTrue(users.isEmpty());
    }

    @Test
    void testAddNewUser() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("qwe@asd.ru")
                .login("qwerty")
                .name("egor")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User responseUser = addUser(user);
        assertEquals(user, responseUser);

        List<User> users = getUsers();
        assertEquals(user, users.getFirst());
    }

    @Test
    void testAddFilmsWithBadEmail() throws IOException, InterruptedException {
        //электронная почта должна быть корректной
        User user = User.builder()
                .id(1L)
                .email("")
                .login("qwerty")
                .name("egor")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User responseUser = addUser(user);
        assertEquals(userOfNulls, responseUser);

        user = User.builder()
                .id(1L)
                .email(null)
                .login("qwerty")
                .name("egor")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        responseUser = addUser(user);
        assertEquals(userOfNulls, responseUser);

        user = User.builder()
                .id(1L)
                .email("qwe")
                .login("qwerty")
                .name("egor")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        responseUser = addUser(user);
        assertEquals(userOfNulls, responseUser);

        List<User> users = getUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void testAddFilmsWithBadLogin() throws IOException, InterruptedException {
        //логин не может быть пустым и содержать пробелы
        User user = User.builder()
                .id(1L)
                .email("qwe@asd.ru")
                .login("")
                .name("egor")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User responseUser = addUser(user);
        assertEquals(userOfNulls, responseUser);

        user = User.builder()
                .id(1L)
                .email("qwe@asd.ru")
                .login(null)
                .name("egor")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        responseUser = addUser(user);
        assertEquals(userOfNulls, responseUser);

        List<User> users = getUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void testAddFilmsWithEmptyName() throws IOException, InterruptedException {
        //имя для отображения может быть пустым — в таком случае будет использован логин
        User user = User.builder()
                .id(1L)
                .email("qwe@asd.ru")
                .login("qwerty")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User responseUser = addUser(user);
        user.setName(user.getLogin());
        assertEquals(user, responseUser);

        user = User.builder()
                .id(1L)
                .email("qwe@asd.ru")
                .login("qwerty")
                .name(null)
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        responseUser = addUser(user);
        user.setName(user.getLogin());
        assertEquals(userOfNulls, responseUser);

        List<User> users = getUsers();
        assertEquals(2, users.size());
    }

    @Test
    void testAddFilmsWithBadBirthday() throws IOException, InterruptedException {
        //дата рождения не может быть в будущем
        User user = User.builder()
                .id(1L)
                .email("qwe@asd.ru")
                .login("qwerty")
                .name("egor")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        User responseUser = addUser(user);
        assertEquals(userOfNulls, responseUser);

        List<User> users = getUsers();
        assertTrue(users.isEmpty());
    }

    private List<User> getUsers() throws IOException, InterruptedException {
        String baseUrl = "http://localhost:" + port;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .GET()
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return gson.fromJson(resp.body(), new ListOfUsersTypeToken().getType());
    }

    private User addUser(User user) throws IOException, InterruptedException {
        HttpRequest create = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user), StandardCharsets.UTF_8))
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();
        HttpResponse<String> response =
                client.send(create, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return gson.fromJson(response.body(), User.class);
    }
}
