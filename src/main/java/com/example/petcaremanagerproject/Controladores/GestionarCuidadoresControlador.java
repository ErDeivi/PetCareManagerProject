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
    @FXML private TableColumn<Cuidador, String> colApellidos;
    @FXML private TableColumn<Cuidador, String> colCorreo;
    @FXML private TableColumn<Cuidador, String> colTelefono;
    @FXML private TableColumn<Cuidador, String> colEspecialidad;
    @FXML private TableColumn<Cuidador, String> colDisponibilidad;
    @FXML private TextField txtBuscar;

    private ObservableList<Cuidador> cuidadores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarCuidadores();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Permitir ordenamiento en todas las columnas visibles
        tablaCuidadores.getSortOrder().add(colNombre);
        tablaCuidadores.setSortPolicy(tableView -> {
            ObservableList<Cuidador> items = FXCollections.observableArrayList(tableView.getItems());
            FXCollections.sort(items, (o1, o2) -> {
                for (TableColumn<Cuidador, ?> column : tableView.getSortOrder()) {
                    if (column == colNombre) {
                        return o1.getNombre().compareTo(o2.getNombre());
                    } else if (column == colCorreo) {
                        return o1.getCorreo().compareTo(o2.getCorreo());
                    } else if (column == colTelefono) {
                         return o1.getTelefono().compareTo(o2.getTelefono());
                    }
                }
                return 0;
            });
            return true;
        });
    }

    private void configurarBusqueda() {
        txtBuscar.setOnAction(event -> buscarCuidadorOnAction());
    }

    private void cargarCuidadores() {
        List<Cuidador> listaCuidadores = obtenerTodos();
        cuidadores.clear();
        cuidadores.addAll(listaCuidadores);
        tablaCuidadores.setItems(cuidadores);
    }

    private List<Cuidador> obtenerTodos() {
        List<Cuidador> cuidadores = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, u.telefono FROM cuidador c JOIN usuario u ON c.id_usuario = u.id_usuario ORDER BY u.nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cuidadores.add(new Cuidador(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("telefono")
                ));
            }   
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar cuidadores", e);
        }
        return cuidadores;
    }

    @FXML
    private void volverOnAction() throws IOException {
        App.setRoot("menuAdmin");
    }

    @FXML
    private void nuevoCuidadorOnAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearCuidador.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Nuevo Cuidador");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarCuidadores(); // Recargar la tabla después de cerrar la ventana de creación
    }

    @FXML
    private void modificarCuidadorOnAction() throws IOException {
        Cuidador cuidadorSeleccionado = tablaCuidadores.getSelectionModel().getSelectedItem();
        if (cuidadorSeleccionado != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearCuidador.fxml"));
            Scene scene = new Scene(loader.load());
            
            CrearCuidadorControlador controlador = loader.getController();
            controlador.setCuidadorAModificar(cuidadorSeleccionado);
            
            Stage stage = new Stage();
            stage.setTitle("Modificar Cuidador");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            cargarCuidadores(); // Recargar la tabla después de cerrar la ventana de modificación
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un cuidador para modificar.");
        }
    }

    @FXML
    private void eliminarCuidadorOnAction() {
        Cuidador cuidadorSeleccionado = tablaCuidadores.getSelectionModel().getSelectedItem();
        if (cuidadorSeleccionado != null) {
            if (confirmarEliminacion()) {
                try {
                    eliminar(cuidadorSeleccionado.getId());
                    cuidadores.remove(cuidadorSeleccionado);
                    tablaCuidadores.refresh(); // Asegurar que la tabla se actualice visualmente
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cuidador eliminado correctamente");
                } catch (SQLException e) {
                     // Manejo básico de error de clave foránea
                    if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                         mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                            "No se puede eliminar el cuidador porque tiene elementos asociados (ej: citas). Elimine primero los elementos asociados.");
                    } else {
                        mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                            "Error al eliminar el cuidador: " + e.getMessage());
                    }
                }
            }
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un cuidador para eliminar.");
        }
    }

    private void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cuidador WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
             conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                 int filasAfectadas = pstmt.executeUpdate();
                 if (filasAfectadas > 0) {
                    conn.commit();
                 } else {
                     conn.rollback();
                    throw new SQLException("No se encontró el cuidador para eliminar");
                 }
            } catch (SQLException e) {
                 conn.rollback();
                 throw new SQLException("Error al eliminar cuidador: " + e.getMessage());
            }
        }
    }

    @FXML
    private void buscarCuidadorOnAction() {
        String busqueda = txtBuscar.getText().trim();
        if (!busqueda.isEmpty()) {
            List<Cuidador> cuidadoresEncontrados = buscar(busqueda);
            cuidadores.clear();
            cuidadores.addAll(cuidadoresEncontrados);
        } else {
            cargarCuidadores();
        }
    }

    private List<Cuidador> buscar(String busqueda) {
        List<Cuidador> cuidadores = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, u.telefono FROM cuidador c JOIN usuario u ON c.id_usuario = u.id_usuario WHERE u.nombre LIKE ? OR u.correo LIKE ? OR u.telefono LIKE ? ORDER BY u.nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String busquedaPattern = "%" + busqueda + "%";
            pstmt.setString(1, busquedaPattern);
            pstmt.setString(2, busquedaPattern);
            pstmt.setString(3, busquedaPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                 cuidadores.add(new Cuidador(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("telefono")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cuidadores", e);
        }
        return cuidadores;
    }

     private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar este cuidador?");
        alert.setContentText("Esta acción eliminará el cuidador y su información de usuario asociada. Asegúrese de que no tiene citas asociadas."); // Nota sobre posible FK
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