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

/**
 * Controlador para la gestión de categorías de servicios.
 * Permite realizar operaciones CRUD sobre las categorías y su visualización.
 */
public class GestionarCategoriasControlador {
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, String> colTipo;
    @FXML private TableColumn<Categoria, String> colDescripcion;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;

    private ObservableList<Categoria> categorias = FXCollections.observableArrayList();

    /**
     * Inicializa el controlador configurando la tabla, cargando las categorías
     * y configurando la funcionalidad de búsqueda.
     */
    @FXML
    public void initialize() {
        configurarTabla();
        cargarCategorias();
        configurarBusqueda();
    }

    /**
     * Configura las columnas de la tabla y establece el ordenamiento por defecto.
     */
    private void configurarTabla() {
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tablaCategorias.getSortOrder().add(colTipo);
    }

    /**
     * Configura los eventos de búsqueda para el campo de texto y el botón.
     */
    private void configurarBusqueda() {
        txtBuscar.setOnAction(event -> buscarCategoriaOnAction());
        btnBuscar.setOnAction(event -> buscarCategoriaOnAction());
    }

    /**
     * Carga todas las categorías en la tabla.
     */
    private void cargarCategorias() {
        List<Categoria> listaCategorias = obtenerTodas();
        categorias.clear();
        categorias.addAll(listaCategorias);
        tablaCategorias.setItems(categorias);
        tablaCategorias.refresh();
    }

    /**
     * Obtiene todas las categorías de la base de datos.
     * @return Lista de categorías ordenadas por tipo
     */
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

    /**
     * Maneja el evento de volver al menú de administrador.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void volverOnAction() throws IOException {
        App.setRoot("menuAdmin");
    }

    /**
     * Abre la ventana para añadir una nueva categoría.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void anadirCategoriaOnAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearCategoria.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Nueva Categoría");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarCategorias();
    }

    /**
     * Abre la ventana para modificar una categoría seleccionada.
     * @throws IOException si hay un error al cargar la vista
     */
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
            
            cargarCategorias();
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione una categoría para modificar.");
        }
    }

    /**
     * Maneja la eliminación de una categoría seleccionada.
     */
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

    /**
     * Valida si una categoría puede ser eliminada verificando que no tenga servicios asociados.
     * @param categoria La categoría a validar
     * @return true si la categoría puede ser eliminada, false en caso contrario
     */
    private boolean validarEliminacion(Categoria categoria) {
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

    /**
     * Elimina una categoría de la base de datos.
     * @param id El ID de la categoría a eliminar
     * @throws SQLException si ocurre un error durante la eliminación
     */
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

    /**
     * Maneja la búsqueda de categorías basada en el texto ingresado.
     */
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

    /**
     * Busca categorías en la base de datos que coincidan con el texto de búsqueda.
     * La búsqueda se realiza tanto en el tipo como en la descripción de las categorías.
     * 
     * @param busqueda El texto a buscar en las categorías
     * @return Lista de categorías que coinciden con el criterio de búsqueda
     */
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

    /**
     * Muestra un diálogo de confirmación para la eliminación de una categoría.
     * 
     * @return true si el usuario confirma la eliminación, false en caso contrario
     */
    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar esta categoría?");
        alert.setContentText("Esta acción no se puede deshacer.");
        return alert.showAndWait().get() == ButtonType.OK;
    }

    /**
     * Muestra un mensaje de alerta al usuario.
     * 
     * @param tipo El tipo de alerta a mostrar (ERROR, WARNING, INFORMATION, etc.)
     * @param titulo El título de la ventana de alerta
     * @param contenido El mensaje a mostrar en la alerta
     */
    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
} 