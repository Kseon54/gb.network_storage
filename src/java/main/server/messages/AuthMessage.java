package main.server.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.server.db.User;

import java.nio.file.Path;

@Data
@AllArgsConstructor
public class AuthMessage implements IMessage {

    private User user;
    private Path path;

}
