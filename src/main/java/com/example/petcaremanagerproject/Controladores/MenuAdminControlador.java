package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class MenuAdminControlador {

    @FXML
    private void gestionarMascotasOnAction() throws IOException {
        App.setRoot("gestionarMascotas");
    }

    @FXML
    private void gestionarDuenosOnAction() throws IOException {
        App.setRoot("gestionarDuenos");
    }

    @FXML
    private void gestionarCuidadoresOnAction() throws IOException {
        App.setRoot("gestionarCuidadores");
    }

    @FXML
    private void gestionarCitasOnAction() throws IOException {
        App.setRoot("gestionarCitas");
    }

    @FXML
    private void gestionarServiciosOnAction() throws IOException {
        App.setRoot("gestionarServicios");
    }

    @FXML
    private void gestionarCategoriasOnAction() throws IOException {
        App.setRoot("gestionarCategorias");
    }

    @FXML
    private void gestionarUsuariosOnAction() throws IOException {
        App.setRoot("gestionarUsuarios");
    }

    @FXML
    private void cambiarContrasenaOnAction() throws IOException {
        App.setRoot("cambiarContrasena");
    }

    @FXML
    private void cerrarSesionOnAction() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar sesión");
        alert.setHeaderText("¿Estás seguro de que quieres cerrar sesión?");
        alert.setContentText("Se cerrará la sesión actual.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            App.setRoot("login");
        }
    }
}
