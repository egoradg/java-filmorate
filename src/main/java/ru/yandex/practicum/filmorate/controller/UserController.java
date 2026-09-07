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

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        return service.updateUser(newUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestParam Long id){
        service.deleteUser(id);
    }

    public void clear() {
        service.clear();
    }
}
