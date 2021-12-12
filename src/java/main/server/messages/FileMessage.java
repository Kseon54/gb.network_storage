package main.server.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileMessage implements IMessage {

    private Path path;
    private byte[] fileByte;
    private String name;
    private LocalDateTime time;

}
