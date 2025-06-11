package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Mascota;
import com.example.petcaremanagerproject.Modelo.Usuario;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModificarMascotaControlador {
    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbEspecie;
    @FXML private TextField txtRaza, txtEdad, txtPeso;
    @FXML private ComboBox<Usuario> cmbCliente;

    private Mascota mascotaAModificar;

    @FXML
    public void initialize() {
        configurarCampos();
        cargarDatos();
    }

    private void configurarCampos() {
        // Configurar ComboBox de especies
        ObservableList<String> especies = FXCollections.observableArrayList(
            "Perro", "Gato", "Ave", "Reptil", "Otro"
        );
        cmbEspecie.setItems(especies);

        // Configurar ComboBox de clientes
        cmbCliente.setCellFactory(new Callback<ListView<Usuario>, ListCell<Usuario>>() {
            @Override
            public ListCell<Usuario> call(ListView<Usuario> param) {
                return new ListCell<Usuario>() {
                    @Override
                    protected void updateItem(Usuario usuario, boolean empty) {
                        super.updateItem(usuario, empty);
                        if (empty || usuario == null) {
                            setText(null);
                        } else {
                            setText(usuario.getNombre());
                        }
                    }
                };
            }
        });

        cmbCliente.setButtonCell(new ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setText(null);
                } else {
                    setText(usuario.getNombre());
                }
            }
        });
    }

    private void cargarDatos() {
        List<Usuario> duenos = obtenerDuenos();
        cmbCliente.getItems().addAll(duenos);
    }

    private List<Usuario> obtenerDuenos() {
        List<Usuario> duenos = new ArrayList<>();
        String sql = """
            SELECT u.id_usuario, u.nombre, u.correo, u.telefono, u.imagen_url
            FROM usuario u
            JOIN dueño d ON u.id_usuario = d.id_usuario
            ORDER BY u.nombre
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                duenos.add(new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    null,
                    rs.getString("telefono"),
                    rs.getString("imagen_url")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al cargar dueños: " + e.getMessage());
        }
        return duenos;
    }

    public void setMascotaAModificar(Mascota mascota) {
        this.mascotaAModificar = mascota;
        if (mascota != null) {
            txtNombre.setText(mascota.getNombre());
            cmbEspecie.setValue(mascota.getEspecie());
            txtRaza.setText(mascota.getRaza());
            txtEdad.setText(String.valueOf(mascota.getEdad()));
            txtPeso.setText(String.valueOf(mascota.getPeso()));
            
            // Seleccionar el dueño en el ComboBox
            for (Usuario dueno : cmbCliente.getItems()) {
                if (dueno.getIdUsuario() == mascota.getIdDueno()) {
                    cmbCliente.setValue(dueno);
                    break;
                }
            }
        }
    }

    @FXML
    private void guardarOnAction() {
        if (validarCampos()) {
            try {
                Mascota mascota = new Mascota(
                    mascotaAModificar.getIdMascota(),
                    txtNombre.getText().trim(),
                    cmbEspecie.getValue(),
                    txtRaza.getText().trim(),
                    Integer.parseInt(txtEdad.getText().trim()),
                    Double.parseDouble(txtPeso.getText().trim()),
                    cmbCliente.getValue().getIdUsuario(),
                    mascotaAModificar.getImagenUrl() // Mantener la URL de imagen existente
                );
                
                actualizar(mascota);
                mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Mascota modificada correctamente");
                cerrarVentana();
            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al modificar la mascota: " + e.getMessage());
            }
        }
    }

    private void actualizar(Mascota mascota) throws SQLException {
        String sql = """
            UPDATE mascota 
            SET nombre = ?, especie = ?, raza = ?, edad = ?, peso = ?, id_dueño = ?
            WHERE id_mascota = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, mascota.getNombre());
            pstmt.setString(2, mascota.getEspecie());
            pstmt.setString(3, mascota.getRaza());
            pstmt.setInt(4, mascota.getEdad());
            pstmt.setDouble(5, mascota.getPeso());
            pstmt.setInt(6, mascota.getIdDueno());
            pstmt.setInt(7, mascota.getIdMascota());
            
            pstmt.executeUpdate();
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El nombre es obligatorio");
            return false;
        }
        if (cmbEspecie.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una especie");
            return false;
        }
        if (txtRaza.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La raza es obligatoria");
            return false;
        }
        if (txtEdad.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La edad es obligatoria");
            return false;
        }
        try {
            int edad = Integer.parseInt(txtEdad.getText().trim());
            if (edad < 0 || edad > 30) {
                mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La edad debe estar entre 0 y 30 años");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La edad debe ser un número válido");
            return false;
        }
        if (txtPeso.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El peso es obligatorio");
            return false;
        }
        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());
            if (peso <= 0 || peso > 100) {
                mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El peso debe estar entre 0 y 100 kg");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El peso debe ser un número válido");
            return false;
        }
        if (cmbCliente.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un dueño");
            return false;
        }
        return true;
    }

    @FXML
    private void cancelarOnAction() {
        cerrarVentana();
    }

    @FXML
    private void volverOnAction() throws IOException {
        App.setRoot("gestionarMascotas");
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
} 