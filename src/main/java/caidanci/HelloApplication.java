package caidanci;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

  public static void main(String[] args) {
    launch(args); // 启动JavaFX应用程序
  }

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
    var size = tools.screenAutoSize(330, 600);
    Parent root = fxmlLoader.load();
    Scene scene = new Scene(root, size[0], size[1]);
    // 加载CSS文件
    stage.setTitle("Hello!");
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
  }
}