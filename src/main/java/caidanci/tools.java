package caidanci;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.attach.util.Services;
import javafx.collections.FXCollections;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Font;
import javafx.stage.Screen;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


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

        // keyword to reset font family to its default value
        fontFamilyChooser.getItems().add(tm.getFontFamily());
        fontFamilyChooser.getItems().addAll(FXCollections.observableArrayList(Font.getFamilies()));

        // select active font family value on page load
        fontFamilyChooser.getSelectionModel().select(tm.getFontFamily());
        fontFamilyChooser.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                tm.setFontFamily(val);
            }
        });

        // Custom cell factory to set font for each item
        fontFamilyChooser.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
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
                8, 72, 14);
        fontSize.setValueFactory(valueFactory);
        var tm = ThemeManager.getInstance();
        // 添加监听器，当Spinner值改变时，更新Label的字体大小
        fontSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            tm.setFontSize(newValue);
        });

    }

    /**
     * 初始化资源，将嵌入的资源释放到本地文件系统，以确保在部分系统如安卓上 h2，javafx.font.load，file能正常使用
     *
     * @param embeddedResourcePath 资源在程序内部的路径
     * @return  资源在本地系统上的输入流，如果是非桌面端（linux win mac）将会是文件在本地文件系统的File对象
     */
    public static Object initializeResource(String embeddedResourcePath) {
        Platform os = com.gluonhq.attach.util.Platform.getCurrent();
        System.out.println(os.getName());

        if (os == Platform.ANDROID) {
            AtomicReference<String> localResourcePath = new AtomicReference<>("");
            Services.get(StorageService.class).ifPresent(storage -> {
                Optional<File> internalStorage = storage.getPrivateStorage();
                if (internalStorage.isEmpty()) {
                    System.err.println("Failed to get internal storage path.");
                }
                internalStorage.ifPresent(storageFile -> {
                    System.out.println("Internal Storage Path: " + storageFile.getAbsolutePath());
                    localResourcePath.set(storageFile.getAbsolutePath().replace(".gluon", "") + embeddedResourcePath);
                });
            });
            File localFile = new File(localResourcePath.get());
            if (!localFile.exists()) {
                System.out.println("Resource does not exist, creating...");
                try (InputStream is = tools.class.getResourceAsStream(embeddedResourcePath)) {
                    if (is == null) {
                        throw new RuntimeException("Embedded resource not found: " + embeddedResourcePath);
                    }
                    System.out.println(localFile.getAbsolutePath());
                    Files.copy(is, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy embedded Resource: " + e.getMessage(), e);
                }
            }
                return localFile;
        } else {
            return tools.class.getResourceAsStream(embeddedResourcePath);
        }
    }


}