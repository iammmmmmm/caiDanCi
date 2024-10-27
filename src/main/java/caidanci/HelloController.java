package caidanci;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelloController {

    private final SqlTools sqlTools = new SqlTools();
    private final ThemeManager tm = ThemeManager.getInstance();
    String[] theme = {new PrimerLight().getUserAgentStylesheet(), new PrimerDark().getUserAgentStylesheet()};
    @FXML
    private Spinner<Integer> fontSize;
    /**
     * 主题 0 默认 1 暗黑
     */
    private int themeFlag = 1;
    /**
     * 单词长度
     */
    private int wordLength = 5;
    private String yellowWords = "";
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
    private ComboBox<String> fontChose;
    @FXML
    private Button showHint;

    /**
     * 切换主题
     */
    @FXML
    void changeTheme() {
        if (themeFlag == 1) {

            changeTheme.setGraphic(new FontIcon(BootstrapIcons.SUN));
            themeFlag = 0;
        } else {

            changeTheme.setGraphic(new FontIcon(BootstrapIcons.MOON));
            themeFlag = 1;
        }
        Application.setUserAgentStylesheet(theme[themeFlag]);
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
        String inputText = inputTextFiled.getText().toLowerCase();
        inputTextFiled.clear();
        inputCheck(inputText);

    }

    private void inputCheck(String inputText) {
        if (inputText.isEmpty()) {
            return;
        }
        if (inputText.length() != wordLength) {
            showAlert(Alert.AlertType.WARNING, "错误！", "输入格式错误！", "你应该输入一个单词，且该单词长度为" + wordLength + "！");
            return;
        }
        if (!checkWord(inputText)) {
            showAlert(Alert.AlertType.ERROR, "错误！", "输入的单词错误！", "应该输入一个正确的单词");
            return;
        }
        if (inputTime == 0) {
            showAlert(Alert.AlertType.INFORMATION, "不，你失败了！", "你耗尽了机会！", "答案：" + answerWord + " :\n" + sqlTools.getWordInfo(answerWord));
            startGame();
            return;
        }
        if (wordList.contains(inputText)) {
            showAlert(Alert.AlertType.WARNING, "不", "你已经猜过这个单词了！", inputText + " :\n" + sqlTools.getWordInfo(inputText));
            inputTextFiled.clear();
            return;
        }
        inputTime--;
        wordList.add(inputText);
        refresh(inputText);
        inputTimeUpdate();
        if (inputText.equals(answerWord)) {
            showAlert(Alert.AlertType.INFORMATION, "胜利！", "恭喜你，成功猜出单词！", answerWord + " :\n" + sqlTools.getWordInfo(answerWord));
            startGame();
            return;
        }
        if (inputTime == 0) {
            showAlert(Alert.AlertType.INFORMATION, "不，你失败了！", "你耗尽了机会！", "答案：" + answerWord + " :\n" + sqlTools.getWordInfo(answerWord));
            startGame();
        }
    }

    private void getYellowWord() {
        StringBuilder sb = new StringBuilder();
        for (var c : wordList) {
            char[] chars = c.toCharArray();
            for (var ch : chars) {
                if (answerWord.contains(String.valueOf(ch))) {
                    sb.append(ch);
                }
            }
        }
        this.yellowWords = sb.toString();
    }

    private String retain(String str, String keepChar) {
        StringBuilder sb = new StringBuilder();
        for (var c : str.toCharArray()) {
            if (keepChar.indexOf(c) != -1) {
                sb.append(c);
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * 刷新显示
     */
    private void refresh(String inputText) {
        refresh(inputText, outputGrid);
    }

    /**
     * 刷新显示
     */
    private void refresh(String inputText, GridPane gridPane) {
        int charSize = 40; // 每个字符的大小
        gridPane.setAlignment(Pos.CENTER); // 居中对齐

        // 将字符串分割成字符数组，并在每个格子中添加一个标签
        char[] chars = inputText.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String temp = String.valueOf(chars[i]);
            Label label = new Label(temp);
            label.setPrefSize(charSize, charSize);
            label.setAlignment(Pos.CENTER); // 居中对齐
            label.setBackground(new Background(new BackgroundFill(getColor(temp, i), CornerRadii.EMPTY, null)));
            label.setFont(inputButton.getFont());//与全局字体同步
            label.setStyle("-fx-border-color: black; -fx-border-width: 1px;-fx-font-size: %d;".formatted(charSize)); // 设置边框样式
            gridPane.add(label, i, rowIndex); // 放置在第一行的不同列
        }
        rowIndex++;
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

        Font a = Font.loadFont(Objects.requireNonNull(this.getClass().getResourceAsStream("fonts/fzjt.ttf")), 0);
Platform.runLater(() -> tm.setFontFamily(a.getFamily()));
        Platform.runLater(() -> tm.addScene(info.getScene()));

 tools.makeFontFamilyChooser(fontChose);
        tools.makeFontSizeChooser(fontSize);
        Platform.runLater(() -> Application.setUserAgentStylesheet(theme[themeFlag]));
        changeTheme.setGraphic(new FontIcon(BootstrapIcons.MOON));
        info.setGraphic(new FontIcon(BootstrapIcons.INFO_CIRCLE));
        gameIsStart = false;
        startGame();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 10, wordLength);
        levelChose.setValueFactory(valueFactory);
        levelChose.valueProperty().addListener((observable, oldValue, newValue) -> {
            wordLength = newValue;
            inputTimeUpdate();
        });
        inputTimeUpdate();
        this.yellowWords = "";
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
        if (!gameIsStart) {
            startGame.setText("开始游戏");
            levelChose.setDisable(false);
            inputButton.setDisable(true);
            inputTextFiled.setDisable(true);
            showHint.setDisable(true);
            gameIsStart = true;
            reset();
        } else {
            answerUpdate();
            startGame.setText("结束游戏");
            levelChose.setDisable(true);
            inputButton.setDisable(false);
            inputTextFiled.setDisable(false);
            showHint.setDisable(false);
            gameIsStart = false;
            wordList = new ArrayList<>();
            inputTimeUpdate();
        }
        this.yellowWords = "";
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
        tm.addScene(alert.getDialogPane().getScene());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
alert.initOwner(inputTextFiled.getScene().getWindow());
        alert.showAndWait();
    }

    @FXML
    void showHint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(inputTextFiled.getScene().getWindow());
        tm.addScene(alert.getDialogPane().getScene());
        alert.setTitle("提示");
        alert.setHeaderText(null);
        // 创建自定义的内容
        VBox content = new VBox();
        // 添加自定义的节点，比如一个进度条
        GridPane gridPane = new GridPane();
        getYellowWord();
        refresh(retain(answerWord, yellowWords), gridPane);
        content.getChildren().add(gridPane);

        // 设置对话框的内容
        alert.getDialogPane().setContent(content);
        alert.show();
    }
}
