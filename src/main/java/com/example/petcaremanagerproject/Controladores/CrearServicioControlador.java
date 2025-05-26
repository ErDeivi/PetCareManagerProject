package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearServicioControlador {

    @FXML
    private TextField estadoServicio, observacionesServicio;
    @FXML
    private Button crearServicio, volverServicio;

    @FXML
    void crearServicioOnAction(ActionEvent event) {
        if (validarCampos()) {
            Connection conn = null;
            PreparedStatement pst = null;

            try {
                conn = DatabaseConnection.getConnection();
                String query = "INSERT INTO servicio (estado, observaciones) VALUES (?, ?)";
                pst = conn.prepareStatement(query);

                pst.setString(1, estadoServicio.getText());
                pst.setString(2, observacionesServicio.getText());

                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Servicio creado correctamente");
                    limpiarCampos();
                }

            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al crear el servicio: " + e.getMessage());
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

    @FXML
    void volverServicioOnAction(ActionEvent event) {
        App.setRoot("listadoServicios");
    }

    private boolean validarCampos() {
        if (estadoServicio.getText().isEmpty() || observacionesServicio.getText().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, rellene todos los campos obligatorios");
            return false;
        }
        return true;
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        estadoServicio.clear();
        observacionesServicio.clear();
    }
} 