package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.Modelo.Cuidador;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModificarCuidadorControlador {
    @FXML private TextField txtNombre, txtCorreo, txtTelefono;
    @FXML private javafx.scene.control.Label lblTitulo;
    private Cuidador cuidadorAModificar;

    public void setCuidadorAModificar(Cuidador cuidador) {
        this.cuidadorAModificar = cuidador;
        txtNombre.setText(cuidador.getNombre());
        txtCorreo.setText(cuidador.getCorreo());
        txtTelefono.setText(cuidador.getTelefono());
    }

    @FXML
    public void initialize() {
        lblTitulo.setText("Modificar Cuidador");
    }

    @FXML
    private void guardarOnAction() {
        if (validarCampos()) {
            try {
                actualizar();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cuidador actualizado correctamente");
                cerrarVentana();
            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al guardar el cuidador: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cancelarOnAction() {
        cerrarVentana();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El nombre es obligatorio");
            return false;
        }
        if (!txtNombre.getText().trim().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El nombre solo debe contener letras y espacios");
            return false;
        }
        if (txtCorreo.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El correo es obligatorio");
            return false;
        }
        if (!txtCorreo.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El formato del correo electrónico no es válido");
            return false;
        }
        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El teléfono es obligatorio");
            return false;
        }
        if (!txtTelefono.getText().trim().matches("^[6-9]\\d{8}$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El teléfono debe ser un número de 9 dígitos que comience por 6, 7, 8 o 9");
            return false;
        }
        return true;
    }

    private void actualizar() throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, correo = ?, telefono = ? WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtNombre.getText().trim());
            pstmt.setString(2, txtCorreo.getText().trim());
            pstmt.setString(3, txtTelefono.getText().trim());
            pstmt.setInt(4, cuidadorAModificar.getIdUsuario());
            pstmt.executeUpdate();
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