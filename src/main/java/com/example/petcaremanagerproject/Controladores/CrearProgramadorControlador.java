package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Programador;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearProgramadorControlador {

    @FXML
    private TextField dni;
    @FXML
    private TextField nombre;
    @FXML
    private TextField apellido;
    @FXML
    private TextField localidad;
    @FXML
    private TextField salario;

    @FXML
    private void crearOnAction(ActionEvent event) {
        if (validarCampos()) {
            Connection conn = null;
            PreparedStatement pst = null;

            try {
                conn = DatabaseConnection.getConnection();
                String query = "INSERT INTO Programador (Nombre, Apellido, Localidad, Salario,DNI) VALUES (?, ?, ?, ?, ?)";
                pst = conn.prepareStatement(query);

                
                pst.setString(1,nombre.getText());
                pst.setString(2, apellido.getText());
                pst.setString(3, localidad.getText());
                pst.setInt(4, Integer.parseInt(salario.getText()));
                pst.setString(5, dni.getText());

                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Programador creado correctamente");
                    volverAListado();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el programador");
                }

            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al crear el programador: " + e.getMessage());
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
        if (dni.getText().isEmpty() || nombre.getText().isEmpty() || 
            apellido.getText().isEmpty() || localidad.getText().isEmpty() || 
            salario.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", 
                         "Por favor, rellene todos los campos");
            return false;
        }

        try {
            Integer.parseInt(salario.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Salario inválido", 
                         "El salario debe ser un número");
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
        App.setRoot("listadoProgramadores");
    }
}