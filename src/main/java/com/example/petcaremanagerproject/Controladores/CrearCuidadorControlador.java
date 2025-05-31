package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearCuidadorControlador {
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> comboBoxEspecialidad;
    @FXML private ComboBox<String> comboBoxDisponibilidad;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    @FXML
    public void initialize() {
        comboBoxEspecialidad.getItems().addAll("Perros", "Gatos", "Aves", "Reptiles", "Todos");
        comboBoxDisponibilidad.getItems().addAll("Mañana", "Tarde", "Noche", "Completo");
    }

    @FXML
    private void guardarOnAction() {
        if (validarCampos()) {
            try {
                crear();
                cerrarVentana();
            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                    "Error al crear el cuidador: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cancelarOnAction() {
        cerrarVentana();
    }

    private boolean validarCampos() {
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El nombre es obligatorio");
            return false;
        }
        if (!txtNombre.getText().trim().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El nombre solo debe contener letras y espacios");
            return false;
        }

        // Validar apellidos
        if (txtApellidos.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Los apellidos son obligatorios");
            return false;
        }
        if (!txtApellidos.getText().trim().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Los apellidos solo deben contener letras y espacios");
            return false;
        }

        // Validar correo electrónico
        if (txtCorreo.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El correo es obligatorio");
            return false;
        }
        if (!txtCorreo.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El formato del correo electrónico no es válido");
            return false;
        }

        // Validar teléfono
        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El teléfono es obligatorio");
            return false;
        }
        if (!txtTelefono.getText().trim().matches("^[6-9]\\d{8}$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El teléfono debe ser un número de 9 dígitos que comience por 6, 7, 8 o 9");
            return false;
        }

        // Validar especialidad
        if (comboBoxEspecialidad.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La especialidad es obligatoria");
            return false;
        }

        // Validar disponibilidad
        if (comboBoxDisponibilidad.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La disponibilidad es obligatoria");
            return false;
        }

        return true;
    }

    private void crear() throws SQLException {
        String sql = "INSERT INTO cuidadores (nombre, apellidos, telefono, email, especialidad, disponibilidad) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtNombre.getText().trim());
                pstmt.setString(2, txtApellidos.getText().trim());
                pstmt.setString(3, txtTelefono.getText().trim());
                pstmt.setString(4, txtCorreo.getText().trim());
                pstmt.setString(5, comboBoxEspecialidad.getValue());
                pstmt.setString(6, comboBoxDisponibilidad.getValue());
                pstmt.executeUpdate();
                conn.commit();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cuidador creado correctamente");
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al crear cuidador", e);
            }
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
} 