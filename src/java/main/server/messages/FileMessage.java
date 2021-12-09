package main.server.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class FileMessage extends AbstractMessage{

    private Path path;
    private File file;
    private LocalDateTime time;

}
