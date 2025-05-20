package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Programador;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ListadoProgramadoresControlador implements Initializable {
    
    @FXML
    private TableView<Programador> tablaProgramadores;
    
    @FXML
    private TableColumn<Programador, String> colNombre;
    
    @FXML
    private TableColumn<Programador, String> colApellido;
    
    @FXML
    private TableColumn<Programador, String> colLocalidad;

    @FXML
    private TableColumn<Programador, Integer> colSalario;

    @FXML
    private TableColumn<Programador, String> colDni;
    
    @FXML
    private ComboBox<String> comboBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        configurarComboBox();
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colLocalidad.setCellValueFactory(new PropertyValueFactory<>("localidad"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
    }

    private void cargarDatos() {
        ObservableList<Programador> programadores = FXCollections.observableArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Programador");

            while (rs.next()) {
                Programador programador = new Programador(
                    rs.getInt("Id"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Localidad"),
                    rs.getInt("Salario"),
                    rs.getString("DNI")
                );
                programadores.add(programador);
            }

            tablaProgramadores.setItems(programadores);

        } catch (SQLException e) {
            mostrarError("Error al cargar los datos: " + e.getMessage());
        } finally {
            cerrarConexiones(conn, stmt, rs);
        }
    }

    private void configurarComboBox() {
        ObservableList<String> opciones = FXCollections.observableArrayList(
            "Todos los programadores",
            "Programadores mejor pagados",
            "Programadores con videojuegos"
        );
        comboBox.setItems(opciones);
    }

    @FXML
    public void ejecutarConsultaOnAction(ActionEvent actionEvent) {
        String seleccion = comboBox.getValue();
        if (seleccion == null) return;
        
        ObservableList<Programador> programadores = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "";

            switch (seleccion) {
                case "Todos los programadores":
                    query = "SELECT * FROM Programador";
                    pst = conn.prepareStatement(query);
                    break;

                case "Programadores mejor pagados":
                    query = "SELECT * FROM Programador " +
                           "ORDER BY Salario DESC " +
                           "LIMIT 5";
                    pst = conn.prepareStatement(query);
                    break;

                case "Programadores con videojuegos":
                    query = "SELECT DISTINCT p.* " +
                           "FROM Programador p " +
                           "INNER JOIN `Videojuego-Programador` vp ON p.Id = vp.ProgramadorId " +
                           "ORDER BY p.Nombre";
                    pst = conn.prepareStatement(query);
                    break;
            }

            rs = pst.executeQuery();

            while (rs.next()) {
                Programador programador = new Programador(
                    rs.getInt("Id"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Localidad"),
                    rs.getInt("Salario"),
                    rs.getString("DNI")
                );
                programadores.add(programador);
            }

            tablaProgramadores.setItems(programadores);

        } catch (SQLException e) {
            mostrarError("Error al ejecutar la consulta: " + e.getMessage());
        } finally {
            cerrarConexiones(conn, pst, rs);
        }
    }

    @FXML 

    public void asignarVideojuegoOnAction(ActionEvent actionEvent){

        App.setRoot("asignarVideojuego");

    }

    @FXML
    public void nuevoProgramadorOnAction(ActionEvent actionEvent) {
        App.setRoot("crearProgramador");
    }
    
    @FXML
    public void borrarProgramadorOnAction(ActionEvent actionEvent) {
        Programador programadorSeleccionado = tablaProgramadores.getSelectionModel().getSelectedItem();
        
        if (programadorSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ningún programador seleccionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione un programador para borrar.");
            alert.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar borrado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea borrar el programador " + 
                                  programadorSeleccionado.getNombre() + " " + programadorSeleccionado.getApellido() + "?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
        Connection conn = null;
        PreparedStatement pstDeleteRelaciones = null;
        PreparedStatement pstDeleteProgramador = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Primero eliminamos las relaciones en Videojuego-Programador
                String queryRelaciones = "DELETE FROM `Videojuego-Programador` WHERE ProgramadorID = ?";
            pstDeleteRelaciones = conn.prepareStatement(queryRelaciones);
                pstDeleteRelaciones.setInt(1, programadorSeleccionado.getId());

            // Luego eliminamos el programador
            String queryProgramador = "DELETE FROM Programador WHERE id = ?";
            pstDeleteProgramador = conn.prepareStatement(queryProgramador);
                pstDeleteProgramador.setInt(1, programadorSeleccionado.getId());

                // Ejecutamos las consultas
                pstDeleteRelaciones.executeUpdate();
            int filasAfectadas = pstDeleteProgramador.executeUpdate();

            conn.commit();
            
            if (filasAfectadas > 0) {
                    // Eliminamos el programador de la lista observable
                    ObservableList<Programador> programadores = FXCollections.observableArrayList(tablaProgramadores.getItems());
                    programadores.remove(programadorSeleccionado);
                    tablaProgramadores.setItems(programadores);
                    
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Programador borrado");
                    exito.setHeaderText(null);
                    exito.setContentText("El programador ha sido borrado correctamente.");
                    exito.showAndWait();
                    
                    // Recargamos los datos de la tabla
                cargarDatos();
            }

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Error al borrar el programador: " + e.getMessage());
                error.showAndWait();
        } finally {
                try {
                    if (pstDeleteRelaciones != null) pstDeleteRelaciones.close();
                    if (pstDeleteProgramador != null) pstDeleteProgramador.close();
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.out.println("Error al cerrar las conexiones: " + e.getMessage());
                }
            }
        }
    }
    
    @FXML
    public void modificarOnAction(ActionEvent actionEvent) {
        Programador programadorSeleccionado = tablaProgramadores.getSelectionModel().getSelectedItem();
        
        if (programadorSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ningún programador seleccionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione un programador para modificar.");
            alert.showAndWait();
            return;
        }

        App.setProgramadorModificar(programadorSeleccionado);
        App.setRoot("modificarProgramador");
    }
    
    @FXML
    public void atras(ActionEvent actionEvent) {
        App.setRoot("menuAdmin");
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
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
