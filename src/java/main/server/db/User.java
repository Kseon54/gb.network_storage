package main.server.db;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    public static User[] users = new User[]{
            new User("user1","pass"),
            new User("user2","pass")
    };

    String login;
    String password;
}
