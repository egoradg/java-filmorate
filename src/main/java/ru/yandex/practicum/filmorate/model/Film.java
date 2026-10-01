package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.model.enums.Rating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data

public class Film {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @Size(max = 200)
    @NotNull
    @NotBlank
    private String description;

    private LocalDate releaseDate;

    @Positive
    @NotNull
    private Long duration;

    private Set<Long> likes;

    private Genre genre;

    private Rating rating;

    @Builder
    public Film(Long id, String name, String description, LocalDate releaseDate, Long duration, Genre genre, Rating rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
        this.genre = genre;
        this.rating = rating;
    }
}
