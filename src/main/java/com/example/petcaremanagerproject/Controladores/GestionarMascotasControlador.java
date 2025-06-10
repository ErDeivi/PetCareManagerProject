package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Mascota;
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
 * Controlador para la gestión de mascotas en la aplicación.
 * Permite realizar operaciones CRUD sobre las mascotas y gestionar su visualización.
 */
public class GestionarMascotasControlador {
    @FXML private TableView<Mascota> tablaMascotas;
    @FXML private TableColumn<Mascota, String> colNombre;
    @FXML private TableColumn<Mascota, String> colEspecie;
    @FXML private TableColumn<Mascota, String> colRaza;
    @FXML private TableColumn<Mascota, Integer> colEdad;
    @FXML private TableColumn<Mascota, Double> colPeso;
    @FXML private TableColumn<Mascota, String> colCliente;
    @FXML private TableColumn<Mascota, String> colImagenUrl;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private ComboBox<String> cmbFiltros;

    private ObservableList<Mascota> mascotas = FXCollections.observableArrayList();

    /**
     * Inicializa el controlador configurando la tabla, cargando las mascotas
     * y configurando los componentes de búsqueda y filtrado.
     */
    @FXML
    public void initialize() {
        configurarTabla();
        cargarMascotas();
        configurarBusqueda();
        configurarFiltros();
    }

    /**
     * Configura las columnas de la tabla y establece el ordenamiento.
     */
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreDueno"));
        colImagenUrl.setCellValueFactory(new PropertyValueFactory<>("imagenUrl"));

        tablaMascotas.getSortOrder().add(colNombre);
        tablaMascotas.setSortPolicy(tableView -> {
            ObservableList<Mascota> items = FXCollections.observableArrayList(tableView.getItems());
            FXCollections.sort(items, (o1, o2) -> {
                for (TableColumn<Mascota, ?> column : tableView.getSortOrder()) {
                    if (column == colNombre) {
                        return o1.getNombre().compareTo(o2.getNombre());
                    } else if (column == colEspecie) {
                        return o1.getEspecie().compareTo(o2.getEspecie());
                    } else if (column == colRaza) {
                        return o1.getRaza().compareTo(o2.getRaza());
                    } else if (column == colEdad) {
                        return Integer.compare(o1.getEdad(), o2.getEdad());
                    } else if (column == colPeso) {
                        return Double.compare(o1.getPeso(), o2.getPeso());
                    } else if (column == colCliente) {
                        return o1.getNombreDueno().compareTo(o2.getNombreDueno());
                    }
                }
                return 0;
            });
            return true;
        });
    }

    /**
     * Configura los eventos de búsqueda para el campo de texto y el botón.
     */
    private void configurarBusqueda() {
        txtBuscar.setOnAction(event -> buscarMascotaOnAction());
        btnBuscar.setOnAction(event -> buscarMascotaOnAction());
    }

    /**
     * Configura los filtros disponibles en el ComboBox.
     */
    private void configurarFiltros() {
        cmbFiltros.getItems().addAll(
            "Todas las mascotas",
            "Mascotas jóvenes (0-3 años)",
            "Mascotas adultas (3 o más años)",
            "Perros",
            "Gatos",
            "Mascotas por peso (más de 10kg)",
            "Mascotas sin imagen"
        );
        cmbFiltros.setValue("Todas las mascotas");
        cmbFiltros.setOnAction(e -> aplicarFiltro());
    }

    /**
     * Aplica el filtro seleccionado a la lista de mascotas.
     */
    private void aplicarFiltro() {
        String filtroSeleccionado = cmbFiltros.getValue();
        List<Mascota> mascotasFiltradas = new ArrayList<>();

        switch (filtroSeleccionado) {
            case "Todas las mascotas":
                mascotasFiltradas = obtenerTodas();
                break;
            case "Mascotas jóvenes (0-3 años)":
                mascotasFiltradas = obtenerMascotasPorEdad(0, 3);
                break;
            case "Mascotas adultas (3 o más años)":
                mascotasFiltradas = obtenerMascotasPorEdad(3, 100);
                break;
            case "Perros":
                mascotasFiltradas = obtenerMascotasPorEspecie("Perro");
                break;
            case "Gatos":
                mascotasFiltradas = obtenerMascotasPorEspecie("Gato");
                break;
            case "Mascotas por peso (más de 10kg)":
                mascotasFiltradas = obtenerMascotasPorPeso(10.0);
                break;
            case "Mascotas sin imagen":
                mascotasFiltradas = obtenerMascotasSinImagen();
                break;
        }

        mascotas.clear();
        mascotas.addAll(mascotasFiltradas);
        tablaMascotas.refresh();
    }

    /**
     * Obtiene las mascotas dentro de un rango de edad específico.
     * @param edadMin Edad mínima del rango
     * @param edadMax Edad máxima del rango
     * @return Lista de mascotas que cumplen con el criterio de edad
     */
    private List<Mascota> obtenerMascotasPorEdad(int edadMin, int edadMax) {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = """
            SELECT m.id_mascota, m.nombre AS mascota_nombre, m.especie, m.raza, m.edad, m.peso, u.nombre AS dueno_nombre, m.id_dueño, m.imagen_url
            FROM mascota m 
            JOIN dueño d ON m.id_dueño = d.id_usuario
            JOIN usuario u ON d.id_usuario = u.id_usuario
            WHERE m.edad >= ? AND m.edad < ?
            ORDER BY m.nombre
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, edadMin);
            pstmt.setInt(2, edadMax);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                mascotas.add(new Mascota(
                    rs.getInt("id_mascota"),
                    rs.getString("mascota_nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getInt("edad"),
                    rs.getDouble("peso"),
                    rs.getInt("id_dueño"),
                    rs.getString("imagen_url"),
                    rs.getString("dueno_nombre")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al filtrar mascotas por edad: " + e.getMessage());
        }
        return mascotas;
    }

    /**
     * Obtiene las mascotas de una especie específica.
     * @param especie Especie de mascota a filtrar
     * @return Lista de mascotas de la especie especificada
     */
    private List<Mascota> obtenerMascotasPorEspecie(String especie) {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = """
            SELECT m.id_mascota, m.nombre AS mascota_nombre, m.especie, m.raza, m.edad, m.peso, u.nombre AS dueno_nombre, m.id_dueño, m.imagen_url
            FROM mascota m 
            JOIN dueño d ON m.id_dueño = d.id_usuario
            JOIN usuario u ON d.id_usuario = u.id_usuario
            WHERE m.especie = ?
            ORDER BY m.nombre
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, especie);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                mascotas.add(new Mascota(
                    rs.getInt("id_mascota"),
                    rs.getString("mascota_nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getInt("edad"),
                    rs.getDouble("peso"),
                    rs.getInt("id_dueño"),
                    rs.getString("imagen_url"),
                    rs.getString("dueno_nombre")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al filtrar mascotas por especie: " + e.getMessage());
        }
        return mascotas;
    }

    /**
     * Obtiene las mascotas que tienen un peso mayor o igual al especificado.
     * @param pesoMin Peso mínimo para filtrar las mascotas
     * @return Lista de mascotas que cumplen con el criterio de peso
     */
    private List<Mascota> obtenerMascotasPorPeso(double pesoMin) {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = """
            SELECT m.id_mascota, m.nombre AS mascota_nombre, m.especie, m.raza, m.edad, m.peso, u.nombre AS dueno_nombre, m.id_dueño, m.imagen_url
            FROM mascota m 
            JOIN dueño d ON m.id_dueño = d.id_usuario
            JOIN usuario u ON d.id_usuario = u.id_usuario
            WHERE m.peso >= ?
            ORDER BY m.peso DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, pesoMin);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                mascotas.add(new Mascota(
                    rs.getInt("id_mascota"),
                    rs.getString("mascota_nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getInt("edad"),
                    rs.getDouble("peso"),
                    rs.getInt("id_dueño"),
                    rs.getString("imagen_url"),
                    rs.getString("dueno_nombre")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al filtrar mascotas por peso: " + e.getMessage());
        }
        return mascotas;
    }

    /**
     * Obtiene las mascotas que no tienen imagen asociada.
     * @return Lista de mascotas sin imagen
     */
    private List<Mascota> obtenerMascotasSinImagen() {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = """
            SELECT m.id_mascota, m.nombre AS mascota_nombre, m.especie, m.raza, m.edad, m.peso, u.nombre AS dueno_nombre, m.id_dueño, m.imagen_url
            FROM mascota m 
            JOIN dueño d ON m.id_dueño = d.id_usuario
            JOIN usuario u ON d.id_usuario = u.id_usuario
            WHERE m.imagen_url IS NULL OR m.imagen_url = ''
            ORDER BY m.nombre
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                mascotas.add(new Mascota(
                    rs.getInt("id_mascota"),
                    rs.getString("mascota_nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getInt("edad"),
                    rs.getDouble("peso"),
                    rs.getInt("id_dueño"),
                    rs.getString("imagen_url"),
                    rs.getString("dueno_nombre")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al filtrar mascotas sin imagen: " + e.getMessage());
        }
        return mascotas;
    }

    /**
     * Carga todas las mascotas en la tabla de la interfaz gráfica.
     */
    private void cargarMascotas() {
        List<Mascota> listaMascotas = obtenerTodas();
        System.out.println("Cargadas " + listaMascotas.size() + " mascotas."); // Debugging line
        mascotas.clear();
        mascotas.addAll(listaMascotas);
        tablaMascotas.setItems(mascotas);
        tablaMascotas.refresh();
    }

    /**
     * Obtiene todas las mascotas de la base de datos.
     * @return Lista con todas las mascotas ordenadas por nombre
     * @throws RuntimeException si ocurre un error al cargar las mascotas
     */
    private List<Mascota> obtenerTodas() {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = """
            SELECT m.id_mascota, m.nombre AS mascota_nombre, m.especie, m.raza, m.edad, m.peso, u.nombre AS dueno_nombre, m.id_dueño, m.imagen_url
            FROM mascota m 
            JOIN dueño d ON m.id_dueño = d.id_usuario
            JOIN usuario u ON d.id_usuario = u.id_usuario
            ORDER BY m.nombre
        """;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    String nombreDueno = rs.getString("dueno_nombre");

                    mascotas.add(new Mascota(
                        rs.getInt("id_mascota"),
                        rs.getString("mascota_nombre"),
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getInt("edad"),
                        rs.getDouble("peso"),
                        rs.getInt("id_dueño"),
                        rs.getString("imagen_url"),
                        nombreDueno
                    ));
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al cargar mascotas", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la conexión a la base de datos", e);
        }
        return mascotas;
    }

    /**
     * Maneja el evento de volver al menú de administrador.
     * @throws IOException si ocurre un error al cargar la vista del menú
     */
    @FXML
    private void volverOnAction() throws IOException {
        App.setRoot("menuAdmin");
    }

    /**
     * Maneja el evento de añadir una nueva mascota.
     * Abre una nueva ventana para crear una mascota.
     * @throws IOException si ocurre un error al cargar la vista de creación
     */
    @FXML
    private void anadirMascotaOnAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearMascota.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Nueva Mascota");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarMascotas();
    }

    /**
     * Maneja el evento de modificar una mascota existente.
     * Abre una nueva ventana para modificar la mascota seleccionada.
     * @throws IOException si ocurre un error al cargar la vista de modificación
     */
    @FXML
    private void modificarMascotaOnAction() throws IOException {
        Mascota mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearMascota.fxml"));
            Scene scene = new Scene(loader.load());
            
            CrearMascotaControlador controlador = loader.getController();
            controlador.setMascotaAModificar(mascotaSeleccionada);
            
            Stage stage = new Stage();
            stage.setTitle("Modificar Mascota");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            cargarMascotas();
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione una mascota para modificar.");
        }
    }

    /**
     * Maneja el evento de eliminar una mascota.
     * Realiza las validaciones necesarias y elimina la mascota seleccionada.
     */
    @FXML
    private void eliminarMascotaOnAction() {
        Mascota mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            if (validarEliminacion(mascotaSeleccionada) && confirmarEliminacion()) {
                try {
                    eliminar(mascotaSeleccionada.getIdMascota());
                    mascotas.remove(mascotaSeleccionada);
                    tablaMascotas.refresh();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Mascota eliminada correctamente");
                } catch (SQLException e) {
                    if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                        mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                            "No se puede eliminar la mascota porque tiene servicios asociados. Elimine primero los servicios.");
                    } else {
                        mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                            "Error al eliminar la mascota: " + e.getMessage());
                    }
                }
            }
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione una mascota para eliminar.");
        }
    }

    /**
     * Valida si una mascota puede ser eliminada verificando si tiene servicios asociados.
     * @param mascota La mascota a validar
     * @return true si la mascota puede ser eliminada, false si tiene servicios asociados
     */
    private boolean validarEliminacion(Mascota mascota) {
        // Verificar si la mascota tiene servicios asociados
        String sql = "SELECT COUNT(*) FROM servicio WHERE id_mascota = ?"; // Table name is 'servicio'
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, mascota.getIdMascota());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", 
                    "Esta mascota tiene servicios asociados. Elimine primero los servicios.");
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
     * Elimina una mascota de la base de datos.
     * @param id El ID de la mascota a eliminar
     * @throws SQLException si ocurre un error durante la eliminación
     */
    private void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM mascota WHERE id_mascota = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    conn.commit();
                } else {
                     conn.rollback();
                    throw new SQLException("No se encontró la mascota para eliminar");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error al eliminar mascota: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja el evento de búsqueda de mascotas.
     * Actualiza la tabla con los resultados de la búsqueda o carga todas las mascotas si la búsqueda está vacía.
     */
    @FXML
    private void buscarMascotaOnAction() {
        String busqueda = txtBuscar.getText().trim();
        if (!busqueda.isEmpty()) {
            List<Mascota> mascotasEncontradas = buscar(busqueda);
            mascotas.clear();
            mascotas.addAll(mascotasEncontradas);
        } else {
            cargarMascotas();
        }
    }

    /**
     * Busca mascotas que coincidan con el criterio de búsqueda.
     * @param busqueda El texto de búsqueda
     * @return Lista de mascotas que coinciden con el criterio de búsqueda
     */
    private List<Mascota> buscar(String busqueda) {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = """
            SELECT m.id_mascota, m.nombre AS mascota_nombre, m.especie, m.raza, m.edad, m.peso, u.nombre AS dueno_nombre, m.id_dueño, m.imagen_url
            FROM mascota m 
            JOIN dueño d ON m.id_dueño = d.id_usuario
            JOIN usuario u ON d.id_usuario = u.id_usuario
            WHERE m.nombre LIKE ? 
            OR m.especie LIKE ? 
            OR m.raza LIKE ? 
            OR u.nombre LIKE ?
            ORDER BY m.nombre
        """;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String busquedaPattern = "%" + busqueda + "%";
                pstmt.setString(1, busquedaPattern);
                pstmt.setString(2, busquedaPattern);
                pstmt.setString(3, busquedaPattern);
                pstmt.setString(4, busquedaPattern);
                
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    // Usar el alias 'dueno_nombre' para obtener el nombre del dueño
                    String nombreDueno = rs.getString("dueno_nombre");
                    mascotas.add(new Mascota(
                        rs.getInt("id_mascota"),
                        rs.getString("mascota_nombre"),
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getInt("edad"),
                        rs.getDouble("peso"),
                        rs.getInt("id_dueño"),
                        rs.getString("imagen_url"),
                        nombreDueno
                    ));
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al buscar mascotas", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la conexión a la base de datos", e);
        }
        return mascotas;
    }

    /**
     * Muestra un diálogo de confirmación para la eliminación de una mascota.
     * @return true si el usuario confirma la eliminación, false en caso contrario
     */
    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar esta mascota?");
        alert.setContentText("Esta acción no se puede deshacer.");
        return alert.showAndWait().get() == ButtonType.OK;
    }

    /**
     * Muestra un mensaje de alerta al usuario.
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