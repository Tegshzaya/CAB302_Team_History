module com.learn.plan_b_302 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;



    requires java.sql;

    opens com.learn.plan_b_302 to javafx.fxml;
    exports com.learn.plan_b_302;
}