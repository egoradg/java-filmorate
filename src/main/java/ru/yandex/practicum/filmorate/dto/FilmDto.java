package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Id;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String name;

    private String description;

    private LocalDate releaseDate;

    private Long duration;

    private Long likes;

    private List<Id> genres;

    private Id mpa;
}
