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

public class GestionarMascotasControlador {
    @FXML private TableView<Mascota> tablaMascotas;
    @FXML private TableColumn<Mascota, Integer> colId;
    @FXML private TableColumn<Mascota, String> colNombre;
    @FXML private TableColumn<Mascota, String> colEspecie;
    @FXML private TableColumn<Mascota, String> colRaza;
    @FXML private TableColumn<Mascota, Integer> colEdad;
    @FXML private TableColumn<Mascota, Double> colPeso;
    @FXML private TableColumn<Mascota, String> colCliente;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;

    private ObservableList<Mascota> mascotas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarMascotas();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));

        // Permitir ordenamiento en todas las columnas
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
                        return o1.getCliente().compareTo(o2.getCliente());
                    }
                }
                return 0;
            });
            return true;
        });
    }

    private void configurarBusqueda() {
        // Buscar al presionar Enter en el campo de búsqueda
        txtBuscar.setOnAction(event -> buscarMascotaOnAction());
        
        // Buscar al hacer clic en el botón
        btnBuscar.setOnAction(event -> buscarMascotaOnAction());
    }

    private void cargarMascotas() {
        List<Mascota> listaMascotas = obtenerTodas();
        System.out.println("Cargadas " + listaMascotas.size() + " mascotas."); // Debugging line
        mascotas.clear();
        mascotas.addAll(listaMascotas);
        tablaMascotas.setItems(mascotas);
        tablaMascotas.refresh();
    }

    private List<Mascota> obtenerTodas() {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = """
            SELECT m.id_mascota, m.nombre AS mascota_nombre, m.especie, m.raza, m.edad, m.peso, u.nombre AS dueno_nombre, m.id_dueño 
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
                    // Usar el alias 'dueno_nombre' para obtener el nombre del dueño
                    String nombreDueno = rs.getString("dueno_nombre");

                    mascotas.add(new Mascota(
                        rs.getInt("id_mascota"),
                        rs.getString("mascota_nombre"), // Usar el alias 'mascota_nombre' para el nombre de la mascota
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getInt("edad"),
                        rs.getDouble("peso"),
                        nombreDueno, // Asignar el nombre del dueño a la propiedad 'cliente'
                        rs.getInt("id_dueño")
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

    @FXML
    private void volverOnAction() throws IOException {
        App.setRoot("menuAdmin");
    }

    @FXML
    private void anadirMascotaOnAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/petcaremanagerproject/crearMascota.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Nueva Mascota");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        App.configurarVentanaModal(stage);
        stage.showAndWait();

        cargarMascotas(); // Recargar la tabla después de cerrar la ventana de creación
    }

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
            App.configurarVentanaModal(stage);
            stage.showAndWait();
            
            cargarMascotas(); // Recargar la tabla después de cerrar la ventana de modificación
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione una mascota para modificar.");
        }
    }

    @FXML
    private void eliminarMascotaOnAction() {
        Mascota mascotaSeleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            if (validarEliminacion(mascotaSeleccionada) && confirmarEliminacion()) {
                try {
                    eliminar(mascotaSeleccionada.getId());
                    mascotas.remove(mascotaSeleccionada);
                    tablaMascotas.refresh(); // Asegurar que la tabla se actualice visualmente
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

    private boolean validarEliminacion(Mascota mascota) {
        // Verificar si la mascota tiene servicios asociados
        String sql = "SELECT COUNT(*) FROM servicio WHERE id_mascota = ?"; // Table name is 'servicio'
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, mascota.getId());
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

    private List<Mascota> buscar(String busqueda) {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = """
            SELECT m.id_mascota, m.nombre AS mascota_nombre, m.especie, m.raza, m.edad, m.peso, u.nombre AS dueno_nombre, m.id_dueño 
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
                        rs.getString("mascota_nombre"), // Usar el alias 'mascota_nombre' para el nombre de la mascota
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getInt("edad"),
                        rs.getDouble("peso"),
                        nombreDueno, // Asignar el nombre del dueño a la propiedad 'cliente'
                        rs.getInt("id_dueño")
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

    private boolean confirmarEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar esta mascota?");
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