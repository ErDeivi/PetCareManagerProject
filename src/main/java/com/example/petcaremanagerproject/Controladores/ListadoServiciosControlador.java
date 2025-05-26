package com.example.petcaremanagerproject.Controladores;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Servicio;
import com.example.petcaremanagerproject.Util.DatabaseConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ListadoServiciosControlador implements Initializable {

    @FXML
    private TableColumn<Servicio, Integer> colIdServicio;
    @FXML
    private TableColumn<Servicio, Integer> colIdCategoria;
    @FXML
    private TableColumn<Servicio, String> colEstado;
    @FXML
    private TableColumn<Servicio, String> colObservaciones;
    @FXML
    private TableColumn<Servicio, Integer> colIdMascota;
    @FXML
    private TableColumn<Servicio, Integer> colIdCuidador;
    @FXML
    private TableColumn<Servicio, Integer> colIdDueno;
    @FXML
    private TableColumn<Servicio, Object> colFechaSolicitud;
    @FXML
    private TableColumn<Servicio, Object> colFechaProgramada;
    @FXML
    private TableColumn<Servicio, Object> colFechaRealizacion;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TableView<Servicio> tablaServicios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        configurarComboBox();
    }

    private void configurarColumnas() {
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
    }

    private void cargarDatos() {
        ObservableList<Servicio> servicios = FXCollections.observableArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM servicio");

            while (rs.next()) {
                Servicio servicio = new Servicio(
                        rs.getInt("id_servicio"),
                        rs.getInt("id_categoria"),
                        rs.getTimestamp("fecha_solicitud").toLocalDateTime(),
                        rs.getTimestamp("fecha_programada") != null ? rs.getTimestamp("fecha_programada").toLocalDateTime() : null,
                        rs.getTimestamp("fecha_realizacion") != null ? rs.getTimestamp("fecha_realizacion").toLocalDateTime() : null,
                        rs.getString("estado"),
                        rs.getString("observaciones"),
                        rs.getInt("id_mascota"),
                        rs.getInt("id_cuidador"),
                        rs.getInt("id_dueño")
                );
                servicios.add(servicio);
            }

            tablaServicios.setItems(servicios);

        } catch (SQLException e) {
            System.out.println("Error al cargar los datos: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar las conexiones: " + e.getMessage());
            }
        }
    }

    private void configurarComboBox() {
        ObservableList<String> opciones = FXCollections.observableArrayList(
                "Todos los servicios",
                "Servicios por estado",
                "Servicios por cuidador"
        );
        comboBox.setItems(opciones);
    }

    public void ejecutarConsultaOnAction(ActionEvent actionEvent) {
        String seleccion = comboBox.getValue();
        if (seleccion == null)
            return;

        ObservableList<Servicio> servicios = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "";

            switch (seleccion) {
                case "Todos los servicios":
                    query = "SELECT * FROM servicio";
                    pst = conn.prepareStatement(query);
                    break;
                case "Servicios por estado":
                    query = "SELECT * FROM servicio ORDER BY estado";
                    pst = conn.prepareStatement(query);
                    break;
                case "Servicios por cuidador":
                    query = "SELECT * FROM servicio ORDER BY id_cuidador";
                    pst = conn.prepareStatement(query);
                    break;
            }

            rs = pst.executeQuery();

            while (rs.next()) {
                Servicio servicio = new Servicio(
                        rs.getInt("id_servicio"),
                        rs.getInt("id_categoria"),
                        rs.getTimestamp("fecha_solicitud").toLocalDateTime(),
                        rs.getTimestamp("fecha_programada") != null ? rs.getTimestamp("fecha_programada").toLocalDateTime() : null,
                        rs.getTimestamp("fecha_realizacion") != null ? rs.getTimestamp("fecha_realizacion").toLocalDateTime() : null,
                        rs.getString("estado"),
                        rs.getString("observaciones"),
                        rs.getInt("id_mascota"),
                        rs.getInt("id_cuidador"),
                        rs.getInt("id_dueño")
                );
                servicios.add(servicio);
            }

            tablaServicios.setItems(servicios);

        } catch (SQLException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText(null);
            error.setContentText("Error al ejecutar la consulta: " + e.getMessage());
            error.showAndWait();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pst != null)
                    pst.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar las conexiones: " + e.getMessage());
            }
        }
    }

    public void nuevaBandaSonoraOnAction(ActionEvent actionEvent) {
        App.setRoot("crearBandaSonora");
    }

    public void borrarServicioOnAction(ActionEvent actionEvent) {
        Servicio servicioSeleccionado = tablaServicios.getSelectionModel().getSelectedItem();

        if (servicioSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ningún servicio seleccionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione un servicio para borrar.");
            alert.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar borrado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea borrar el servicio con ID " +
                servicioSeleccionado.getIdServicio() + "?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            Connection conn = null;
            PreparedStatement pstDeleteServicio = null;

            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false);

                String queryServicio = "DELETE FROM servicio WHERE id_servicio = ?";
                pstDeleteServicio = conn.prepareStatement(queryServicio);
                pstDeleteServicio.setInt(1, servicioSeleccionado.getIdServicio());
                int filasAfectadas = pstDeleteServicio.executeUpdate();

                conn.commit(); // Confirmamos la transacción

                if (filasAfectadas > 0) {
                    cargarDatos();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Servicio eliminado");
                    alert.setHeaderText(null);
                    alert.setContentText("El servicio ha sido eliminado correctamente.");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                System.out.println("Error al borrar el servicio: " + e.getMessage());
            } finally {
                try {
                    if (pstDeleteServicio != null)
                        pstDeleteServicio.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar las conexiones: " + e.getMessage());
                }
            }
        }
    }

    public void modificarOnAction(ActionEvent actionEvent) {
        Servicio servicioSeleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (servicioSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ningún servicio seleccionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione un servicio para modificarlo.");
            alert.showAndWait();
            return;
        }

        App.setServicioModificar(servicioSeleccionado);
        App.setRoot("modificarServicio");
    }

    public void atras(ActionEvent actionEvent) {
        App.setRoot("menuAdmin");
    }

}
