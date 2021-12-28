package main.server.messages;

import lombok.Data;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Data
public class FileMessage implements IMessage {

    private String path;
    private byte[] fileByte;
    private String name;
    private LocalDateTime time;

    public FileMessage(Path path, byte[] fileByte, String name, LocalDateTime time) {
        this.path = path.toString();
        this.fileByte = fileByte;
        this.name = name;
        this.time = time;
    }

    public FileMessage(String path, byte[] fileByte, String name) {
        this.path = path;
        this.fileByte = fileByte;
        this.name = name;
        this.time = LocalDateTime.now();
    }

    public FileMessage(Path path, byte[] fileByte, String name) {
        this.path = path.toString();
        this.fileByte = fileByte;
        this.name = name;
        this.time = LocalDateTime.now();
    }
}
