package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

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
        App.setRoot("listadoServicios");
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

    @FXML
    private void exportarBD() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Base de Datos");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("SQL Files", "*.sql")
        );
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try {
                // TODO: Implementar la exportación de la base de datos
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportación");
                alert.setHeaderText("Base de datos exportada");
                alert.setContentText("La base de datos se ha exportado correctamente.");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al exportar");
                alert.setContentText("Ha ocurrido un error al exportar la base de datos: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void importarBD() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Base de Datos");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("SQL Files", "*.sql")
        );
        File file = fileChooser.showOpenDialog(null);
        
        if (file != null) {
            try {
                // TODO: Implementar la importación de la base de datos
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Importación");
                alert.setHeaderText("Base de datos importada");
                alert.setContentText("La base de datos se ha importado correctamente.");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al importar");
                alert.setContentText("Ha ocurrido un error al importar la base de datos: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
