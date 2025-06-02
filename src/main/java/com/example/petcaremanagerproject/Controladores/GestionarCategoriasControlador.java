package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Categoria;
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

public class GestionarCategoriasControlador {
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colTipo;
    @FXML private TableColumn<Categoria, String> colDescripcion;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;

    private ObservableList<Categoria> categorias = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarCategorias();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Permitir ordenamiento en todas las columnas
        tablaCategorias.getSortOrder().add(colTipo);
    }

    private void configurarBusqueda() {
        // Buscar al presionar Enter en el campo de búsqueda
        txtBuscar.setOnAction(event -> buscarCategoriaOnAction());
        
        // Buscar al hacer clic en el botón
        btnBuscar.setOnAction(event -> buscarCategoriaOnAction());
    }

    private void cargarCategorias() {
        List<Categoria> listaCategorias = obtenerTodas();
        categorias.clear();
        categorias.addAll(listaCategorias);
        tablaCategorias.setItems(categorias);
        tablaCategorias.refresh();
    }

    private List<Categoria> obtenerTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria ORDER BY tipo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categorias.add(new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("tipo"),
                    rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al cargar categorías: " + e.getMessage());
        }
        return categorias;
    }

    @FXML
    private void volverOnAction() throws IOException {
        App.setRoot("menuAdmin");
    }

    @FXML
    private void anadirCategoriaOnAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearCategoria.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Nueva Categoría");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarCategorias(); // Recargar la tabla después de cerrar la ventana de creación
    }

    @FXML
    private void modificarCategoriaOnAction() throws IOException {
        Categoria categoriaSeleccionada = tablaCategorias.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearCategoria.fxml"));
            Scene scene = new Scene(loader.load());
            
            CrearCategoriaControlador controlador = loader.getController();
            controlador.setCategoriaAModificar(categoriaSeleccionada);
            
            Stage stage = new Stage();
            stage.setTitle("Modificar Categoría");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            cargarCategorias(); // Recargar la tabla después de cerrar la ventana de modificación
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione una categoría para modificar.");
        }
    }

    @FXML
    private void eliminarCategoriaOnAction() {
        Categoria categoriaSeleccionada = tablaCategorias.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada != null) {
            if (validarEliminacion(categoriaSeleccionada) && confirmarEliminacion()) {
                try {
                    eliminar(categoriaSeleccionada.getIdCategoria());
                    categorias.remove(categoriaSeleccionada);
                    tablaCategorias.refresh();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Categoría eliminada correctamente");
                } catch (SQLException e) {
                    if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                        mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                            "No se puede eliminar la categoría porque tiene servicios asociados. Elimine primero los servicios.");
                    } else {
                        mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                            "Error al eliminar la categoría: " + e.getMessage());
                    }
                }
            }
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione una categoría para eliminar.");
        }
    }

    private boolean validarEliminacion(Categoria categoria) {
        // Verificar si la categoría tiene servicios asociados
        String sql = "SELECT COUNT(*) FROM servicio WHERE id_categoria = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoria.getIdCategoria());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", 
                    "Esta categoría tiene servicios asociados. Elimine primero los servicios.");
                return false;
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                "Error al validar la eliminación: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new SQLException("No se encontró la categoría para eliminar");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error al eliminar categoría: " + e.getMessage());
            }
        }
    }

    @FXML
    private void buscarCategoriaOnAction() {
        String busqueda = txtBuscar.getText().trim();
        if (!busqueda.isEmpty()) {
            List<Categoria> categoriasEncontradas = buscar(busqueda);
            categorias.clear();
            categorias.addAll(categoriasEncontradas);
        } else {
            cargarCategorias();
        }
    }

    private List<Categoria> buscar(String busqueda) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria WHERE tipo LIKE ? OR descripcion LIKE ? ORDER BY tipo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String busquedaPattern = "%" + busqueda + "%";
            pstmt.setString(1, busquedaPattern);
            pstmt.setString(2, busquedaPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                categorias.add(new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("tipo"),
                    rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al buscar categorías: " + e.getMessage());
        }
        return categorias;
    }

    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar esta categoría?");
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