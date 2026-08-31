package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
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
