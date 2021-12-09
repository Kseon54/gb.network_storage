package main.server.messages;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class StringMessage extends AbstractMessage {

    private String content;
    private LocalDateTime time;

}
