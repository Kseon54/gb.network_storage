package main.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import main.client.guiController.GuiClient;
import main.client.guiController.helperClass.GuiScene;
import main.server.comands.CommandType;
import main.server.db.User;
import main.server.messages.ActionMessage;
import main.server.messages.FileMessage;
import main.server.messages.IMessage;
import main.server.messages.ListFilesMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@Slf4j
@Data
public class ClientApp extends Application{

    private NettyNet net;
    private Stage primaryStage;
    private GuiScene guiScene;

    private User user;
    private String thisPath;
    private Path mainPath;
    private boolean isAuth;
    private boolean isWaitingFile;

    private static ClientApp clientApp;

    public ClientApp() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        clientApp = this;
        this.net = new NettyNet(MessageAnalyzer::messageAnalyzer);
        this.primaryStage = primaryStage;

        initApp();
        loadScene("authClient.fxml");
        primaryStage.show();
    }

    private void initApp() {
        primaryStage.setTitle("Network storage");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(we -> close());
    }

    private EventHandler<KeyEvent> createEventHandler(){
        return event -> guiScene.keyPressed(event);
    }


    public void auth(User user, Path path) throws IOException {
        this.user = user;
        this.thisPath = "";
        this.mainPath = path;
        this.isAuth = true;
        loadScene("guiClient.fxml");
    }

    public void pathUp() {
        if (!thisPath.equals("")) {
            int i = thisPath.lastIndexOf(File.separator);
            if (i == -1) {
                setThisPath("");
            } else {
                setThisPath(thisPath.substring(0, i));
            }
            getFiles();
        }
    }

    public void enterCatalog(String catalog) {
        setThisPath(thisPath + File.separator + catalog);
        getFiles();
    }

    public Path getAbsolutePath() {
        return Paths.get(mainPath + (thisPath.equals("") ? "" : File.separator + thisPath));
    }

    private void loadScene(String nameScene) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(nameScene)));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        scene.setOnKeyPressed(createEventHandler());
        Platform.runLater(() -> primaryStage.setScene(scene));
        guiScene = loader.getController();
    }

    public static ClientApp getClientApp() {
        return clientApp;
    }

    public void setInfoText(String text) {
        Platform.runLater(() -> guiScene.setInfoText(text));
    }

    public void getFiles() {
        sendMessage(new ActionMessage(CommandType.GET_FILES, getAbsolutePath(), null));
    }

    public void sendMessage(IMessage message) {
        net.sendMessage(message);
        setInfoText("");
    }

    public void sendActionMessage(CommandType commandType, String s) {
        sendMessage(new ActionMessage(commandType, getAbsolutePath(), s));
        getFiles();
    }

    public void sendActionMessage(CommandType commandType, String addPath, String s) {
        sendMessage(new ActionMessage(commandType, getAbsolutePath() + File.separator + addPath, s));
        getFiles();
    }

    public void sendFileMessage(byte[] fileByte, String name) {
        FileMessage message = new FileMessage(
                getAbsolutePath(),
                fileByte,
                name
        );
        sendMessage(message);
        getFiles();
    }

    public static void setFileList(ListFilesMessage message) {
        Platform.runLater(() -> {
            GuiClient.guiClient.listView.getItems().clear();
            message.getListFiles().forEach(file ->
                    GuiClient.guiClient.listView.getItems().add((file.getIsFile() ? "[f]" : "[d]") + file.getName())
            );
        });
    }

    public void close() {
        net.close();
        Platform.exit();
    }

    public void setThisPath(String thisPath) {
        this.thisPath = thisPath;
        if (isAuth)
            if (thisPath.equals("")) {
                GuiClient.guiClient.setPathText(File.separator);
            } else GuiClient.guiClient.setPathText(thisPath);
    }

    public void downlandFile(FileMessage message) {
        if (isAuth && isWaitingFile) {
            setWaitingFile(false);
            Platform.runLater(() -> {
                Optional<File> file = getFileInstallationLocation(message.getName());
                if (file.isPresent()) {
                    try (FileOutputStream fos = new FileOutputStream(file.get())) {
                        fos.write(message.getFileByte());
                        setInfoText("Файл загружен");
                    } catch (IOException e) {
                        log.error("e=", e);
                        setInfoText("Ошибка размещения файла");
                    }
                }
            });
            getFiles();
        }
    }

    private Optional<File> getFileInstallationLocation(String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранение файла");
        fileChooser.setInitialFileName(fileName);
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) return Optional.of(file);
        return Optional.empty();
    }
}


