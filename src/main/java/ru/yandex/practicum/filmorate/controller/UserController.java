package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return service.findCommonFriends(id, otherId);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        return service.updateUser(newUser);
    }

    @GetMapping("/{id}/friends")
    List<User> findFriends(@PathVariable Long id) {
        return service.findFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }

    public void clear() {
        service.clear();
    }
}
