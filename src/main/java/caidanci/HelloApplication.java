package caidanci;

import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class HelloApplication extends Application {

  public static void main(String[] args) {
    launch(args); // 启动JavaFX应用程序
  }

  @Override
  public void start(Stage stage) throws IOException {
    // 加载字体
    Font.loadFont(
        Objects.requireNonNull(this.getClass()
            .getResourceAsStream("fonts/BigBlueTerminal/BigBlueTerm437NerdFont-Regular.ttf")), 20);

    Application.setUserAgentStylesheet(
        Objects.requireNonNull(this.getClass().getResource("theme/primer-light.css"))
            .toExternalForm());
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
    var size = tools.screenAutoSize(330, 600);
    Parent root = fxmlLoader.load();
    Scene scene = new Scene(root, size[0], size[1]);
    // 加载CSS文件
    root.setStyle("-fx-font-family: HeavyData Nerd Font Propo;");
    stage.setTitle("Hello!");
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
  }
}