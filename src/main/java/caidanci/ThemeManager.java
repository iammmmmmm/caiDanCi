package caidanci;

import javafx.css.PseudoClass;
import javafx.scene.Scene;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Iammm
 * 2024/9/3 16:33
 */
public class ThemeManager {
    private static final PseudoClass USER_CUSTOM = PseudoClass.getPseudoClass("user-custom");
    private final Map<String, String> customCSSDeclarations = new LinkedHashMap<>(); // -fx-property | value;
    private final Map<String, String> customCSSRules = new LinkedHashMap<>(); // .foo | -fx-property: value;
    private final List<Scene> scene = new ArrayList<>();
    private String fontFamily = "FZKai-Z03S";

    public static ThemeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void reloadCustomCSS() {
        Objects.requireNonNull(scene);
        StringBuilder css = new StringBuilder();

        css.append(".root:");
        css.append(USER_CUSTOM.getPseudoClassName());
        css.append(" {\n");
        customCSSDeclarations.forEach((k, v) -> {
            css.append("\t");
            css.append(k);
            css.append(": ");
            css.append(v);
            css.append(";\n");
        });
        css.append("}\n");

        customCSSRules.forEach((k, v) -> {
            // custom CSS is applied to the body,
            // thus it has a preference over accent color
            css.append(".body:");
            css.append(USER_CUSTOM.getPseudoClassName());
            css.append(" ");
            css.append(k);
            css.append(" {");
            css.append(v);
            css.append("}\n");
        });
        for (Scene scene : getScene()) {

            scene.getRoot().getStylesheets().removeIf(uri -> uri.startsWith("data:text/css"));
            scene.getRoot().getStylesheets().add("data:text/css;base64," + Base64.getEncoder().encodeToString(css.toString().getBytes(UTF_8)));
            scene.getRoot().pseudoClassStateChanged(USER_CUSTOM, true);
        }

    }

    public List<Scene> getScene() {
        return scene;
    }

    public void addScene(Scene scene) {
        this.scene.add(scene);
        //对新加入scene生效css
        reloadCustomCSS();
    }

    private void setCustomDeclaration(String property, String value) {
        customCSSDeclarations.put(property, value);
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        Objects.requireNonNull(fontFamily);
        setCustomDeclaration("-fx-font-family", "\"" + fontFamily + "\"");

        this.fontFamily = fontFamily;

        reloadCustomCSS();
    }

    private static class InstanceHolder {

        private static final ThemeManager INSTANCE = new ThemeManager();
    }

}
