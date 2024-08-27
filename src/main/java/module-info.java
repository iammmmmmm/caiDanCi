module caiDanCi {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.kordamp.ikonli.bootstrapicons;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires com.gluonhq.attach.storage;
    requires com.gluonhq.attach.util;

    requires jdk.xml.dom;
    opens caidanci to javafx.fxml;

    exports caidanci;
}