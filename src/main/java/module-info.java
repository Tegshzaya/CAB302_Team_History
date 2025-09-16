module com.example.login_page {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;            // for WebView
    requires jdk.jsobject;         // for netscape.javascript.JSObject (JS bridge)

    requires org.kordamp.bootstrapfx.core; // optional styling

    opens com.example.login_page to javafx.fxml;
    exports com.example.login_page;
}
