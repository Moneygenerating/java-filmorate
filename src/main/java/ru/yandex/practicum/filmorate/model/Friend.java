package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(of= "friendId")
public class Friend {
    private final int friendId;
}
