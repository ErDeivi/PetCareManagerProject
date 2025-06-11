package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Cuidador;
import com.example.petcaremanagerproject.Modelo.Dueno;
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
 * Controlador para la gestión de cuidadores en la aplicación.
 * Permite realizar operaciones CRUD sobre los cuidadores y gestionar su visualización.
 */
public class GestionarCuidadoresControlador {
    @FXML private TableView<Cuidador> tablaCuidadores;
    @FXML private TableColumn<Cuidador, String> colNombre;
    @FXML private TableColumn<Cuidador, String> colCorreo;
    @FXML private TableColumn<Cuidador, String> colTelefono;
    @FXML private TableColumn<Cuidador, String> colImagenUrl;
    @FXML private TextField txtBuscar;

    private ObservableList<Cuidador> cuidadores = FXCollections.observableArrayList();

    /**
     * Inicializa el controlador configurando la tabla, cargando los cuidadores
     * y configurando la funcionalidad de búsqueda.
     */
    @FXML
    public void initialize() {
        if (tablaCuidadores == null) {
            System.err.println("Error: tablaCuidadores no se ha inicializado");
            return;
        }
        configurarTabla();
        cargarCuidadores();
        configurarBusquedaEnTiempoReal();
    }
    /**
     * Configura la búsqueda en tiempo real si el campo de texto está disponible
     */
    private void configurarBusquedaEnTiempoReal() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    realizarBusqueda();
                } catch (Exception e) {
                    System.err.println("Error en búsqueda en tiempo real: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Advertencia: txtBuscar no está disponible para búsqueda en tiempo real");
        }
    }

    /**
     * Realiza la búsqueda basada en el texto ingresado
     */
    private void realizarBusqueda() {
        String textoBusqueda = obtenerTextoBusqueda();

        if (!textoBusqueda.isEmpty()) {
            List<Cuidador> resultados = buscar(textoBusqueda);
            cuidadores.clear();
            cuidadores.addAll(resultados);
            if (tablaCuidadores != null) {
                tablaCuidadores.setItems(cuidadores);
            }
        } else {
            cargarCuidadores(); // Mostrar todos si no hay búsqueda
        }
    }

    /**
     * Obtiene el texto de búsqueda de forma segura
     * @return El texto de búsqueda o cadena vacía si no está disponible
     */
    private String obtenerTextoBusqueda() {
        if (txtBuscar == null) {
            System.out.println("Advertencia: txtBuscar es null, retornando cadena vacía");
            return "";
        }

        String texto = txtBuscar.getText();
        return (texto != null) ? texto.trim() : "";
    }

    /**
     * Configura las columnas de la tabla y establece el ordenamiento por defecto.
     */
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colImagenUrl.setCellValueFactory(new PropertyValueFactory<>("imagenUrl"));

        // Permitir ordenamiento
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



    /**
     * Carga los cuidadores desde la base de datos y los muestra en la tabla.
     */
    private void cargarCuidadores() {
        List<Cuidador> listaCuidadores = obtenerTodos();
        cuidadores.clear();
        cuidadores.addAll(listaCuidadores);
        tablaCuidadores.setItems(cuidadores);
    }

    /**
     * Obtiene todos los cuidadores de la base de datos.
     * @return Lista de cuidadores ordenados por nombre
     */
    private List<Cuidador> obtenerTodos() {
        List<Cuidador> cuidadores = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, u.contraseña, u.telefono, u.imagen_url FROM usuario u JOIN cuidador c ON u.id_usuario = c.id_usuario ORDER BY u.nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cuidadores.add(new Cuidador(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("contraseña"),
                    rs.getString("telefono"),
                    rs.getString("imagen_url")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al cargar cuidadores: " + e.getMessage());
        }
        return cuidadores;
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
     * Abre la ventana para añadir un nuevo cuidador.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void anadirCuidadorOnAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearCuidador.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Nuevo Cuidador");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarCuidadores();
    }

    /**
     * Abre la ventana para modificar un cuidador seleccionado.
     * @throws IOException si hay un error al cargar la vista
     */
    @FXML
    private void modificarCuidadorOnAction() throws IOException {
        Cuidador cuidadorSeleccionado = tablaCuidadores.getSelectionModel().getSelectedItem();
        if (cuidadorSeleccionado != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/modificarCuidador.fxml"));
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

    /**
     * Maneja la eliminación de un cuidador seleccionado.
     */
    @FXML
    private void eliminarCuidadorOnAction() {
        Cuidador cuidadorSeleccionado = tablaCuidadores.getSelectionModel().getSelectedItem();
        if (cuidadorSeleccionado != null) {
            if (validarEliminacion(cuidadorSeleccionado) && confirmarEliminacion()) {
                try {
                    eliminar(cuidadorSeleccionado.getIdUsuario());
                    cuidadores.remove(cuidadorSeleccionado);
                    tablaCuidadores.refresh();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cuidador eliminado correctamente");
                } catch (SQLException e) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al eliminar el cuidador: " + e.getMessage());
                }
            }
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un cuidador para eliminar.");
        }
    }

    /**
     * Valida si un cuidador puede ser eliminado verificando si tiene servicios asociados.
     * @param cuidador El cuidador a validar
     * @return true si el cuidador puede ser eliminado, false en caso contrario
     */
    private boolean validarEliminacion(Cuidador cuidador) {
        String sql = "SELECT COUNT(*) FROM servicio WHERE id_cuidador = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cuidador.getIdUsuario());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", 
                    "Este cuidador tiene servicios asociados. Elimine primero los servicios.");
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
     * Elimina un usuario de la base de datos.
     * @param idUsuario El ID del usuario a eliminar
     * @throws SQLException si ocurre un error durante la eliminación
     */
    private void eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.executeUpdate();
        }
    }

    /**
     * Maneja la búsqueda de cuidadores basada en el texto ingresado.
     * Si el campo de búsqueda está vacío, recarga todos los cuidadores.
     */
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

    /**
     * Busca cuidadores en la base de datos que coincidan con el texto de búsqueda.
     * La búsqueda se realiza en nombre, correo y teléfono de los cuidadores.
     * 
     * @param busqueda El texto a buscar en los cuidadores
     * @return Lista de cuidadores que coinciden con el criterio de búsqueda
     */
    private List<Cuidador> buscar(String busqueda) {
        List<Cuidador> cuidadores = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, u.contraseña, u.telefono, u.imagen_url FROM usuario u JOIN cuidador c ON u.id_usuario = c.id_usuario WHERE u.nombre LIKE ? OR u.correo LIKE ? OR u.telefono LIKE ? ORDER BY u.nombre";
        
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
                    rs.getString("contraseña"),
                    rs.getString("telefono"),
                    rs.getString("imagen_url")
                 ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al buscar cuidadores: " + e.getMessage());
        }
        return cuidadores;
    }

    /**
     * Muestra un diálogo de confirmación para la eliminación de un cuidador.
     * 
     * @return true si el usuario confirma la eliminación, false en caso contrario
     */
    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar este cuidador?");
        alert.setContentText("Esta acción eliminará permanentemente el cuidador y su información de usuario asociada.");
        return alert.showAndWait().get() == ButtonType.OK;
    }

    /**
     * Muestra un mensaje de alerta al usuario.
     * 
     * @param tipo El tipo de alerta a mostrar (ERROR, WARNING, INFORMATION, etc.)
     * @param titulo El título de la ventana de alerta
     * @param contenido El contenido del mensaje a mostrar
     */
    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
} 