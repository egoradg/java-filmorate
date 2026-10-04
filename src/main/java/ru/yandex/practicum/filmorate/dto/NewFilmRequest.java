package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Id;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewFilmRequest {
    @NotBlank
    private String name;

    @Size(max = 200)
    @NotBlank
    private String description;

    private LocalDate releaseDate;

    @Positive
    @NotNull
    private Long duration;

    private List<Id> genres;

    private Id mpa;
}
