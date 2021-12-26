package main.client.guiController;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.client.ClientApp;
import main.client.guiController.helperClass.GuiScene;
import main.server.messages.StringMessage;

public class AuthClient implements GuiScene {
    public TextField outputLogin;
    public PasswordField outputPassword;
    public Label info;
    public Button loginBtn;

    public void actionLogin(ActionEvent actionEvent){
        StringMessage message =
                new StringMessage(
                        String.format("auth %s %s", outputLogin.getText().trim(), outputPassword.getText().trim())
                );
        ClientApp.getClientApp().sendMessage(message);
    }

    @Override
    public void setInfoText(String text) {
        info.setText(text);
    }

}
