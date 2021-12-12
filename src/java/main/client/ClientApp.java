package main.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import main.server.db.User;
import main.server.messages.StringMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@Data
@Slf4j
public class ClientApp extends Application {
    private NettyNet net;
    private User user;
    private Path path;
    private boolean isAuth;
    private Stage primaryStage;

    private static ClientApp clientApp;

    public ClientApp() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        clientApp = this;
        this.net = new NettyNet(MessageAnalyzer::messageAnalyzer);

        primaryStage.setTitle("Network storage");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(we -> net.close());

        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("authClient.fxml")));
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

    public void auth(User user, Path path) throws IOException {
        this.user = user;
        this.path = path;
        this.isAuth = true;
        log.debug("test= {}", user);
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("guiClient.fxml")));
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

    public static ClientApp getClientApp() {
        return clientApp;
    }

    public void sendMessage(StringMessage message) {
        net.sendMessage(message);
    }
}
