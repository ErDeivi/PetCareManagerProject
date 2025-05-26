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
    private TableColumn<Servicio, String> colEstado;
    @FXML
    private TableColumn<Servicio, String> colObservaciones;
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
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
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
                        rs.getInt("ID"),
                        rs.getString("Estado"),
                        rs.getString("Observaciones"));
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
                "Todas las bandas sonoras",
                "Bandas sonoras por compositor",
                "Bandas sonoras por orden alfabético");
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
                case "Todas las bandas sonoras":
                    query = "SELECT * FROM servicio";
                    pst = conn.prepareStatement(query);
                    break;

                case "Banda sonora por compositor":
                    query = "SELECT bs.*, COUNT(*) as cantidad " +
                            "FROM servicio bs " +
                            "GROUP BY Compositor " +
                            "ORDER BY Compositor";
                    pst = conn.prepareStatement(query);
                    break;

                case "Bandas sonoras por orden alfabético":
                    query = "SELECT * FROM servicio " +
                            "ORDER BY Nombre";
                    pst = conn.prepareStatement(query);
                    break;
            }

            rs = pst.executeQuery();

            while (rs.next()) {
                Servicio servicio = new Servicio(
                        rs.getInt("ID"),
                        rs.getString("Estado"),
                        rs.getString("Observaciones"));
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

    public void borrarBandaSonoraOnAction(ActionEvent actionEvent) {
        Servicio servicioSeleccionado = tablaServicios.getSelectionModel().getSelectedItem();

        if (servicioSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ninguna banda sonora seleccionada");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione una banda sonora para borrar.");
            alert.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar borrado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea borrar la banda sonora " +
                servicioSeleccionado.getEstado() + "?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            Connection conn = null;
            PreparedStatement pstDeleteBandaSonora = null;

            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false);

                String queryBandaSonora = "DELETE FROM servicio WHERE ID = ?";
                pstDeleteBandaSonora = conn.prepareStatement(queryBandaSonora);
                pstDeleteBandaSonora.setInt(1, servicioSeleccionado.getId());
                int filasAfectadas = pstDeleteBandaSonora.executeUpdate();

                conn.commit(); // Confirmamos la transacción

                if (filasAfectadas > 0) {
                    cargarDatos();
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Banda sonora borrada");
                    exito.setHeaderText(null);
                    exito.setContentText("La banda sonora ha sido borrada correctamente.");
                    exito.showAndWait();
                }

            } catch (SQLException e) {
                try {
                    if (conn != null)
                        conn.rollback(); // Si hay error, deshacemos los cambios
                } catch (SQLException ex) {
                    System.out.println("Error al hacer rollback: " + ex.getMessage());
                }
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Error al borrar la banda sonora: " + e.getMessage());
                error.showAndWait();
            } finally {
                try {
                    if (pstDeleteBandaSonora != null)
                        pstDeleteBandaSonora.close();
                    if (conn != null) {
                        conn.setAutoCommit(true); // Restauramos el autocommit
                        conn.close();
                    }
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
            alert.setTitle("Ninguna banda sonora seleccionada");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione una banda sonora para modificarla.");
            alert.showAndWait();
            return;
        }

        App.setBandaSonoraModificar(servicioSeleccionado);
        App.setRoot("modificarBandaSonora");
    }

    public void atras(ActionEvent actionEvent) {
        App.setRoot("menuAdmin");
    }

}
