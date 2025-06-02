module com.example.petcaremanagerproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires javafx.base;

    opens com.example.petcaremanagerproject to javafx.fxml;
    opens com.example.petcaremanagerproject.Controladores to javafx.fxml;
    opens com.example.petcaremanagerproject.Modelo to javafx.base;
    opens com.example.petcaremanagerproject.Util to javafx.base;

    exports com.example.petcaremanagerproject;
    exports com.example.petcaremanagerproject.Controladores;
    exports com.example.petcaremanagerproject.Modelo;
    exports com.example.petcaremanagerproject.Util;
}