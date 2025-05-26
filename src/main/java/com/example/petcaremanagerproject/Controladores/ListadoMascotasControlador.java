package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Mascota;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ListadoMascotasControlador implements Initializable {
    @FXML
    private TableView<Mascota> tablaMascotas;
    @FXML
    private TableColumn<Mascota, String> colNombreMascota, colEspecie, colRaza;
    @FXML
    private TableColumn<Mascota, Integer> colEdad, colIdDueno;
    @FXML
    private TableColumn<Mascota, Double> colPeso;
    @FXML
    private ComboBox<String> comboBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        configurarComboBox();
    }

    private void configurarColumnas() {
        colNombreMascota.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colIdDueno.setCellValueFactory(new PropertyValueFactory<>("idDueno"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));

        // Para la banda sonora necesitamos una consulta JOIN
        colEspecie.setCellFactory(column -> {
            return new TableCell<Mascota, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            };
        });

        // Formato para la fecha
        colEdad.setCellFactory(column -> {
            return new TableCell<Mascota, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            };
        });

        // Formato para el precio
        colPeso.setCellFactory(column -> {
            return new TableCell<Mascota, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f kg", item));
                    }
                }
            };
        });
    }

    private void cargarDatos() {
        ObservableList<Mascota> mascotas = FXCollections.observableArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Mascota");

            while (rs.next()) {
                Mascota mascota = new Mascota(
                        rs.getInt("ID"),
                        rs.getString("Nombre"),
                        rs.getString("Especie"),
                        rs.getString("Raza"),
                        rs.getInt("Edad"),
                        rs.getInt("IDDueno"),
                        rs.getDouble("Peso"));
                mascotas.add(mascota);
            }

            tablaMascotas.setItems(mascotas);

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
                "Todas las mascotas",
                "Mascotas por especie",
                "Mascotas más pesadas",
                "Mascotas más jóvenes",
                "Mascotas más recientes",
                "Mascotas con raza");
        comboBox.setItems(opciones);
    }

    public void ejecutarConsultaOnAction(ActionEvent actionEvent) {
        String seleccion = comboBox.getValue();
        if (seleccion == null)
            return;

        ObservableList<Mascota> mascotas = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "";

            switch (seleccion) {
                case "Todas las mascotas":
                    query = "SELECT * FROM Mascota";
                    pst = conn.prepareStatement(query);
                    break;

                case "Mascotas por especie":
                    query = "SELECT v.*, COUNT(*) as cantidad " +
                            "FROM Mascota v " +
                            "GROUP BY Especie " +
                            "ORDER BY Especie";
                    pst = conn.prepareStatement(query);
                    break;

                case "Mascotas más pesadas":
                    query = "SELECT * FROM Mascota " +
                            "ORDER BY Peso DESC " +
                            "LIMIT 5";
                    pst = conn.prepareStatement(query);
                    break;

                case "Mascotas más jóvenes":
                    query = "SELECT * FROM Mascota " +
                            "ORDER BY Edad DESC " +
                            "LIMIT 5";
                    pst = conn.prepareStatement(query);
                    break;

                case "Mascotas más recientes":
                    query = "SELECT * FROM Mascota " +
                            "WHERE Edad IS NOT NULL " +
                            "ORDER BY Edad DESC " +
                            "LIMIT 5";
                    pst = conn.prepareStatement(query);
                    break;

                case "Mascotas con raza":
                    query = "SELECT v.*, b.Nombre as NombreRaza " +
                            "FROM Mascota v " +
                            "INNER JOIN Raza b ON v.RazaID = b.ID " +
                            "ORDER BY v.Nombre";
                    pst = conn.prepareStatement(query);
                    break;
            }

            rs = pst.executeQuery();

            while (rs.next()) {
                Mascota mascota = new Mascota(
                        rs.getInt("ID"),
                        rs.getString("Nombre"),
                        rs.getString("Especie"),
                        rs.getString("Raza"),
                        rs.getInt("Edad"),
                        rs.getInt("IDDueno"),
                        rs.getDouble("Peso"));
                mascotas.add(mascota);
            }

            tablaMascotas.setItems(mascotas);

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

    public void nuevaMascotaOnAction(ActionEvent actionEvent) {
        App.setRoot("crearMascota");
    }

    public void borrarMascotaOnAction(ActionEvent actionEvent) {
        Mascota mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();

        if (mascotaSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ningún mascota seleccionada");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione una mascota para borrar.");
            alert.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar borrado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea borrar la mascota " +
                mascotaSeleccionada.getNombre() + "?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            Connection conn = null;
            PreparedStatement pstDeleteRelaciones = null;
            PreparedStatement pstDeleteMascota = null;

            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false); // Iniciamos una transacción

                // Primero eliminamos las relaciones en Mascota-Dueno
                String queryRelaciones = "DELETE FROM `Mascota-Dueno` WHERE MascotaID = ?";
                pstDeleteRelaciones = conn.prepareStatement(queryRelaciones);
                pstDeleteRelaciones.setInt(1, mascotaSeleccionada.getId());
                pstDeleteRelaciones.executeUpdate();

                // Luego eliminamos la mascota
                String queryMascota = "DELETE FROM Mascota WHERE ID = ?";
                pstDeleteMascota = conn.prepareStatement(queryMascota);
                pstDeleteMascota.setInt(1, mascotaSeleccionada.getId());
                int filasAfectadas = pstDeleteMascota.executeUpdate();

                conn.commit(); // Confirmamos la transacción

                if (filasAfectadas > 0) {
                    cargarDatos();
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Mascota borrada");
                    exito.setHeaderText(null);
                    exito.setContentText("La mascota ha sido borrada correctamente.");
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
                error.setContentText("Error al borrar la mascota: " + e.getMessage());
                error.showAndWait();
            } finally {
                try {
                    if (pstDeleteRelaciones != null)
                        pstDeleteRelaciones.close();
                    if (pstDeleteMascota != null)
                        pstDeleteMascota.close();
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
        Mascota mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();

        if (mascotaSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ningún mascota seleccionada");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione una mascota para modificar.");
            alert.showAndWait();
            return;
        }

        App.setMascotaModificar(mascotaSeleccionada);
        App.setRoot("modificarMascota");
    }

    public void atras(ActionEvent actionEvent) {
        App.setRoot("menuAdmin");
    }

}
