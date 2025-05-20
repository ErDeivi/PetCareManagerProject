package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CrearVideojuegoControlador implements Initializable {
    @FXML
    private TextField nombre,consola,genero,precio,ventas;
    @FXML
    private ComboBox<String> bandaSonora;
    @FXML
    private DatePicker fechaPublicacion;
    @FXML
    private Button crear,volver;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarBandasSonoras();
    }

    private void cargarBandasSonoras() {
        ObservableList<String> bandasSonoras = FXCollections.observableArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT Nombre FROM BandaSonora");

            while (rs.next()) {
                bandasSonoras.add(rs.getString("Nombre"));
            }

            bandaSonora.setItems(bandasSonoras);

        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error",
                    "Error al cargar las bandas sonoras: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar las conexiones: " + e.getMessage());
            }
        }
    }

    public void crearOnAction(ActionEvent actionEvent) {
        if (validarCampos()) {
            Connection conn = null;
            PreparedStatement pst = null;
            ResultSet rs = null;

            try {
                conn = DatabaseConnection.getConnection();
                String query = "INSERT INTO Videojuego (Nombre, Plataforma, Género, Publicado, Precio, Ventas, BandaSonoraID) " +
                        "VALUES (?, ?, ?, ?, ?, ?, (SELECT ID FROM BandaSonora WHERE Nombre = ?))";
                pst = conn.prepareStatement(query);

                pst.setString(1, nombre.getText());
                pst.setString(2, consola.getText());
                pst.setString(3, genero.getText());
                Date fechaSQL = Date.valueOf(fechaPublicacion.getValue());
                pst.setDate(4, fechaSQL);
                pst.setDouble(5, Double.parseDouble(precio.getText()));
                pst.setInt(6, Integer.parseInt(ventas.getText()));
                pst.setString(7, bandaSonora.getValue());

                int filasAfectadas = pst.executeUpdate();
                
                if (filasAfectadas > 0) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", 
                            "Videojuego creado correctamente");
                    limpiarCampos();
                }

            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error",
                        "Error al crear el videojuego: " + e.getMessage());
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (pst != null) pst.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar las conexiones: " + e.getMessage());
                }
            }
        }
    }

    private boolean validarCampos() {
        if (nombre.getText().isEmpty() || consola.getText().isEmpty() || 
            genero.getText().isEmpty() || fechaPublicacion.getValue() == null || 
            precio.getText().isEmpty() || ventas.getText().isEmpty()) {
            
            mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos", 
                          "Por favor, rellene todos los campos obligatorios");
            return false;
        }

        try {
            Double.parseDouble(precio.getText());
        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.WARNING, "Error de formato", 
                          "El precio debe ser un número válido");
            return false;
        }

        try {
            Integer.parseInt(ventas.getText());
        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.WARNING, "Error de formato", 
                          "Las ventas deben ser un número entero");
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
        consola.clear();
        genero.clear();
        fechaPublicacion.setValue(null);
        precio.clear();
        ventas.clear();
        bandaSonora.setValue(null);
    }

    public void volverOnAction(ActionEvent actionEvent) {
        App.setRoot("listadoVideojuegos");
    }
}
