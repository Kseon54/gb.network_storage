package main.server.db;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
public class User {
    private static User[] users = new User[]{
            new User(1L, "user1", "pass"),
            new User(2L, "user2", "pass")
    };

    Long id;
    String login;
    String password;

    public static Optional<User> findUserByLoginAndPassword(String login, String password) {
        List<User> users = Arrays.stream(User.users)
                .filter(u -> u.getLogin().equals(login) && u.getPassword().equals(password))
                .collect(Collectors.toList());
        for (User user : users) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
