/**
 * Copyright (c) 2019 Gluon All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source code must retain the
 * above copyright notice, this list of conditions and the following disclaimer. * Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * * Neither the name of Gluon, any associated website, nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior written
 * permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

  /**
   * 主题 0 默认 1 暗黑
   */
  int themeFlag = 0;
  /**
   * 单词长度
   */
  int worldLength = 5;
  int inputTime = 0;
  boolean gameIsStart = false;
  String answerWorld = "12345";
  int rowIndex = 0;
  List<String> worldList = null;
  SqlTools sqlToos = new SqlTools();
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

  @FXML
  void changeTheme() {
//        System.out.println(themeFlag);
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

  @FXML
  void inputFiledKeyRelease(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      inputButtonClicked();
    }
  }

  @FXML
  void inputButtonClicked() {
    var inputText = "";
    if (inputTextFiled.getText() != null && !inputTextFiled.getText().isEmpty()) {
      inputText = inputTextFiled.getText();
      if (inputText.length() == worldLength) {
        if (checkWorld(inputText)) {
          if (inputTime == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("不，你失败了！");
            alert.setHeaderText("你耗尽了机会！");
            alert.setContentText("答案：" + answerWorld + " :\n" + sqlToos.getWordInfo(answerWorld));
            alert.showAndWait();
            startGame();
          } else {
            inputTime--;
            if (worldList.contains(inputText)) {
              Alert alert = new Alert(Alert.AlertType.WARNING);
              alert.setTitle("不");
              alert.setHeaderText("你已经猜过这个单词了！");
              alert.setContentText(inputText + " :\n" + sqlToos.getWordInfo(inputText));
              alert.showAndWait();
              inputTextFiled.clear();
            } else {
              worldList.add(inputText);
              refresh(inputText);
              if (inputText.equals(answerWorld)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("胜利！");
                alert.setHeaderText("恭喜你，成功猜出单词！");
                alert.setContentText(answerWorld + " :\n" + sqlToos.getWordInfo(answerWorld));
                alert.showAndWait();
                startGame();
              }
            }
          }
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("错误！");
          alert.setHeaderText("输入的单词错误！");
          alert.setContentText("应该输入一个正确的单词");
          alert.showAndWait();
        }
      } else {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("错误！");
        alert.setHeaderText("输入格式错误！");
        alert.setContentText("你应该输入一个单词，且该单词长度为" + worldLength + "！");
        alert.showAndWait();
      }
    }
    inputTextFiled.clear();
  }

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
  private boolean checkWorld(String word) {
    return sqlToos.checkWord(word);
  }

  @FXML
  void initialize() {
    var a = Font.loadFont(
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
        3, 10, worldLength);
    levelChose.setValueFactory(valueFactory);
    levelChose.valueProperty().addListener((observable, oldValue, newValue) -> {
      worldLength = newValue;
      inputTimeUpdate();
    });
    inputTimeUpdate();


  }

  private void inputTimeUpdate() {
    inputTime = worldLength - rowIndex + 1;
    inputButton.setText("输入" + " " + (inputTime));
  }

  // 获取随机颜色
  private Color getColor(String letter, int index) {
    Color color;
    if (answerWorld.contains(letter)) {
      if (answerWorld.indexOf(letter, index - 1) == index) {
        color = Color.GREEN;
      } else {
        color = Color.YELLOW;
      }
    } else {
      color = Color.GRAY;
    }
    return color;
  }

  @FXML
  void inputButtonTouched() {
    inputButtonClicked();
  }

  @FXML
  void infoButtonClicked() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("游戏说明");
    alert.setHeaderText("游戏说明");
    alert.setContentText("""
        答案为指定长度单词，输入对应长度单词即可;
        绿色代表此单词中有此字母且位置正确;
        黄色代表此单词中有此字母，但该字母所处位置不对
        灰色代表此单词中没有此字母
        一共有单词长度+1次机会，机会用尽前猜中即为胜利
        """);
    alert.showAndWait();
  }

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
      worldList = new ArrayList<>();
    }
    inputTimeUpdate();
  }

  private void reset() {
    answerUpdate();
    rowIndex = 0;
    outputGrid.getChildren().clear();
  }

  private void answerUpdate() {
    answerWorld = sqlToos.getAnswer(worldLength);
  }

}