package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @NotNull(groups = UserValidationGroups.UpdateGroup.class)
    private Long id;

    @Email(groups = {UserValidationGroups.CreateGroup.class, UserValidationGroups.UpdateGroup.class})
    @NotNull(groups = UserValidationGroups.CreateGroup.class)
    @NotBlank(groups = {UserValidationGroups.CreateGroup.class, UserValidationGroups.UpdateGroup.class})
    private String email;

    @NotNull(groups = UserValidationGroups.CreateGroup.class)
    @NotBlank(groups = {UserValidationGroups.CreateGroup.class, UserValidationGroups.UpdateGroup.class})
    private String login;

    @NotBlank(groups = UserValidationGroups.UpdateGroup.class)
    private String name;

    @NotNull(groups = UserValidationGroups.CreateGroup.class)
    @PastOrPresent(groups = {UserValidationGroups.CreateGroup.class, UserValidationGroups.UpdateGroup.class})
    private LocalDate birthday;

    private Set<Long> friends;

    @Builder
    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }
}
