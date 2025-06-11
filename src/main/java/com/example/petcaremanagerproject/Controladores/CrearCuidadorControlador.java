package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Controlador para la creación y modificación de cuidadores en el sistema.
 * Gestiona la interfaz de usuario para añadir nuevos cuidadores o modificar los existentes.
 */
public class CrearCuidadorControlador {
    @FXML private TextField txtNombre, txtCorreo, txtTelefono;
    @FXML private javafx.scene.control.Label lblTitulo;

    @FXML
    public void initialize() {
        lblTitulo.setText("Nuevo Cuidador");
        txtNombre.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
    }

    @FXML
    private void guardarOnAction() {
        if (validarCampos()) {
            try {
                crear();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cuidador creado correctamente");
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

    private void crear() throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (nombre, correo, contraseña, telefono, imagen_url) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                pstmtUsuario.setString(1, txtNombre.getText().trim());
                pstmtUsuario.setString(2, txtCorreo.getText().trim());
                pstmtUsuario.setString(3, "123456"); // Contraseña por defecto
                pstmtUsuario.setString(4, txtTelefono.getText().trim());
                pstmtUsuario.setNull(5, java.sql.Types.VARCHAR); // Imagen URL null
                pstmtUsuario.executeUpdate();
                ResultSet rs = pstmtUsuario.getGeneratedKeys();
                int idUsuario = -1;
                if (rs.next()) {
                    idUsuario = rs.getInt(1);
                }
                if (idUsuario == -1) {
                    conn.rollback();
                    throw new SQLException("No se pudo obtener el ID del usuario insertado");
                }
                String sqlCuidadorSimple = "INSERT INTO cuidador (id_usuario) VALUES (?)";
                try (PreparedStatement pstmtCuidador = conn.prepareStatement(sqlCuidadorSimple)) {
                    pstmtCuidador.setInt(1, idUsuario);
                    pstmtCuidador.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error al crear cuidador: " + e.getMessage());
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