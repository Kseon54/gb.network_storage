package main.server.messages;

import lombok.Data;
import main.server.db.User;

import java.nio.file.Path;

@Data
public class AuthMessage implements IMessage {

    private User user;
    private String path;

    public AuthMessage(User user, Path path) {
        this.user = user;
        this.path = path.toString();
    }

    public AuthMessage(User user, String path) {
        this.user = user;
        this.path = path;
    }
}
