package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Cuidador;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestionarCuidadoresControlador {
    @FXML private TableView<Cuidador> tablaCuidadores;
    @FXML private TableColumn<Cuidador, Integer> colId;
    @FXML private TableColumn<Cuidador, String> colNombre;
    @FXML private TableColumn<Cuidador, String> colCorreo;
    @FXML private TableColumn<Cuidador, String> colTelefono;
    @FXML private TableColumn<Cuidador, String> colEspecialidad;
    @FXML private TableColumn<Cuidador, String> colDisponibilidad;

    private ObservableList<Cuidador> cuidadores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarCuidadores();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colDisponibilidad.setCellValueFactory(new PropertyValueFactory<>("disponibilidad"));

        // Permitir ordenamiento en todas las columnas
        tablaCuidadores.getSortOrder().add(colNombre);
    }

    private void cargarCuidadores() {
        List<Cuidador> listaCuidadores = obtenerTodos();
        cuidadores.clear();
        cuidadores.addAll(listaCuidadores);
        tablaCuidadores.setItems(cuidadores);
    }

    private List<Cuidador> obtenerTodos() {
        List<Cuidador> cuidadores = new ArrayList<>();
        String sql = "SELECT * FROM cuidadores ORDER BY apellidos, nombre";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    cuidadores.add(new Cuidador(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("especialidad"),
                        rs.getString("disponibilidad")
                    ));
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al cargar cuidadores", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la conexión a la base de datos", e);
        }
        return cuidadores;
    }

    @FXML
    private void atras() throws IOException {
        App.setRoot("menuAdmin");
    }

    @FXML
    private void nuevoCuidadorOnAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("crearCuidador.fxml"));
        Scene scene = new Scene(loader.load());
        
        Stage stage = new Stage();
        stage.setTitle("Nuevo Cuidador");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        cargarCuidadores();
    }

    @FXML
    private void modificarCuidadorOnAction() throws IOException {
        Cuidador cuidadorSeleccionado = tablaCuidadores.getSelectionModel().getSelectedItem();
        if (cuidadorSeleccionado != null) {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("modificarCuidador.fxml"));
            Scene scene = new Scene(loader.load());
            
            ModificarCuidadorControlador controlador = loader.getController();
            controlador.setCuidadorAModificar(cuidadorSeleccionado);
            
            Stage stage = new Stage();
            stage.setTitle("Modificar Cuidador");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            cargarCuidadores();
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un cuidador para modificar.");
        }
    }

    @FXML
    private void borrarCuidadorOnAction() {
        Cuidador cuidadorSeleccionado = tablaCuidadores.getSelectionModel().getSelectedItem();
        if (cuidadorSeleccionado != null) {
            if (confirmarEliminacion()) {
                try {
                    eliminar(cuidadorSeleccionado.getId());
                    cuidadores.remove(cuidadorSeleccionado);
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cuidador eliminado correctamente");
                } catch (SQLException e) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                        "Error al eliminar el cuidador: " + e.getMessage());
                }
            }
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un cuidador para eliminar.");
        }
    }

    private void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cuidadores WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al eliminar cuidador", e);
            }
        }
    }

    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar este cuidador?");
        alert.setContentText("Esta acción no se puede deshacer.");
        return alert.showAndWait().get() == ButtonType.OK;
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
} 