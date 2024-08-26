package caidanci;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

public class HelloController {

  private final SqlTools sqlTools = new SqlTools();
  /**
   * 主题 0 默认 1 暗黑
   */
  private int themeFlag = 0;
  /**
   * 单词长度
   */
  private int wordLength = 5;
  private int inputTime = 0;
  private boolean gameIsStart = false;
  private String answerWord = "12345";
  private int rowIndex = 0;
  private List<String> wordList = null;
  @FXML
  private Button changeTheme;
  @FXML
  private Button info;
  @FXML
  private Button inputButton;
  @FXML
  private TextField inputTextFiled;
  @FXML
  private Spinner<Integer> levelChose;
  @FXML
  private GridPane outputGrid;
  @FXML
  private Button startGame;
  @FXML
  private Label testText;

  /**
   * 切换主题
   */
  @FXML
  void changeTheme() {
    if (themeFlag == 1) {
      Application.setUserAgentStylesheet(
          Objects.requireNonNull(this.getClass().getResource("theme/primer-light.css"))
              .toExternalForm());
      changeTheme.setGraphic(new FontIcon(BootstrapIcons.SUN));
      themeFlag = 0;
    } else {
      Application.setUserAgentStylesheet(
          Objects.requireNonNull(this.getClass().getResource("theme/primer-dark.css"))
              .toExternalForm());
      changeTheme.setGraphic(new FontIcon(BootstrapIcons.MOON));
      themeFlag = 1;
    }
  }

  /**
   * 处理输入框的键盘释放事件
   */
  @FXML
  void inputFiledKeyRelease(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      inputButtonClicked();
    }
  }

  /**
   * 处理输入按钮点击事件
   */
  @FXML
  void inputButtonClicked() {
    String inputText = inputTextFiled.getText();
    if (inputText != null && !inputText.isEmpty()) {
      if (inputText.length() == wordLength) {
        if (checkWord(inputText)) {
          if (inputTime == 0) {
            showAlert(Alert.AlertType.INFORMATION, "不，你失败了！", "你耗尽了机会！",
                "答案：" + answerWord + " :\n" + sqlTools.getWordInfo(answerWord));
            startGame();
          } else {
            inputTime--;
            if (wordList.contains(inputText)) {
              showAlert(Alert.AlertType.WARNING, "不", "你已经猜过这个单词了！",
                  inputText + " :\n" + sqlTools.getWordInfo(inputText));
              inputTextFiled.clear();
            } else {
              wordList.add(inputText);
              refresh(inputText);
              if (inputText.equals(answerWord)) {
                showAlert(Alert.AlertType.INFORMATION, "胜利！", "恭喜你，成功猜出单词！",
                    answerWord + " :\n" + sqlTools.getWordInfo(answerWord));
                startGame();
              }
            }
          }
        } else {
          showAlert(Alert.AlertType.ERROR, "错误！", "输入的单词错误！", "应该输入一个正确的单词");
        }
      } else {
        showAlert(Alert.AlertType.WARNING, "错误！", "输入格式错误！",
            "你应该输入一个单词，且该单词长度为" + wordLength + "！");
      }
    }
    inputTextFiled.clear();
  }

  /**
   * 刷新显示
   */
  private void refresh(String inputText) {
    int charSize = 40; // 每个字符的大小
    outputGrid.setAlignment(Pos.CENTER); // 居中对齐

    // 将字符串分割成字符数组，并在每个格子中添加一个标签
    char[] chars = inputText.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      String temp = String.valueOf(chars[i]);
      Label label = new Label(temp);
      label.setPrefSize(charSize, charSize);
      label.setAlignment(Pos.CENTER); // 居中对齐
      label.setBackground(
          new Background(new BackgroundFill(getColor(temp, i), CornerRadii.EMPTY, null)));
      label.setStyle("-fx-border-color: black; -fx-border-width: 1px;"); // 设置边框样式
      outputGrid.add(label, i, rowIndex); // 放置在第一行的不同列
    }
    rowIndex++;
    inputTimeUpdate();
  }

  /**
   * 检查单词是否合法
   *
   * @return 是否合法
   */
  private boolean checkWord(String word) {
    return sqlTools.checkWord(word);
  }

  /**
   * 初始化方法
   */
  @FXML
  void initialize() {
    Font a = Font.loadFont(
        Objects.requireNonNull(this.getClass()
            .getResourceAsStream("fonts/HeavyData/HeavyDataNerdFontPropo-Regular.ttf")), 20);
    System.out.println(a.getFamily());
    testText.setFont(Font.font(a.getFamily()));
    changeTheme.setGraphic(new FontIcon(BootstrapIcons.SUN));
    info.setGraphic(new FontIcon(BootstrapIcons.INFO_CIRCLE));
    startGame.setText("开始游戏");
    levelChose.setDisable(false);
    inputButton.setDisable(true);
    inputTextFiled.setDisable(true);
    gameIsStart = false;

    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
        3, 10, wordLength);
    levelChose.setValueFactory(valueFactory);
    levelChose.valueProperty().addListener((observable, oldValue, newValue) -> {
      wordLength = newValue;
      inputTimeUpdate();
    });
    inputTimeUpdate();
  }

  /**
   * 更新输入次数
   */
  private void inputTimeUpdate() {
    inputTime = wordLength - rowIndex + 1;
    inputButton.setText("输入 " + inputTime);
  }

  /**
   * 获取颜色
   */
  private Color getColor(String letter, int index) {
    if (answerWord.contains(letter)) {
      if (answerWord.indexOf(letter, index - 1) == index) {
        return Color.GREEN;
      } else {
        return Color.YELLOW;
      }
    } else {
      return Color.GRAY;
    }
  }

  /**
   * 处理输入按钮触摸事件
   */
  @FXML
  void inputButtonTouched() {
    inputButtonClicked();
  }

  /**
   * 处理信息按钮点击事件
   */
  @FXML
  void infoButtonClicked() {
    showAlert(Alert.AlertType.INFORMATION, "游戏说明", "游戏说明", """
        答案为指定长度单词，输入对应长度单词即可;
        绿色代表此单词中有此字母且位置正确;
        黄色代表此单词中有此字母，但该字母所处位置不对
        灰色代表此单词中没有此字母
        一共有单词长度+1次机会，机会用尽前猜中即为胜利
        """);
  }

  /**
   * 开始游戏
   */
  @FXML
  void startGame() {
    if (gameIsStart) {
      startGame.setText("开始游戏");
      levelChose.setDisable(false);
      inputButton.setDisable(true);
      inputTextFiled.setDisable(true);
      gameIsStart = false;
      reset();
    } else {
      answerUpdate();
      startGame.setText("结束游戏");
      levelChose.setDisable(true);
      inputButton.setDisable(false);
      inputTextFiled.setDisable(false);
      gameIsStart = true;
      wordList = new ArrayList<>();
    }
    inputTimeUpdate();
  }

  /**
   * 重置游戏
   */
  private void reset() {
    answerUpdate();
    rowIndex = 0;
    outputGrid.getChildren().clear();
  }

  /**
   * 更新答案
   */
  private void answerUpdate() {
    answerWord = sqlTools.getAnswer(wordLength);
  }

  /**
   * 显示提示框
   */
  private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
