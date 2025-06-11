package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
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
 * Controlador para la gestión de dueños en la aplicación.
 * Permite realizar operaciones CRUD sobre los dueños y gestionar su visualización.
 */
public class GestionarDuenosControlador {
    @FXML private TableView<Dueno> tablaDuenos;
    @FXML private TableColumn<Dueno, String> colNombre;
    @FXML private TableColumn<Dueno, String> colCorreo;
    @FXML private TableColumn<Dueno, String> colTelefono;
    @FXML private TableColumn<Dueno, String> colImagenUrl;
    @FXML private TextField txtBuscar;

    private ObservableList<Dueno> duenos = FXCollections.observableArrayList();

    /**
     * Inicializa el controlador configurando la tabla y cargando los dueños.
     */
    @FXML
    public void initialize() {
        // Verificar que todos los componentes se han inicializado
        if (tablaDuenos == null) {
            System.err.println("Error: tablaDuenos no se ha inicializado");
            return;
        }

        configurarTabla();
        cargarDuenos();

        // Configurar búsqueda en tiempo real solo si txtBuscar está disponible
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
            List<Dueno> resultados = buscar(textoBusqueda);
            duenos.clear();
            duenos.addAll(resultados);
            if (tablaDuenos != null) {
                tablaDuenos.setItems(duenos);
            }
        } else {
            cargarDuenos(); // Mostrar todos si no hay búsqueda
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
        if (colNombre == null || colCorreo == null || colTelefono == null || colImagenUrl == null) {
            System.err.println("Error: Una o más columnas no se han inicializado");
            return;
        }

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colImagenUrl.setCellValueFactory(new PropertyValueFactory<>("imagenUrl"));

        if (tablaDuenos != null) {
            tablaDuenos.getSortOrder().add(colNombre);
        }
    }

    /**
     * Carga los dueños desde la base de datos y los muestra en la tabla.
     */
    private void cargarDuenos() {
        try {
            List<Dueno> listaDuenos = obtenerTodos();
            duenos.clear();
            duenos.addAll(listaDuenos);

            if (tablaDuenos != null) {
                tablaDuenos.setItems(duenos);
            } else {
                System.err.println("Error: tablaDuenos es null, no se pueden mostrar los datos");
            }
        } catch (Exception e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error",
                    "Error al cargar los dueños: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene todos los dueños de la base de datos.
     * @return Lista de dueños ordenados por nombre
     */
    private List<Dueno> obtenerTodos() {
        List<Dueno> duenos = new ArrayList<>();
        String sql = """
            SELECT u.id_usuario, u.nombre, u.correo, u.telefono, u.imagen_url
            FROM dueño d
            JOIN usuario u ON d.id_usuario = u.id_usuario
            ORDER BY u.nombre
        """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    duenos.add(new Dueno(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getString("telefono"),
                            rs.getString("imagen_url")
                    ));
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al cargar dueños", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la conexión a la base de datos", e);
        }
        return duenos;
    }

    /**
     * Busca dueños que coincidan con el criterio de búsqueda.
     * @param busqueda Texto a buscar en nombre, correo o teléfono
     * @return Lista de dueños que coinciden con la búsqueda
     */
    private List<Dueno> buscar(String busqueda) {
        List<Dueno> duenos = new ArrayList<>();
        String sql = """
            SELECT u.id_usuario, u.nombre, u.correo, u.telefono, u.imagen_url
            FROM dueño d
            JOIN usuario u ON d.id_usuario = u.id_usuario
            WHERE u.nombre LIKE ?
            OR u.correo LIKE ?
            OR u.telefono LIKE ?
            ORDER BY u.nombre
        """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String busquedaPattern = "%" + busqueda + "%";
                pstmt.setString(1, busquedaPattern);
                pstmt.setString(2, busquedaPattern);
                pstmt.setString(3, busquedaPattern);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    duenos.add(new Dueno(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getString("telefono"),
                            rs.getString("imagen_url")
                    ));
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al buscar dueños", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la conexión a la base de datos", e);
        }
        return duenos;
    }

    /**
     * Navega de vuelta al menú de administrador.
     * @throws IOException Si hay un error al cargar la vista
     */
    @FXML
    private void atras() throws IOException {
        App.setRoot("menuAdmin");
    }

    /**
     * Abre la ventana para crear un nuevo dueño.
     * @throws IOException Si hay un error al cargar la vista
     */
    @FXML
    private void nuevoDuenoOnAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearDueno.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Nuevo Dueño");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarDuenos();
    }

    /**
     * Abre la ventana para modificar el dueño seleccionado.
     * @throws IOException Si hay un error al cargar la vista
     */
    @FXML
    private void modificarDuenoOnAction() throws IOException {
        if (tablaDuenos == null) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "La tabla no está disponible");
            return;
        }

        Dueno duenoSeleccionado = tablaDuenos.getSelectionModel().getSelectedItem();
        if (duenoSeleccionado != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/modificarDueno.fxml"));
            Scene scene = new Scene(loader.load());

            ModificarDuenoControlador controlador = loader.getController();
            controlador.setDuenoAModificar(duenoSeleccionado);

            Stage stage = new Stage();
            stage.setTitle("Modificar Dueño");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarDuenos();
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un dueño para modificar.");
        }
    }

    /**
     * Elimina el dueño seleccionado después de confirmar la acción.
     */
    @FXML
    private void borrarDuenoOnAction() {
        if (tablaDuenos == null) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "La tabla no está disponible");
            return;
        }

        Dueno duenoSeleccionado = tablaDuenos.getSelectionModel().getSelectedItem();
        if (duenoSeleccionado != null) {
            if (confirmarEliminacion()) {
                try {
                    eliminar(duenoSeleccionado.getIdUsuario());
                    duenos.remove(duenoSeleccionado);
                    tablaDuenos.refresh();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Dueño eliminado correctamente");
                } catch (SQLException e) {
                    if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                        mostrarMensaje(Alert.AlertType.ERROR, "Error",
                                "No se puede eliminar el dueño porque tiene mascotas asociadas. Elimine primero las mascotas.");
                    } else {
                        mostrarMensaje(Alert.AlertType.ERROR, "Error",
                                "Error al eliminar el dueño: " + e.getMessage());
                    }
                }
            }
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un dueño para eliminar.");
        }
    }

    /**
     * Elimina un dueño de la base de datos.
     * @param id El ID del dueño a eliminar
     * @throws SQLException si ocurre un error durante la eliminación
     */
    private void eliminar(int id) throws SQLException {
        String sqlDueno = "DELETE FROM dueño WHERE id_usuario = ?";
        String sqlUsuario = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Primero eliminar de la tabla dueño
                try (PreparedStatement pstmtDueno = conn.prepareStatement(sqlDueno)) {
                    pstmtDueno.setInt(1, id);
                    int filasAfectadas = pstmtDueno.executeUpdate();
                    if (filasAfectadas > 0) {
                        // Luego eliminar de la tabla usuario
                        try (PreparedStatement pstmtUsuario = conn.prepareStatement(sqlUsuario)) {
                            pstmtUsuario.setInt(1, id);
                            pstmtUsuario.executeUpdate();
                        }
                        conn.commit();
                    } else {
                        conn.rollback();
                        throw new SQLException("No se encontró el dueño para eliminar");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error al eliminar el dueño: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra un diálogo de confirmación para la eliminación de un dueño.
     * El diálogo incluye una advertencia sobre las mascotas asociadas.
     *
     * @return true si el usuario confirma la eliminación, false en caso contrario
     */
    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar este dueño?");
        alert.setContentText("Esta acción no se puede deshacer.\n\nNota: Si este dueño tiene mascotas asociadas, no podrá eliminarlo hasta que elimine las mascotas.");
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