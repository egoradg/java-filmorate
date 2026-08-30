package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

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
}
