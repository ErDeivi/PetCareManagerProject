package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Videojuego;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class ModificarVideojuegoControlador implements Initializable {

    @FXML
    private TextField id,nombre,consola,genero,precio,ventas;
    @FXML
    private ComboBox<String> bandaSonora;
    @FXML
    private DatePicker fechaPublicacion;

    private Videojuego videojuego;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarBandasSonoras();
        cargarDatosVideojuego();
    }

    private void cargarBandasSonoras() {
        ObservableList<String> bandasSonoras = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Nombre FROM BandaSonora")) {

            while (rs.next()) {
                bandasSonoras.add(rs.getString("Nombre"));
            }
            bandaSonora.setItems(bandasSonoras);

        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error",
                    "Error al cargar las bandas sonoras: " + e.getMessage());
        }
    }

    private void cargarDatosVideojuego() {
        videojuego = App.getVideojuegoModificar();
        if (videojuego != null) {
            id.setText(String.valueOf(videojuego.getId()));
            nombre.setText(videojuego.getNombre());
            consola.setText(videojuego.getPlataforma());
            genero.setText(videojuego.getGenero());
            fechaPublicacion.setValue(((Date) videojuego.getPublicado()).toLocalDate());
            precio.setText(String.valueOf(videojuego.getPrecio()));
            ventas.setText(String.valueOf(videojuego.getVentas()));
            
            // Cargar banda sonora actual
            cargarBandaSonoraActual();
        }
    }

    private void cargarBandaSonoraActual() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String queryBandaSonora = "SELECT b.Nombre FROM BandaSonora b " +
                    "JOIN Videojuego v ON v.BandaSonoraID = b.ID " +
                    "WHERE v.ID = ?";
            PreparedStatement pst = conn.prepareStatement(queryBandaSonora);
            pst.setInt(1, videojuego.getId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                bandaSonora.setValue(rs.getString("Nombre"));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error",
                    "Error al cargar la banda sonora: " + e.getMessage());
        }
    }

    public void editarOnAction(ActionEvent actionEvent) {
        if (validarCampos()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE Videojuego SET Nombre=?, Plataforma=?, " +
                        "Género=?, Publicado=?, Precio=?, Ventas=?, " +
                        "BandaSonoraID=(SELECT ID FROM BandaSonora WHERE Nombre=?) " +
                        "WHERE ID=?";
                
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, nombre.getText());
                pst.setString(2, consola.getText());
                pst.setString(3, genero.getText());
                pst.setDate(4, Date.valueOf(fechaPublicacion.getValue()));
                pst.setDouble(5, Double.parseDouble(precio.getText()));
                pst.setInt(6, Integer.parseInt(ventas.getText()));
                pst.setString(7, bandaSonora.getValue());
                pst.setInt(8, videojuego.getId());

                int filasAfectadas = pst.executeUpdate();
                
                if (filasAfectadas > 0) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", 
                            "Videojuego modificado correctamente");
                    volverOnAction(null);
                }

            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error",
                        "Error al modificar el videojuego: " + e.getMessage());
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

    public void volverOnAction(ActionEvent actionEvent) {
        App.setRoot("listadoVideojuegos");
    }
}
