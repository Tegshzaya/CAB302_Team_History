module com.example.login_page {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.kordamp.bootstrapfx.core;
    requires javafx.base;

    requires javafx.graphics;

    opens com.example.login_page to javafx.fxml;
    exports com.example.login_page;
}