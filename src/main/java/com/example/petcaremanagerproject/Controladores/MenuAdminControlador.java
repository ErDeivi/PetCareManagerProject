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
    @FXML private Button gestionarMascotas;
    @FXML private Button gestionarDuenos;
    @FXML private Button gestionarCuidadores;
    @FXML private Button gestionarCitas;
    @FXML private Button gestionarServicios;
    @FXML private Button gestionarCategorias;
    @FXML private Button gestionarUsuarios;
    @FXML private Button cambiarContrasena;
    @FXML private Button cerrarSesion;

    @FXML
    private void gestionarMascotasOnAction() throws IOException {
        Stage stage = (Stage) gestionarMascotas.getScene().getWindow();
        App.setRoot("gestionarMascotas");
        App.configurarVentanaModal(stage);
    }

    @FXML
    private void gestionarDuenosOnAction() throws IOException {
        Stage stage = (Stage) gestionarDuenos.getScene().getWindow();
        App.setRoot("gestionarDuenos");
        App.configurarVentanaModal(stage);
    }

    @FXML
    private void gestionarCuidadoresOnAction() throws IOException {
        Stage stage = (Stage) gestionarCuidadores.getScene().getWindow();
        App.setRoot("gestionarCuidadores");
        App.configurarVentanaModal(stage);
    }

    @FXML
    private void gestionarCitasOnAction() throws IOException {
        Stage stage = (Stage) gestionarCitas.getScene().getWindow();
        App.setRoot("gestionarCitas");
        App.configurarVentanaModal(stage);
    }

    @FXML
    private void gestionarServiciosOnAction() throws IOException {
        Stage stage = (Stage) gestionarServicios.getScene().getWindow();
        App.setRoot("listadoServicios");
        App.configurarVentanaModal(stage);
    }

    @FXML
    private void gestionarCategoriasOnAction() throws IOException {
        Stage stage = (Stage) gestionarCategorias.getScene().getWindow();
        App.setRoot("gestionarCategorias");
        App.configurarVentanaModal(stage);
    }

    @FXML
    private void gestionarUsuariosOnAction() throws IOException {
        Stage stage = (Stage) gestionarUsuarios.getScene().getWindow();
        App.setRoot("gestionarUsuarios");
        App.configurarVentanaModal(stage);
    }

    @FXML
    private void cambiarContrasenaOnAction() throws IOException {
        Stage stage = (Stage) cambiarContrasena.getScene().getWindow();
        App.setRoot("cambiarContrasena");
        App.configurarVentanaModal(stage);
    }

    @FXML
    private void cerrarSesionOnAction() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar sesión");
        alert.setHeaderText("¿Estás seguro de que quieres cerrar sesión?");
        alert.setContentText("Se cerrará la sesión actual.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) cerrarSesion.getScene().getWindow();
            App.setRoot("login");
        }
    }
}
