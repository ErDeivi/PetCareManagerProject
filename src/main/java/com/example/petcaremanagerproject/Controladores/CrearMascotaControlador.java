package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CrearMascotaControlador implements Initializable {
    @FXML
    private TextField nombreMascota, especieMascota, razaMascota, pesoMascota, idDuenoMascota;
    @FXML
    private DatePicker edadMascota;
    @FXML
    private Button crearMascota, volverMascota;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // No hay combos que cargar
    }

    public void crearMascotaOnAction(ActionEvent actionEvent) {
        if (validarCampos()) {
            Connection conn = null;
            PreparedStatement pst = null;
            try {
                conn = DatabaseConnection.getConnection();
                String query = "INSERT INTO Mascota (nombre, especie, raza, edad, peso, id_dueño) VALUES (?, ?, ?, ?, ?, ?)";
                pst = conn.prepareStatement(query);
                pst.setString(1, nombreMascota.getText());
                pst.setString(2, especieMascota.getText());
                pst.setString(3, razaMascota.getText());
                pst.setInt(4, Integer.parseInt(edadMascota.getEditor().getText()));
                pst.setDouble(5, Double.parseDouble(pesoMascota.getText()));
                pst.setInt(6, Integer.parseInt(idDuenoMascota.getText()));
                int filasAfectadas = pst.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Mascota creada correctamente");
                    limpiarCampos();
                }
            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al crear la mascota: " + e.getMessage());
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
        if (nombreMascota.getText().isEmpty() || especieMascota.getText().isEmpty() || razaMascota.getText().isEmpty() || edadMascota.getEditor().getText().isEmpty() || pesoMascota.getText().isEmpty() || idDuenoMascota.getText().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, rellene todos los campos obligatorios");
            return false;
        }
        try {
            Integer.parseInt(edadMascota.getEditor().getText());
        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.WARNING, "Error de formato", "La edad debe ser un número entero");
            return false;
        }
        try {
            Double.parseDouble(pesoMascota.getText());
        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.WARNING, "Error de formato", "El peso debe ser un número válido");
            return false;
        }
        try {
            Integer.parseInt(idDuenoMascota.getText());
        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.WARNING, "Error de formato", "El ID del dueño debe ser un número entero");
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
        nombreMascota.clear();
        especieMascota.clear();
        razaMascota.clear();
        edadMascota.getEditor().clear();
        pesoMascota.clear();
        idDuenoMascota.clear();
    }

    public void volverMascotaOnAction(ActionEvent actionEvent) {
        App.setRoot("listadoMascotas");
    }
} 