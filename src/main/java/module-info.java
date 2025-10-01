module com.learn.plan_b_302 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;            // for WebView
    requires jdk.jsobject;         // for netscape.javascript.JSObject (JS bridge)

    requires org.kordamp.bootstrapfx.core; // optional styling
    requires java.sql;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires okhttp3;

    opens com.learn.plan_b_302 to javafx.fxml;
    exports com.learn.plan_b_302;
}
