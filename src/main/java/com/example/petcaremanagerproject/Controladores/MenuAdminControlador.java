package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

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
    private void exportarDbOnAction() {
        try {
            String filePath = DatabaseConnection.exportDatabase();
            mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Base de datos exportada correctamente a:\n" + filePath);
        } catch (IOException | InterruptedException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al exportar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void importarDbOnAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo SQL para importar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos SQL", "*.sql"));
        
        // Establecer el directorio inicial al de copias de seguridad
        String backupFolderPath = DatabaseConnection.getBackupFolderPath();
        File backupDir = new File(backupFolderPath);
        if (backupDir.exists() && backupDir.isDirectory()) {
            fileChooser.setInitialDirectory(backupDir);
        }

        File selectedFile = fileChooser.showOpenDialog(App.getStage());

        if (selectedFile != null) {
            try {
                DatabaseConnection.importDatabase(selectedFile);
                mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Base de datos importada correctamente desde:\n" + selectedFile.getAbsolutePath());
            } catch (IOException | InterruptedException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al importar la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "No se seleccionó ningún archivo para importar.");
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
