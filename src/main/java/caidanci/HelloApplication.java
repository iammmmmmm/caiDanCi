package caidanci;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        launch(args); // 启动JavaFX应用程序
    }

    @Override
    public void start(Stage stage) throws IOException {
        //
        
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        var size = tools.screenAutoSize(440, 800);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, size[0], size[1]);
        stage.setScene(scene);
        stage.setTitle("Hello!");
        stage.setResizable(false);
        stage.show();
    }
}