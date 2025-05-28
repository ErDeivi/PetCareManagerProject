package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Servicio;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModificarServicioControlador {

    @FXML
    private TextField idServicio, estadoServicio, observacionesServicio;
    @FXML
    private Button modificarServicio, volverServicio;
    private Servicio servicio;

    @FXML
    public void initialize() {
        cargarDatosServicio();
    }

    private void cargarDatosServicio() {
        servicio = App.getServicioModificar();
        if (servicio != null) {
            idServicio.setText(String.valueOf(servicio.getIdServicio()));
            estadoServicio.setText(servicio.getEstado());
            observacionesServicio.setText(servicio.getObservaciones());
        }
    }

    @FXML
    void modificarServicioOnAction(ActionEvent event) {
        if (validarCampos()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE servicio SET estado=?, observaciones=? WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, estadoServicio.getText());
                pst.setString(2, observacionesServicio.getText());
                pst.setInt(3, Integer.parseInt(idServicio.getText()));
                int filasAfectadas = pst.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Servicio modificado correctamente");
                    volverServicioOnAction(null);
                }
            } catch (SQLException | IOException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al modificar el servicio: " + e.getMessage());
            }
        }
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

    @FXML
    void volverServicioOnAction(ActionEvent event) throws IOException {
        App.setRoot("listadoServicios");
    }
} 