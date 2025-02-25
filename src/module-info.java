module ApplicantDetails {
    exports university.management;

    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    opens university.management to javafx.fxml;
}
