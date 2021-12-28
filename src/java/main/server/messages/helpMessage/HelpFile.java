package main.server.messages.helpMessage;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HelpFile implements Serializable {
    String Name;
    Boolean isFile;
}
