package main.server.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.server.comands.CommandType;

import java.nio.file.Path;

@Data
@AllArgsConstructor
public class ActionMessage implements IMessage{

    private CommandType type;
    private String path;
    private String name;

    public ActionMessage(CommandType type, Path path, String name) {
        this.type = type;
        this.path = path.toString();
        this.name = name;
    }
}
