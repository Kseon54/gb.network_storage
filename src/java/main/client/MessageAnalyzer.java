package main.client;

import lombok.extern.slf4j.Slf4j;
import main.server.messages.AuthMessage;
import main.server.messages.IMessage;

import java.io.IOException;

@Slf4j
public final class MessageAnalyzer {

    private MessageAnalyzer() {
    }

    public static void messageAnalyzer(IMessage message){
        if (message instanceof AuthMessage) authClient((AuthMessage)message);
    }


    private static void authClient(AuthMessage message) {
        try {
            ClientApp.getClientApp().auth(message.getUser(),message.getPath());
        } catch (IOException e) {
            log.error("e=", e);
        }
    }
}
