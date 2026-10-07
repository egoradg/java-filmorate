package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserStorageTests {
    private final UserDbStorage storage;

    @BeforeEach
    public void beforeEach(){
        storage.clear();
    }

    @Test
    public void testFindUsers() {

        List<User> users = storage.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    public void testFindUserById() {
        User user = User.builder()
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();
        user = storage.addUser(user);


        User userInStorage = storage.findById(1L);

        assertEquals(user, userInStorage);
    }

    @Test
    public void testAddUser() {
        User user = User.builder()
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();
        User userInStorage = storage.addUser(user);

        assertEquals(user, userInStorage);
    }

    @Test
    public void testUpdateUser() {
        User user = User.builder()
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();
        user = storage.addUser(user);

        User userToUpdate = User.builder()
                .id(user.getId())
                .email("asd@asd.com")
                .login("asd")
                .name("asd")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        user = storage.updateUser(userToUpdate);

        assertEquals(userToUpdate, user);
    }

    @Test
    public void testDeleteUser() {
        User user = User.builder()
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();
        user = storage.addUser(user);
        List<User> users = storage.findAll();

        assertEquals(user, users.getFirst());
        assertEquals(1, users.size());

        storage.deleteUser(user.getId());

        users = storage.findAll();

        assertEquals(0, users.size());
    }

    @Test
    public void testFindFriendsUser() {
        User user1 = User.builder()
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        User user2 = User.builder()
                .email("asd@qwe.com")
                .login("asd")
                .name("asd")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        User user3 = User.builder()
                .email("zxc@qwe.com")
                .login("zxc")
                .name("zxc")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        User user4 = User.builder()
                .email("qaz@qwe.com")
                .login("qaz")
                .name("qaz")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        storage.addUser(user1);
        storage.addUser(user2);
        storage.addUser(user3);
        storage.addUser(user4);

        storage.addFriend(user1.getId(), user2.getId());
        storage.addFriend(user1.getId(), user3.getId());

        List<User> friends = storage.findFriends(user1.getId());

        assertEquals(2, friends.size());
        assertTrue(friends.contains(user2));
        assertTrue(friends.contains(user3));

        friends = storage.findFriends(user2.getId());
        assertEquals(0, friends.size());

        friends = storage.findFriends(user3.getId());
        assertEquals(0, friends.size());
    }

    @Test
    public void testAddFriendsUser() {
        User user1 = User.builder()
                .id(1L)
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        User user2 = User.builder()
                .id(1L)
                .email("asd@qwe.com")
                .login("asd")
                .name("asd")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        storage.addUser(user1);
        storage.addUser(user2);

        storage.addFriend(user1.getId(), user2.getId());

        List<User> friends = storage.findFriends(user1.getId());

        assertEquals(1, friends.size());
        assertTrue(friends.contains(user2));

        friends = storage.findFriends(user2.getId());
        assertEquals(0, friends.size());
    }

    @Test
    public void testDeleteFriendsUser() {
        User user1 = User.builder()
                .id(1L)
                .email("qwe@qwe.com")
                .login("qwe")
                .name("qwe")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        User user2 = User.builder()
                .id(1L)
                .email("asd@qwe.com")
                .login("asd")
                .name("asd")
                .birthday(LocalDate.of(2022, 12, 12))
                .build();

        storage.addUser(user1);
        storage.addUser(user2);

        storage.addFriend(user1.getId(), user2.getId());

        storage.removeFriend(user1.getId(), user2.getId());

        List<User> friends = storage.findFriends(user1.getId());
        assertEquals(0, friends.size());

        friends = storage.findFriends(user2.getId());
        assertEquals(0, friends.size());
    }
}