package main.client;

import lombok.extern.slf4j.Slf4j;
import main.client.guiController.GuiClient;
import main.server.messages.*;

import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
public final class MessageAnalyzer {

    private MessageAnalyzer() {
    }

    public static void messageAnalyzer(IMessage message){
        if (message instanceof AuthMessage) authClient((AuthMessage)message);
        if (message instanceof StringMessage) sendInfoClientGui((StringMessage)message);
        if (message instanceof ListFilesMessage) ClientApp.setFileList((ListFilesMessage) message);
        if (message instanceof FileMessage) ClientApp.getClientApp().downlandFile((FileMessage)message);
    }

    private static void sendInfoClientGui(StringMessage message) {
        ClientApp.getClientApp().setInfoText(message.getContent());
    }


    private static void authClient(AuthMessage message) {
        try {
            ClientApp.getClientApp().auth(message.getUser(), Paths.get(message.getPath()));
        } catch (IOException e) {
            log.error("e=", e);
        }
    }
}
