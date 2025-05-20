package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Videojuego;
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

public class ListadoVideojuegosControlador implements Initializable {
    @FXML
    private TableView<Videojuego> tablaArticulos;
    @FXML
    private TableColumn<Videojuego, String> colNombre, colPlataforma, colGenero, colBandaSonora;
    @FXML
    private TableColumn<Videojuego, Date> colPublicado;
    @FXML
    private TableColumn<Videojuego, Double> colPrecio;
    @FXML
    private TableColumn<Videojuego, Integer> colVentas;
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
        colPlataforma.setCellValueFactory(new PropertyValueFactory<>("plataforma"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colPublicado.setCellValueFactory(new PropertyValueFactory<>("publicado"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colVentas.setCellValueFactory(new PropertyValueFactory<>("ventas"));

        // Para la banda sonora necesitamos una consulta JOIN
        colBandaSonora.setCellValueFactory(cellData -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT b.Nombre FROM BandaSonora b " +
                        "JOIN Videojuego v ON v.BandaSonoraID = b.ID " +
                        "WHERE v.ID = ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setInt(1, cellData.getValue().getId());
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    return new SimpleStringProperty(rs.getString("Nombre"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });

        // Formato para la fecha
        colPublicado.setCellFactory(column -> {
            return new TableCell<Videojuego, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
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
        colPrecio.setCellFactory(column -> {
            return new TableCell<Videojuego, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f €", item));
                    }
                }
            };
        });
    }

    private void cargarDatos() {
        ObservableList<Videojuego> videojuegos = FXCollections.observableArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Videojuego");

            while (rs.next()) {
                Videojuego videojuego = new Videojuego(
                        rs.getInt("ID"),
                        rs.getString("Nombre"),
                        rs.getString("Plataforma"),
                        rs.getString("Género"),
                        rs.getDate("Publicado"),
                        rs.getDouble("Precio"),
                        rs.getInt("Ventas"));
                videojuegos.add(videojuego);
            }

            tablaArticulos.setItems(videojuegos);

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
                "Todos los videojuegos",
                "Videojuegos por plataforma",
                "Videojuegos más caros",
                "Videojuegos más vendidos",
                "Videojuegos más recientes",
                "Videojuegos con banda sonora");
        comboBox.setItems(opciones);
    }

    public void ejecutarConsultaOnAction(ActionEvent actionEvent) {
        String seleccion = comboBox.getValue();
        if (seleccion == null)
            return;

        ObservableList<Videojuego> videojuegos = FXCollections.observableArrayList();
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "";

            switch (seleccion) {
                case "Todos los videojuegos":
                    query = "SELECT * FROM Videojuego";
                    pst = conn.prepareStatement(query);
                    break;

                case "Videojuegos por plataforma":
                    query = "SELECT v.*, COUNT(*) as cantidad " +
                            "FROM Videojuego v " +
                            "GROUP BY Plataforma " +
                            "ORDER BY Plataforma";
                    pst = conn.prepareStatement(query);
                    break;

                case "Videojuegos más caros":
                    query = "SELECT * FROM Videojuego " +
                            "ORDER BY Precio DESC " +
                            "LIMIT 5";
                    pst = conn.prepareStatement(query);
                    break;

                case "Videojuegos más vendidos":
                    query = "SELECT * FROM Videojuego " +
                            "ORDER BY Ventas DESC " +
                            "LIMIT 5";
                    pst = conn.prepareStatement(query);
                    break;

                case "Videojuegos más recientes":
                    query = "SELECT * FROM Videojuego " +
                            "WHERE Publicado IS NOT NULL " +
                            "ORDER BY Publicado DESC " +
                            "LIMIT 5";
                    pst = conn.prepareStatement(query);
                    break;

                case "Videojuegos con banda sonora":
                    query = "SELECT v.*, b.Nombre as NombreBandaSonora " +
                            "FROM Videojuego v " +
                            "INNER JOIN BandaSonora b ON v.BandaSonoraID = b.ID " +
                            "ORDER BY v.Nombre";
                    pst = conn.prepareStatement(query);
                    break;
            }

            rs = pst.executeQuery();

            while (rs.next()) {
                Videojuego videojuego = new Videojuego(
                        rs.getInt("ID"),
                        rs.getString("Nombre"),
                        rs.getString("Plataforma"),
                        rs.getString("Género"),
                        rs.getDate("Publicado"),
                        rs.getDouble("Precio"),
                        rs.getInt("Ventas"));
                videojuegos.add(videojuego);
            }

            tablaArticulos.setItems(videojuegos);

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

    public void nuevoVideojuegoOnAction(ActionEvent actionEvent) {
        App.setRoot("crearVideojuego");
    }

    public void borrarVideojuegoOnAction(ActionEvent actionEvent) {
        Videojuego videojuegoSeleccionado = tablaArticulos.getSelectionModel().getSelectedItem();

        if (videojuegoSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ningún videojuego seleccionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione un videojuego para borrar.");
            alert.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar borrado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea borrar el videojuego " +
                videojuegoSeleccionado.getNombre() + "?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            Connection conn = null;
            PreparedStatement pstDeleteRelaciones = null;
            PreparedStatement pstDeleteVideojuego = null;

            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false); // Iniciamos una transacción

                // Primero eliminamos las relaciones en Videojuego-Programador
                String queryRelaciones = "DELETE FROM `Videojuego-Programador` WHERE VideojuegoID = ?";
                pstDeleteRelaciones = conn.prepareStatement(queryRelaciones);
                pstDeleteRelaciones.setInt(1, videojuegoSeleccionado.getId());
                pstDeleteRelaciones.executeUpdate();

                // Luego eliminamos el videojuego
                String queryVideojuego = "DELETE FROM Videojuego WHERE ID = ?";
                pstDeleteVideojuego = conn.prepareStatement(queryVideojuego);
                pstDeleteVideojuego.setInt(1, videojuegoSeleccionado.getId());
                int filasAfectadas = pstDeleteVideojuego.executeUpdate();

                conn.commit(); // Confirmamos la transacción

                if (filasAfectadas > 0) {
                    cargarDatos();
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Videojuego borrado");
                    exito.setHeaderText(null);
                    exito.setContentText("El videojuego ha sido borrado correctamente.");
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
                error.setContentText("Error al borrar el videojuego: " + e.getMessage());
                error.showAndWait();
            } finally {
                try {
                    if (pstDeleteRelaciones != null)
                        pstDeleteRelaciones.close();
                    if (pstDeleteVideojuego != null)
                        pstDeleteVideojuego.close();
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
        Videojuego videojuegoSeleccionado = tablaArticulos.getSelectionModel().getSelectedItem();

        if (videojuegoSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ningún videojuego seleccionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione un videojuego para modificar.");
            alert.showAndWait();
            return;
        }

        App.setVideojuegoModificar(videojuegoSeleccionado);
        App.setRoot("modificarVideojuego");
    }

    public void atras(ActionEvent actionEvent) {
        App.setRoot("menuAdmin");
    }

}
