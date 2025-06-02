package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Servicio;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ListadoServiciosControlador {
    @FXML private TableView<Servicio> tablaServicios;
    @FXML private TableColumn<Servicio, Integer> colIdServicio;
    @FXML private TableColumn<Servicio, Integer> colIdCategoria;
    @FXML private TableColumn<Servicio, String> colEstado;
    @FXML private TableColumn<Servicio, String> colObservaciones;
    @FXML private TableColumn<Servicio, Integer> colIdMascota;
    @FXML private TableColumn<Servicio, Integer> colIdCuidador;
    @FXML private TableColumn<Servicio, Integer> colIdDueno;
    @FXML private TableColumn<Servicio, LocalDateTime> colFechaSolicitud;
    @FXML private TableColumn<Servicio, LocalDateTime> colFechaProgramada;
    @FXML private TableColumn<Servicio, LocalDateTime> colFechaRealizacion;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;

    private ObservableList<Servicio> servicios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarServicios();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colIdServicio.setCellValueFactory(new PropertyValueFactory<>("idServicio"));
        colIdCategoria.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
        colIdMascota.setCellValueFactory(new PropertyValueFactory<>("idMascota"));
        colIdCuidador.setCellValueFactory(new PropertyValueFactory<>("idCuidador"));
        colIdDueno.setCellValueFactory(new PropertyValueFactory<>("idDueno"));
        colFechaSolicitud.setCellValueFactory(new PropertyValueFactory<>("fechaSolicitud"));
        colFechaProgramada.setCellValueFactory(new PropertyValueFactory<>("fechaProgramada"));
        colFechaRealizacion.setCellValueFactory(new PropertyValueFactory<>("fechaRealizacion"));

        tablaServicios.getSortOrder().add(colIdServicio);
    }

    private void cargarServicios() {
        List<Servicio> listaServicios = obtenerTodos();
        servicios.clear();
        servicios.addAll(listaServicios);
        tablaServicios.setItems(servicios);
        tablaServicios.refresh();
    }

    private List<Servicio> obtenerTodos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicio ORDER BY id_servicio";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                servicios.add(new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getInt("id_categoria"),
                    rs.getTimestamp("fecha_solicitud").toLocalDateTime(),
                    rs.getTimestamp("fecha_programada").toLocalDateTime(),
                    rs.getTimestamp("fecha_realizacion") != null ? 
                        rs.getTimestamp("fecha_realizacion").toLocalDateTime() : null,
                    rs.getString("estado"),
                    rs.getString("observaciones"),
                    rs.getInt("id_mascota"),
                    rs.getInt("id_cuidador"),
                    rs.getInt("id_dueño")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al cargar servicios: " + e.getMessage());
        }
        return servicios;
    }

    private void configurarBusqueda() {
        txtBuscar.setOnAction(event -> buscarServicioOnAction());
        btnBuscar.setOnAction(event -> buscarServicioOnAction());
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
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un servicio para modificar.");
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
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Servicio eliminado correctamente");
                } catch (SQLException e) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al eliminar el servicio: " + e.getMessage());
                }
            }
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un servicio para eliminar.");
        }
    }

    private void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM servicio WHERE id_servicio = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
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
            SELECT * FROM servicio 
            WHERE estado LIKE ? 
            OR observaciones LIKE ?
            ORDER BY id_servicio
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String busquedaPattern = "%" + busqueda + "%";
            pstmt.setString(1, busquedaPattern);
            pstmt.setString(2, busquedaPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                servicios.add(new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getInt("id_categoria"),
                    rs.getTimestamp("fecha_solicitud").toLocalDateTime(),
                    rs.getTimestamp("fecha_programada").toLocalDateTime(),
                    rs.getTimestamp("fecha_realizacion") != null ? 
                        rs.getTimestamp("fecha_realizacion").toLocalDateTime() : null,
                    rs.getString("estado"),
                    rs.getString("observaciones"),
                    rs.getInt("id_mascota"),
                    rs.getInt("id_cuidador"),
                    rs.getInt("id_dueño")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al buscar servicios: " + e.getMessage());
        }
        return servicios;
    }

    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar este servicio?");
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