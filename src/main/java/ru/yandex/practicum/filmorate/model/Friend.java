package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.FriendsStatus;

@Data
public class Friend {
    private final long friendId;
    private FriendsStatus status;

    public Friend(long friendId, FriendsStatus status) {
        this.friendId = friendId;
        this.status = status;
    }
}
