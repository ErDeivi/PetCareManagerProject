package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearUsuarioControlador {

    @FXML
    private TextField nombreUsuario;
    @FXML
    private TextField correoUsuario;
    @FXML
    private TextField contrasenaUsuario;
    @FXML
    private TextField telefonoUsuario;

    @FXML
    private void crearUsuarioOnAction(ActionEvent event) {
        if (validarCampos()) {
            Connection conn = null;
            PreparedStatement pst = null;

            try {
                conn = DatabaseConnection.getConnection();
                String query = "INSERT INTO Usuario (nombre, correo, contrasena, telefono) VALUES (?, ?, ?, ?)";
                pst = conn.prepareStatement(query);

                pst.setString(1, nombreUsuario.getText());
                pst.setString(2, correoUsuario.getText());
                pst.setString(3, contrasenaUsuario.getText());
                pst.setString(4, telefonoUsuario.getText());

                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario creado correctamente");
                    volverAListado();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el usuario");
                }

            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al crear el usuario: " + e.getMessage());
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