package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Programador;
import com.example.petcaremanagerproject.Modelo.Videojuego;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AsignarVideojuegoControlador implements Initializable {

    @FXML
    private TableView<Videojuego> tablaVideojuegos;
    @FXML
    private TableColumn<Videojuego, Integer> colIdVideojuego;
    @FXML
    private TableColumn<Videojuego, String> colNombreVideojuego;

    @FXML
    private TableView<Programador> tablaProgramadores;
    @FXML
    private TableColumn<Programador, Integer> colIdProgramador;
    @FXML
    private TableColumn<Programador, String> colNombreProgramador;

    @FXML
    private DatePicker fechaInicio;
    @FXML
    private DatePicker fechaFinal;

    private ObservableList<Videojuego> listaVideojuegos;
    private ObservableList<Programador> listaProgramadores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colIdVideojuego.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombreVideojuego.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        colIdProgramador.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombreProgramador.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre() + " " + cellData.getValue().getApellido()));
    }

    private void cargarDatos() {
        cargarVideojuegos();
        cargarProgramadores();
    }

    private void cargarVideojuegos() {
        listaVideojuegos = FXCollections.observableArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id,nombre FROM Videojuego");

            while (rs.next()) {
                Videojuego videojuego = new Videojuego(
                    rs.getInt("ID"),
                    rs.getString("Nombre")
                    
                );
                videojuego.setId(rs.getInt("ID"));
                videojuego.setNombre(rs.getString("Nombre"));
                listaVideojuegos.add(videojuego);
            }

            tablaVideojuegos.setItems(listaVideojuegos);

        } catch (SQLException e) {
            mostrarError("Error al cargar videojuegos: " + e.getMessage());
        } finally {
            cerrarConexiones(conn, stmt, rs);
        }
    }

    private void cargarProgramadores() {
        listaProgramadores = FXCollections.observableArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id,nombre,apellido FROM Programador");

            while (rs.next()) {
                Programador programador = new Programador(
                    rs.getInt("ID"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido")
                );
                programador.setId(rs.getInt("ID"));
                programador.setNombre(rs.getString("Nombre"));
                programador.setApellido(rs.getString("Apellido"));
                listaProgramadores.add(programador);
            }

            tablaProgramadores.setItems(listaProgramadores);

        } catch (SQLException e) {
            mostrarError("Error al cargar programadores: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarConexiones(conn, stmt, rs);
        }
    }

    @FXML
    private void asignarOnAction(ActionEvent event) {
        Videojuego videojuego = tablaVideojuegos.getSelectionModel().getSelectedItem();
        Programador programador = tablaProgramadores.getSelectionModel().getSelectedItem();
        LocalDate inicio = fechaInicio.getValue();
        LocalDate fin = fechaFinal.getValue();

        if (!validarSeleccion(videojuego, programador, inicio, fin)) {
            return;
        }

        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO `Videojuego-Programador` VALUES (?, ?, ?, ?)";
            pst = conn.prepareStatement(query);

            pst.setInt(1, videojuego.getId());
            pst.setInt(2, programador.getId());
            pst.setDate(3, Date.valueOf(inicio));
            pst.setDate(4, Date.valueOf(fin));

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarInformacion("Asignación realizada correctamente");
                limpiarSeleccion();
            }

        } catch (SQLException e) {
            mostrarError("Error al asignar: " + e.getMessage());
        } finally {
            cerrarConexiones(conn, pst, null);
        }
    }

    private boolean validarSeleccion(Videojuego videojuego, Programador programador, 
                                   LocalDate inicio, LocalDate fin) {
        if (videojuego == null) {
            mostrarError("Debe seleccionar un videojuego");
            return false;
        }
        if (programador == null) {
            mostrarError("Debe seleccionar un programador");
            return false;
        }
        if (inicio == null) {
            mostrarError("Debe seleccionar una fecha de inicio");
            return false;
        }
        if (fin == null) {
            mostrarError("Debe seleccionar una fecha final");
            return false;
        }
        if (fin.isBefore(inicio)) {
            mostrarError("La fecha final no puede ser anterior a la fecha de inicio");
            return false;
        }
        return true;
    }

    private void limpiarSeleccion() {
        tablaVideojuegos.getSelectionModel().clearSelection();
        tablaProgramadores.getSelectionModel().clearSelection();
        fechaInicio.setValue(null);
        fechaFinal.setValue(null);
    }

    @FXML
    private void salirOnAction(ActionEvent event) {
        App.setRoot("listadoProgramadores");
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarConexiones(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar las conexiones: " + e.getMessage());
        }
    }
}