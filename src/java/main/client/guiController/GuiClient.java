package main.client.guiController;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import main.client.ClientApp;
import main.client.guiController.helperClass.GuiScene;
import main.server.comands.CommandType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;

public class GuiClient implements GuiScene, Initializable {
    public Label info;
    public ListView<String> listView;
    public Label path;

    private String selectItem;

    public static GuiClient guiClient;

    private final ClientApp clientApp;

    public GuiClient() {
        clientApp = ClientApp.getClientApp();
        guiClient = this;
    }

    @Override
    public void setInfoText(String text) {
        info.setText(text);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.DELETE) actionButtonDelete(null);
        if (e.getCode() == KeyCode.UP) actionButtonUp(null);
    }

    public void actionExit(ActionEvent actionEvent) {
        ClientApp.getClientApp().close();
    }

    public void actionButtonMkdir(ActionEvent actionEvent) {
        clientApp.sendActionMessage(CommandType.MKDIR, "Новая папка");
        selectItem = null;
    }

    public void actionButtonDelete(ActionEvent actionEvent) {
        if (selectItem == null) return;
        String fileName = selectItem.substring(3);
        boolean isDelete = askQuestion("Удаление", "Вы точно хотите удалить элемент?", fileName);
        if (isDelete) clientApp.sendActionMessage(CommandType.DELETE, fileName);
        selectItem = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clientApp.getFiles();

        listView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 1) {
                selectItem = listView.getSelectionModel().getSelectedItem();
            }

            if (click.getClickCount() == 2) {
                String itemSelected = listView.getSelectionModel()
                        .getSelectedItem();
                if (itemSelected.charAt(1) == 'd') {
                    ClientApp.getClientApp().enterCatalog(itemSelected.substring(3));
                    clientApp.getFiles();
                }
                if (itemSelected.charAt(1) == 'f') {
                    actionButtonDownland(null);
                }
            }
        });
    }

    public void actionButtonUp(ActionEvent actionEvent) {
        ClientApp.getClientApp().pathUp();
        selectItem = null;
    }

    public void setPathText(String path) {
        this.path.setText(path);
    }

    private boolean askQuestion(String title, String question, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(question);
        alert.setContentText(contentText);

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yes, no);
        Optional<ButtonType> option = alert.showAndWait();
        return option.filter(buttonType -> buttonType == yes).isPresent();
    }

    public void actionButtonRename(ActionEvent actionEvent) {
        if (selectItem == null) return;
        String fileName = selectItem.substring(3);
        Optional<String> newName =
                askQuestionWithTextPanel("Преименновать", "Введите новое имя файла", null, fileName);
        if (newName.isPresent())
            if (!newName.get().equals(""))
                clientApp.sendActionMessage(CommandType.RENAME, fileName, newName.get());
        selectItem = null;
    }

    private Optional<String> askQuestionWithTextPanel(String title, String question, String contentText, String inputText) {
        TextInputDialog alert = new TextInputDialog(inputText);
        alert.setTitle(title);
        alert.setHeaderText(question);
        alert.setContentText(contentText);

        return alert.showAndWait();
    }

    private Optional<File> openFileChooser() {
        FileChooser alert = new FileChooser();
        File file = alert.showOpenDialog(ClientApp.getClientApp().getPrimaryStage());
        if (file != null) return Optional.of(file);
        return Optional.empty();
    }


    public void actionButtonUpland(ActionEvent actionEvent) {
        Optional<File> file = openFileChooser();
        if (file.isPresent()) {
            try {
                clientApp.sendFileMessage(Files.readAllBytes(file.get().toPath()), file.get().getName());
            } catch (IOException e) {
                clientApp.setInfoText("Ошибка загрузки файла");
            }
        }
    }

    public void actionButtonDownland(ActionEvent actionEvent) {
        if (selectItem == null) return;
        String fileName = selectItem.substring(3);
        clientApp.sendActionMessage(CommandType.DOWNLAND, fileName);
        clientApp.setWaitingFile(true);
        selectItem = null;
    }
}
