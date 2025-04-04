package caidanci;


import javafx.collections.FXCollections;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Font;
import javafx.stage.Screen;


/**
 * @author Iammm 2024/8/23 22:32
 */
public class tools {


    public static double[] getScreenSize() {
        // 获取主屏幕
        Screen screen = Screen.getPrimary();

        // 获取屏幕的可见边界
        Rectangle2D bounds = screen.getVisualBounds();

        // 获取屏幕的宽度和高度
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        // 获取屏幕的 DPI
        double screenDPI = screen.getDpi();

        System.out.println("屏幕宽度: " + screenWidth);
        System.out.println("屏幕高度: " + screenHeight);
        System.out.println("屏幕 DPI: " + screenDPI);
        return new double[]{screenWidth, screenHeight};
    }

    /**
     * 获取当前屏幕的中心位置坐标
     *
     * @return int【2】 x坐标 y坐标
     */
    public static int[] getCenterPosition() {
        var a = getScreenSize();
        return new int[]{(int) (a[0] / 2), (int) (a[1] / 2)};
    }

    public static int[] screenAutoSize(int preWith, int preHeight) {
        var screenSize = getScreenSize();
        var shouldSize = new int[2];
        if (screenSize[0] > screenSize[1]) {
            if (preWith > screenSize[0]) {
                shouldSize[0] = (int) screenSize[0];
                shouldSize[1] = (int) (preHeight * (screenSize[0] / preWith));
            } else if (preHeight > screenSize[1]) {
                shouldSize[1] = (int) screenSize[1];
                shouldSize[0] = (int) (preWith * (screenSize[1] / preHeight));
            } else {
                shouldSize[0] = preWith;
                shouldSize[1] = preHeight;
            }
        }

        return shouldSize;
    }

    public static void makeFontFamilyChooser(ComboBox<String> fontFamilyChooser) {
        var tm = ThemeManager.getInstance();
        fontFamilyChooser.setPrefWidth(200);

        fontFamilyChooser.getItems().add(tm.getFontFamily());
        fontFamilyChooser.getItems().addAll(FXCollections.observableArrayList(Font.getFamilies()));

        fontFamilyChooser.getSelectionModel().select(tm.getFontFamily());
        fontFamilyChooser.valueProperty().addListener((_, _, val) -> {
            if (val != null) {
                tm.setFontFamily(val);
            }
        });

        // Custom cell factory to set font for each item
        fontFamilyChooser.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                //下面的有些许问题，包括但不限于性能影响严重，错误多，后台狂刷err，暂时不用
//                if (!(empty || item == null)) {
//                    setText(item);
//                    javafx.application.Platform.runLater(() -> setStyle("-fx-font-family: '" + item + "'; "));
//                }
            }
        });
    }

    public static void makeFontSizeChooser(Spinner<Integer> fontSize) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                8, 72, 18);
        fontSize.setValueFactory(valueFactory);
        var tm = ThemeManager.getInstance();
        tm.setFontSize(fontSize.getValue());
        // 添加监听器，当Spinner值改变时，更新Label的字体大小
        fontSize.valueProperty().addListener((_, _, newValue) -> {
            tm.setFontSize(newValue);
        });

    }


}