package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Mascota;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModificarMascotaControlador implements Initializable {
    @FXML
    private TextField idMascota, nombreMascota, especieMascota, razaMascota, pesoMascota, idDuenoMascota;
    @FXML
    private DatePicker edadMascota;
    @FXML
    private Button modificarMascota, volverMascota;
    private Mascota mascota;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarDatosMascota();
    }

    private void cargarDatosMascota() {
        mascota = App.getMascotaModificar();
        if (mascota != null) {
            idMascota.setText(String.valueOf(mascota.getId()));
            nombreMascota.setText(mascota.getNombre());
            especieMascota.setText(mascota.getEspecie());
            razaMascota.setText(mascota.getRaza());
            edadMascota.getEditor().setText(String.valueOf(mascota.getEdad()));
            pesoMascota.setText(String.valueOf(mascota.getPeso()));
            idDuenoMascota.setText(String.valueOf(mascota.getId()));
        }
    }

    public void modificarMascotaOnAction(ActionEvent actionEvent) {
        if (validarCampos()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE Mascota SET nombre=?, especie=?, raza=?, edad=?, peso=?, id_dueño=? WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, nombreMascota.getText());
                pst.setString(2, especieMascota.getText());
                pst.setString(3, razaMascota.getText());
                pst.setInt(4, Integer.parseInt(edadMascota.getEditor().getText()));
                pst.setDouble(5, Double.parseDouble(pesoMascota.getText()));
                pst.setInt(6, Integer.parseInt(idDuenoMascota.getText()));
                pst.setInt(7, Integer.parseInt(idMascota.getText()));
                int filasAfectadas = pst.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Mascota modificada correctamente");
                    volverMascotaOnAction(null);
                }
            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al modificar la mascota: " + e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
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

    public void volverMascotaOnAction(ActionEvent actionEvent) throws IOException {
        App.setRoot("listadoMascotas");
    }
} 