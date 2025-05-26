module com.example.petcaremanagerproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;

    opens com.example.petcaremanagerproject to javafx.fxml;
    opens com.example.petcaremanagerproject.Controladores to javafx.fxml;
    opens com.example.petcaremanagerproject.Modelo to javafx.base;

    exports com.example.petcaremanagerproject;
    exports com.example.petcaremanagerproject.Controladores;
    exports com.example.petcaremanagerproject.Modelo;
}