package main.client.guiController.helperClass;

import javafx.scene.input.KeyEvent;

public interface GuiScene {
    void setInfoText(String text);

    void keyPressed(KeyEvent e);
}
