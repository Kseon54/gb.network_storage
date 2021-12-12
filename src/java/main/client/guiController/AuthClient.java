package main.client.guiController;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.client.ClientApp;
import main.server.messages.StringMessage;

import java.io.IOException;
import java.time.LocalDateTime;

public class AuthClient {
    public TextField outputLogin;
    public PasswordField outputPassword;
    public Label info;

    public void actionLogin(ActionEvent actionEvent){
        StringMessage message =
                new StringMessage(
                        String.format("auth %s %s", outputLogin.getText(), outputPassword.getText()),
                        LocalDateTime.now()
                );
        ClientApp.getClientApp().sendMessage(message);
    }

    public void setTextLabelInfo(String text){

    }
}
