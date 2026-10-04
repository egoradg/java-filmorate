package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@Data
@Slf4j
public class UpdateFilmRequest {
    @NotNull
    private Long id;
    @NotBlank
    private String name;

    @Size(max = 200)
    @NotBlank
    private String description;

    private LocalDate releaseDate;

    @Positive
    private Long duration;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasReleaseDate() {
        if (releaseDate != null && releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn(("Дата релиза — не раньше 28 декабря 1895 года"));
            throw new ValidationException(("Дата релиза — не раньше 28 декабря 1895 года"));
        }
        return true;
    }

    public boolean hasDuration() {
        return duration != null;
    }
}
