package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.Modelo.Categoria;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModificarCategoriaControlador {
    @FXML private Label lblTitulo;
    @FXML private TextField txtTipo;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnCancelar;

    private Categoria categoriaAModificar;

    @FXML
    public void initialize() {
        txtTipo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 50) {
                txtTipo.setText(oldVal);
            }
        });
        txtDescripcion.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 200) {
                txtDescripcion.setText(oldVal);
            }
        });
    }

    public void setCategoriaAModificar(Categoria categoria) {
        this.categoriaAModificar = categoria;
        if (categoria != null) {
            lblTitulo.setText("Modificar Categoría");
            txtTipo.setText(categoria.getTipo());
            txtDescripcion.setText(categoria.getDescripcion());
        }
    }

    @FXML
    private void guardarOnAction() {
        if (validarCampos()) {
            try {
                modificar();
                cerrarVentana();
            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al modificar la categoría: " + e.getMessage());
            }
        }
    }

    private boolean validarCampos() {
        String tipo = txtTipo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        if (tipo.isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El tipo de categoría es obligatorio.");
            txtTipo.requestFocus();
            return false;
        }
        if (descripcion.isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La descripción es obligatoria.");
            txtDescripcion.requestFocus();
            return false;
        }
        return true;
    }

    private void modificar() throws SQLException {
        String sql = "UPDATE categoria SET tipo = ?, descripcion = ? WHERE id_categoria = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtTipo.getText().trim());
            pstmt.setString(2, txtDescripcion.getText().trim());
            pstmt.setInt(3, categoriaAModificar.getIdCategoria());
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Categoría modificada correctamente");
            } else {
                throw new SQLException("No se pudo modificar la categoría");
            }
        }
    }

    @FXML
    private void cancelarOnAction() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
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