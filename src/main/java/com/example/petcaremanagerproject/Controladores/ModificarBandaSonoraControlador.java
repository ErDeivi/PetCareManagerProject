package com.example.petcaremanagerproject.Controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.BandaSonora;
import com.example.petcaremanagerproject.Modelo.Videojuego;
import com.example.petcaremanagerproject.Util.DatabaseConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ModificarBandaSonoraControlador {

    @FXML
    private TextField id, nombre, compositor, enlace;

    @FXML
    private Button crear, volver;

    private BandaSonora bandaSonora;

    @FXML
    void modificarOnAction(ActionEvent event) {

    }

    private void cargarDatosBandaSonora() {
        bandaSonora = App.getBandaSonoraModificar();
        if (bandaSonora != null) {
            id.setText(String.valueOf(bandaSonora.getId()));
            nombre.setText(bandaSonora.getNombre());
            compositor.setText(bandaSonora.getCompositor());
            enlace.setText(bandaSonora.getUrl());
        }
    }

    public void editarOnAction(ActionEvent actionEvent) {
        if (validarCampos()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE bandasonora SET Nombre=?, Compositor=?, " +
                        "URL=? " +
                        "WHERE ID=?";

                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, nombre.getText());
                pst.setString(2, compositor.getText());
                pst.setString(3, enlace.getText());
                pst.setInt(4, bandaSonora.getId());

                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito",
                            "Banda sonora modificada correctamente");
                    volverOnAction(null);
                }

            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error",
                        "Error al modificar la banda sonora: " + e.getMessage());
            }
        }
    }

    private boolean validarCampos() {
        if (nombre.getText().isEmpty() || enlace.getText().isEmpty() ||
                enlace.getText().isEmpty()) {

            mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos",
                    "Por favor, rellene todos los campos obligatorios");
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

    public void volverOnAction(ActionEvent actionEvent) {
        App.setRoot("listadoBandasSonoras");
    }

}