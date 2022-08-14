package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Genre {
    private final int id;
    private final String name;
}
