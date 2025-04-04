module caiDanCi {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.kordamp.ikonli.bootstrapicons;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;


    requires jdk.xml.dom;
    requires com.gluonhq.attach.storage;
    opens caidanci to javafx.fxml;

    exports caidanci;
}