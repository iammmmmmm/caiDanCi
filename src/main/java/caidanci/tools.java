package caidanci;

import javafx.geometry.Rectangle2D;
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
  
}
