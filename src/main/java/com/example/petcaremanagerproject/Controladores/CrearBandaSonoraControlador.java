package com.example.petcaremanagerproject.Controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.DatabaseConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CrearBandaSonoraControlador {

    @FXML
    private TextField id, nombre, compositor, enlace;

    @FXML
    private Button crear, volver;

    @FXML
    void crearOnAction(ActionEvent event) {
        if (validarCampos()) {
            Connection conn = null;
            PreparedStatement pst = null;
            ResultSet rs = null;

            try {
                conn = DatabaseConnection.getConnection();
                String query = "INSERT INTO Videojuego (Nombre, Compositor, URL) "
                        +
                        "VALUES (?, ?, ?)";
                pst = conn.prepareStatement(query);

                pst.setString(1, nombre.getText());
                pst.setString(2, compositor.getText());
                pst.setString(3, enlace.getText());

                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito",
                            "Banda sonora creada correctamente");
                    limpiarCampos();
                }

            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error",
                        "Error al crear la banda sonora: " + e.getMessage());
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                    if (pst != null)
                        pst.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar las conexiones: " + e.getMessage());
                }
            }
        }

    }

    @FXML
    void volverOnAction(ActionEvent event) {
        App.setRoot("listadoBandasSonoras");
    }

    private boolean validarCampos() {
        if (nombre.getText().isEmpty() || compositor.getText().isEmpty() || enlace.getText().isEmpty()) {
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

    private void limpiarCampos() {
        nombre.clear();
        compositor.clear();
        enlace.clear();
    }

}