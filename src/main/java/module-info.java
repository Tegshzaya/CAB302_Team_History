module com.learn.plan_b_302 {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.web;



    requires java.sql;
    requires jdk.jsobject;
    requires javafx.graphics;
    requires java.desktop;

    opens com.learn.plan_b_302 to javafx.fxml;
    exports com.learn.plan_b_302;
}