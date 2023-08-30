module com.org.cleangreencity {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
    requires java.sql;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires java.datatransfer;
    requires java.desktop;

    opens com.org.cleangreencity to javafx.fxml;
    exports com.org.cleangreencity;
    exports com.org.cleangreencity.model to com.fasterxml.jackson.databind;
    opens com.org.cleangreencity.controller to javafx.fxml;
}