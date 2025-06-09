package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Categoria;
import com.example.petcaremanagerproject.Modelo.Mascota;
import com.example.petcaremanagerproject.Modelo.Servicio;
import com.example.petcaremanagerproject.Modelo.Usuario;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestionarServiciosControlador {
    @FXML private TableView<Servicio> tablaServicios;
    @FXML private TableColumn<Servicio, String> colCategoria;
    @FXML private TableColumn<Servicio, String> colEstado;
    @FXML private TableColumn<Servicio, String> colObservaciones;
    @FXML private TableColumn<Servicio, String> colMascota;
    @FXML private TableColumn<Servicio, String> colCuidador;
    @FXML private TableColumn<Servicio, String> colDueno;
    @FXML private TableColumn<Servicio, LocalDateTime> colFechaSolicitud;
    @FXML private TableColumn<Servicio, LocalDateTime> colFechaProgramada;
    @FXML private TableColumn<Servicio, LocalDateTime> colFechaRealizacion;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private ComboBox<String> cmbFiltros;

    private ObservableList<Servicio> servicios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarServicios();
        configurarBusqueda();
        configurarFiltros();
    }

    private void configurarTabla() {
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
        colMascota.setCellValueFactory(new PropertyValueFactory<>("nombreMascota"));
        colCuidador.setCellValueFactory(new PropertyValueFactory<>("nombreCuidador"));
        colDueno.setCellValueFactory(new PropertyValueFactory<>("nombreDueno"));
        colFechaSolicitud.setCellValueFactory(new PropertyValueFactory<>("fechaSolicitud"));
        colFechaProgramada.setCellValueFactory(new PropertyValueFactory<>("fechaProgramada"));
        colFechaRealizacion.setCellValueFactory(new PropertyValueFactory<>("fechaRealizacion"));

        tablaServicios.getSortOrder().add(colFechaSolicitud);
    }

    private void configurarBusqueda() {
        txtBuscar.setOnAction(event -> buscarServicioOnAction());
        btnBuscar.setOnAction(event -> buscarServicioOnAction());
    }

    private void configurarFiltros() {
        cmbFiltros.getItems().addAll(
            "Todos los servicios",
            "Servicios pendientes",
            "Servicios programados",
            "Servicios completados"
        );
        cmbFiltros.setValue("Todos los servicios");
        cmbFiltros.setOnAction(e -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        String filtroSeleccionado = cmbFiltros.getValue();
        List<Servicio> serviciosFiltrados = new ArrayList<>();

        switch (filtroSeleccionado) {
            case "Todos los servicios":
                serviciosFiltrados = obtenerTodos();
                break;
            case "Servicios pendientes":
                serviciosFiltrados = obtenerServiciosPorEstado("Pendiente");
                break;
            case "Servicios programados":
                serviciosFiltrados = obtenerServiciosPorEstado("Programado");
                break;
            case "Servicios completados":
                serviciosFiltrados = obtenerServiciosPorEstado("Completado");
                break;
        }

        servicios.clear();
        servicios.addAll(serviciosFiltrados);
        tablaServicios.refresh();
    }

    private List<Servicio> obtenerServiciosPorEstado(String estado) {
        List<Servicio> servicios = new ArrayList<>();
        String sql = """
            SELECT s.id_servicio, s.id_categoria, s.id_mascota, s.id_cuidador, s.id_dueño, c.tipo AS nombre_categoria, s.estado, s.observaciones, 
                   m.nombre AS nombre_mascota, u_cuidador.nombre AS nombre_cuidador, u_dueno.nombre AS nombre_dueno,
                   s.fecha_solicitud, s.fecha_programada, s.fecha_realizacion
            FROM servicio s
            JOIN categoria c ON s.id_categoria = c.id_categoria
            JOIN mascota m ON s.id_mascota = m.id_mascota
            JOIN usuario u_cuidador ON s.id_cuidador = u_cuidador.id_usuario
            JOIN usuario u_dueno ON s.id_dueño = u_dueno.id_usuario
            WHERE s.estado = ?
            ORDER BY s.fecha_solicitud DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String nombreCategoria = rs.getString("nombre_categoria");
                String nombreMascota = rs.getString("nombre_mascota");
                String nombreCuidador = rs.getString("nombre_cuidador");
                String nombreDueno = rs.getString("nombre_dueno");

                servicios.add(new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getInt("id_categoria"),
                    rs.getTimestamp("fecha_solicitud").toLocalDateTime(),
                    rs.getTimestamp("fecha_programada").toLocalDateTime(),
                    rs.getTimestamp("fecha_realizacion") != null ? rs.getTimestamp("fecha_realizacion").toLocalDateTime() : null,
                    rs.getString("estado"),
                    rs.getString("observaciones"),
                    rs.getInt("id_mascota"),
                    rs.getInt("id_cuidador"),
                    rs.getInt("id_dueño"),
                    nombreCategoria, 
                    nombreMascota, 
                    nombreCuidador, 
                    nombreDueno 
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al filtrar servicios por estado: " + e.getMessage());
        }
        return servicios;
    }

    private void cargarServicios() {
        List<Servicio> listaServicios = obtenerTodos();
        System.out.println("Cargados " + listaServicios.size() + " servicios."); // Debugging line
        servicios.clear();
        servicios.addAll(listaServicios);
        tablaServicios.setItems(servicios);
        tablaServicios.refresh();
    }

    private List<Servicio> obtenerTodos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = """
            SELECT s.id_servicio, s.id_categoria, s.id_mascota, s.id_cuidador, s.id_dueño, c.tipo AS nombre_categoria, s.estado, s.observaciones, 
                   m.nombre AS nombre_mascota, u_cuidador.nombre AS nombre_cuidador, u_dueno.nombre AS nombre_dueno,
                   s.fecha_solicitud, s.fecha_programada, s.fecha_realizacion
            FROM servicio s
            JOIN categoria c ON s.id_categoria = c.id_categoria
            JOIN mascota m ON s.id_mascota = m.id_mascota
            JOIN usuario u_cuidador ON s.id_cuidador = u_cuidador.id_usuario
            JOIN usuario u_dueno ON s.id_dueño = u_dueno.id_usuario
            ORDER BY s.fecha_solicitud DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    String nombreCategoria = rs.getString("nombre_categoria");
                    String nombreMascota = rs.getString("nombre_mascota");
                    String nombreCuidador = rs.getString("nombre_cuidador");
                    String nombreDueno = rs.getString("nombre_dueno");

                    servicios.add(new Servicio(
                        rs.getInt("id_servicio"),
                        rs.getInt("id_categoria"),
                        rs.getTimestamp("fecha_solicitud").toLocalDateTime(),
                        rs.getTimestamp("fecha_programada").toLocalDateTime(),
                        rs.getTimestamp("fecha_realizacion") != null ? rs.getTimestamp("fecha_realizacion").toLocalDateTime() : null,
                        rs.getString("estado"),
                        rs.getString("observaciones"),
                        rs.getInt("id_mascota"),
                        rs.getInt("id_cuidador"),
                        rs.getInt("id_dueño"),
                        nombreCategoria, 
                        nombreMascota, 
                        nombreCuidador, 
                        nombreDueno 
                    ));
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al cargar servicios: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la conexión a la base de datos: " + e.getMessage(), e);
        }
        return servicios;
    }

    @FXML
    private void volverOnAction() throws IOException {
        App.setRoot("menuAdmin");
    }

    @FXML
    private void anadirServicioOnAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearServicio.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Nuevo Servicio");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarServicios();
    }

    @FXML
    private void modificarServicioOnAction() throws IOException {
        Servicio servicioSeleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (servicioSeleccionado != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearServicio.fxml"));
            Scene scene = new Scene(loader.load());

            CrearServicioControlador controlador = loader.getController();
            controlador.setServicioAModificar(servicioSeleccionado);

            Stage stage = new Stage();
            stage.setTitle("Modificar Servicio");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarServicios();
        } else {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "Por favor, seleccione un servicio para modificar.");
        }
    }

    @FXML
    private void eliminarServicioOnAction() {
        Servicio servicioSeleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (servicioSeleccionado != null) {
            if (confirmarEliminacion()) {
                try {
                    eliminar(servicioSeleccionado.getIdServicio());
                    servicios.remove(servicioSeleccionado);
                    tablaServicios.refresh();
                    mostrarMensaje(AlertType.INFORMATION, "Éxito", "Servicio eliminado correctamente");
                } catch (SQLException e) {
                    mostrarMensaje(AlertType.ERROR, "Error", "Error al eliminar el servicio: " + e.getMessage());
                }
            }
        } else {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "Por favor, seleccione un servicio para eliminar.");
        }
    }

    private void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM servicio WHERE id_servicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                // Éxito, no se necesita mensaje aquí ya que se maneja en eliminarServicioOnAction
            } else {
                throw new SQLException("No se encontró el servicio para eliminar");
            }
        }
    }

    @FXML
    private void buscarServicioOnAction() {
        String busqueda = txtBuscar.getText().trim();
        if (!busqueda.isEmpty()) {
            List<Servicio> serviciosEncontrados = buscar(busqueda);
            servicios.clear();
            servicios.addAll(serviciosEncontrados);
        } else {
            cargarServicios();
        }
    }

    private List<Servicio> buscar(String busqueda) {
        List<Servicio> servicios = new ArrayList<>();
        String sql = """
            SELECT s.id_servicio, s.id_categoria, s.id_mascota, s.id_cuidador, s.id_dueño, c.tipo AS nombre_categoria, s.estado, s.observaciones,
                   m.nombre AS nombre_mascota, u_cuidador.nombre AS nombre_cuidador, u_dueno.nombre AS nombre_dueno,
                   s.fecha_solicitud, s.fecha_programada, s.fecha_realizacion
            FROM servicio s
            JOIN categoria c ON s.id_categoria = c.id_categoria
            JOIN mascota m ON s.id_mascota = m.id_mascota
            JOIN usuario u_cuidador ON s.id_cuidador = u_cuidador.id_usuario
            JOIN usuario u_dueno ON s.id_dueño = u_dueno.id_usuario
            WHERE c.tipo LIKE ? OR s.estado LIKE ? OR m.nombre LIKE ? OR u_cuidador.nombre LIKE ? OR u_dueno.nombre LIKE ?
            ORDER BY s.fecha_solicitud DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String busquedaPattern = "%" + busqueda + "%";
            pstmt.setString(1, busquedaPattern);
            pstmt.setString(2, busquedaPattern);
            pstmt.setString(3, busquedaPattern);
            pstmt.setString(4, busquedaPattern);
            pstmt.setString(5, busquedaPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String nombreCategoria = rs.getString("nombre_categoria");
                String nombreMascota = rs.getString("nombre_mascota");
                String nombreCuidador = rs.getString("nombre_cuidador");
                String nombreDueno = rs.getString("nombre_dueno");

                servicios.add(new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getInt("id_categoria"),
                    rs.getTimestamp("fecha_solicitud").toLocalDateTime(),
                    rs.getTimestamp("fecha_programada").toLocalDateTime(),
                    rs.getTimestamp("fecha_realizacion") != null ? rs.getTimestamp("fecha_realizacion").toLocalDateTime() : null,
                    rs.getString("estado"),
                    rs.getString("observaciones"),
                    rs.getInt("id_mascota"),
                    rs.getInt("id_cuidador"),
                    rs.getInt("id_dueño"),
                    nombreCategoria, 
                    nombreMascota, 
                    nombreCuidador, 
                    nombreDueno 
                ));
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar servicios: " + e.getMessage(), e);
        }
        return servicios;
    }

    private boolean confirmarEliminacion() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar este servicio?");
        alert.setContentText("Esta acción no se puede deshacer.");
        return alert.showAndWait().get() == ButtonType.OK;
    }

    private void mostrarMensaje(AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
} 