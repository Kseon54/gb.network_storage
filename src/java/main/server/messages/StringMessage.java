package main.server.messages;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StringMessage implements IMessage {

    private String content;
    private LocalDateTime time;

}
