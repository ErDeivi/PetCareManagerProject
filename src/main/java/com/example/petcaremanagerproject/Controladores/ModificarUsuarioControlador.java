package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Usuario;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModificarUsuarioControlador implements Initializable {

    @FXML
    private TextField nombreUsuario;
    @FXML
    private TextField correoUsuario;
    @FXML
    private TextField contrasenaUsuario;
    @FXML
    private TextField telefonoUsuario;
    private Usuario usuario;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usuario = App.getUsuarioModificar();
        if (usuario != null) {
            cargarDatosUsuario();
        }
    }

    private void cargarDatosUsuario() {
        nombreUsuario.setText(usuario.getNombre());
        correoUsuario.setText(usuario.getCorreo());
        telefonoUsuario.setText(usuario.getTelefono());
        // contrasenaUsuario.setText(usuario.getContrasena()); // Si tienes el campo en el modelo
    }

    @FXML
    private void modificarUsuarioOnAction(ActionEvent event) {
        if (validarCampos()) {
            Connection conn = null;
            PreparedStatement pst = null;

            try {
                conn = DatabaseConnection.getConnection();
                String query = "UPDATE Usuario SET nombre = ?, correo = ?, contrasena = ?, telefono = ? WHERE id = ?";
                pst = conn.prepareStatement(query);

                pst.setString(1, nombreUsuario.getText());
                pst.setString(2, correoUsuario.getText());
                pst.setString(3, contrasenaUsuario.getText());
                pst.setString(4, telefonoUsuario.getText());
                pst.setInt(5, usuario.getIdUsuario());

                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario modificado correctamente");
                    volverAListado();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo modificar el usuario");
                }

            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al modificar el usuario: " + e.getMessage());
            } finally {
                try {
                    if (pst != null) pst.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar las conexiones: " + e.getMessage());
                }
            }
        }
    }

    private boolean validarCampos() {
        if (nombreUsuario.getText().isEmpty() || correoUsuario.getText().isEmpty() || 
            contrasenaUsuario.getText().isEmpty() || telefonoUsuario.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", 
                         "Por favor, rellene todos los campos");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    private void cancelarOnAction(ActionEvent event) {
        volverAListado();
    }

    private void volverAListado() {
        App.setRoot("listadoUsuarios");
    }
} 