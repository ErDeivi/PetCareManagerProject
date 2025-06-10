package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/**
 * Controlador para el menú de administrador.
 * Maneja la navegación entre las diferentes secciones de la aplicación y las operaciones
 * de exportación e importación de la base de datos.
 */
public class MenuAdminControlador {

    /**
     * Navega a la vista de gestión de mascotas.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void gestionarMascotasOnAction() throws IOException {
        App.setRoot("gestionarMascotas");
    }

    /**
     * Navega a la vista de gestión de dueños.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void gestionarDuenosOnAction() throws IOException {
        App.setRoot("gestionarDuenos");
    }

    /**
     * Navega a la vista de gestión de cuidadores.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void gestionarCuidadoresOnAction() throws IOException {
        App.setRoot("gestionarCuidadores");
    }

    /**
     * Navega a la vista de gestión de citas.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void gestionarCitasOnAction() throws IOException {
        App.setRoot("gestionarCitas");
    }

    /**
     * Navega a la vista de gestión de servicios.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void gestionarServiciosOnAction() throws IOException {
        App.setRoot("gestionarServicios");
    }

    /**
     * Navega a la vista de gestión de categorías.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void gestionarCategoriasOnAction() throws IOException {
        App.setRoot("gestionarCategorias");
    }

    /**
     * Navega a la vista de cambio de contraseña.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void cambiarContrasenaOnAction() throws IOException {
        App.setRoot("cambiarContrasena");
    }

    /**
     * Maneja el cierre de sesión del administrador.
     * Muestra un diálogo de confirmación antes de cerrar la sesión.
     * @throws IOException si hay un error al cargar la vista de login
     */
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

    /**
     * Exporta la base de datos a un archivo SQL.
     * Muestra un mensaje de éxito o error según corresponda.
     */
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

    /**
     * Importa una base de datos desde un archivo SQL seleccionado.
     * Permite al usuario seleccionar el archivo y muestra mensajes de éxito o error.
     */
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

    /**
     * Muestra un diálogo de alerta con el tipo, título y contenido especificados.
     * @param tipo El tipo de alerta a mostrar
     * @param titulo El título del diálogo
     * @param contenido El contenido del mensaje
     */
    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
