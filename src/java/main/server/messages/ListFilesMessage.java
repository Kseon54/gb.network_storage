package main.server.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.server.messages.helpMessage.HelpFile;

import java.util.List;

@Data
@AllArgsConstructor
public class ListFilesMessage implements IMessage{

    private List<HelpFile> listFiles;
}

