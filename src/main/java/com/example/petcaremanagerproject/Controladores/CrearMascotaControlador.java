package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Cliente;
import com.example.petcaremanagerproject.Modelo.Mascota;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrearMascotaControlador {
    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbEspecie;
    @FXML private TextField txtRaza;
    @FXML private TextField txtEdad;
    @FXML private TextField txtPeso;
    @FXML private ComboBox<Cliente> cmbCliente;

    private Mascota mascotaAModificar;

    @FXML
    public void initialize() {
        configurarCampos();
        cargarDatos();
    }

    private void configurarCampos() {
        cmbEspecie.getItems().addAll("Perro", "Gato", "Ave", "Reptil", "Otro");
        
        txtEdad.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtEdad.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtPeso.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                txtPeso.setText(oldValue);
            }
        });
    }

    private void cargarDatos() {
        List<Cliente> clientes = obtenerClientes();
        cmbCliente.getItems().addAll(clientes);
        
        cmbCliente.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Cliente cliente, boolean empty) {
                super.updateItem(cliente, empty);
                if (empty || cliente == null) {
                    setText(null);
                } else {
                    setText(cliente.getNombre());
                }
            }
        });
        
        cmbCliente.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cliente cliente, boolean empty) {
                super.updateItem(cliente, empty);
                if (empty || cliente == null) {
                    setText(null);
                } else {
                    setText(cliente.getNombre());
                }
            }
        });
    }

    private List<Cliente> obtenerClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, u.telefono FROM dueño d JOIN usuario u ON d.id_usuario = u.id_usuario ORDER BY u.nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("telefono")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(AlertType.ERROR, "Error", "Error al cargar dueños: " + e.getMessage());
        }
        return clientes;
    }

    public void setMascotaAModificar(Mascota mascota) {
        this.mascotaAModificar = mascota;
        if (mascota != null) {
            lblTitulo.setText("Modificar Mascota");
            txtNombre.setText(mascota.getNombre());
            cmbEspecie.setValue(mascota.getEspecie());
            txtRaza.setText(mascota.getRaza());
            txtEdad.setText(String.valueOf(mascota.getEdad()));
            txtPeso.setText(String.valueOf(mascota.getPeso()));
            
            for (Cliente cliente : cmbCliente.getItems()) {
                if (cliente.getNombre().equals(mascota.getCliente())) {
                    cmbCliente.setValue(cliente);
                    break;
                }
            }
        }
    }

    @FXML
    private void guardarOnAction() {
        if (validarCampos()) {
            try {
                int idCliente = cmbCliente.getValue().getId();
                String nombreCliente = cmbCliente.getValue().getNombre();
                
                Mascota mascota = new Mascota(
                    mascotaAModificar != null ? mascotaAModificar.getId() : 0,
                    txtNombre.getText(),
                    cmbEspecie.getValue(),
                    txtRaza.getText(),
                    Integer.parseInt(txtEdad.getText()),
                    Double.parseDouble(txtPeso.getText()),
                    nombreCliente,
                    idCliente
                );

                if (mascotaAModificar == null) {
                    insertar(mascota);
                    mostrarMensaje(AlertType.INFORMATION, "Éxito", "Mascota creada correctamente");
                } else {
                    actualizar(mascota);
                    mostrarMensaje(AlertType.INFORMATION, "Éxito", "Mascota actualizada correctamente");
                }
                
                volverOnAction();
            } catch (Exception e) {
                mostrarMensaje(AlertType.ERROR, "Error", "Error al guardar la mascota: " + e.getMessage());
            }
        }
    }

    private void insertar(Mascota mascota) {
        String sql = "INSERT INTO mascota (nombre, especie, raza, edad, peso, id_dueño) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, mascota.getNombre());
                pstmt.setString(2, mascota.getEspecie());
                pstmt.setString(3, mascota.getRaza());
                pstmt.setInt(4, mascota.getEdad());
                pstmt.setDouble(5, mascota.getPeso());
                pstmt.setInt(6, mascota.getIdCliente());
                
                pstmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al insertar mascota", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la conexión a la base de datos", e);
        }
    }

    private void actualizar(Mascota mascota) {
        String sql = "UPDATE mascota SET nombre = ?, especie = ?, raza = ?, edad = ?, peso = ?, id_dueño = ? WHERE id_mascota = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, mascota.getNombre());
                pstmt.setString(2, mascota.getEspecie());
                pstmt.setString(3, mascota.getRaza());
                pstmt.setInt(4, mascota.getEdad());
                pstmt.setDouble(5, mascota.getPeso());
                pstmt.setInt(6, mascota.getIdCliente());
                pstmt.setInt(7, mascota.getId());
                
                pstmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error al actualizar mascota", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la conexión a la base de datos", e);
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "El nombre es obligatorio");
            return false;
        }
        if (!txtNombre.getText().trim().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$")) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "El nombre solo debe contener letras y espacios");
            return false;
        }

        if (cmbEspecie.getValue() == null || cmbEspecie.getValue().trim().isEmpty()) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "Debe seleccionar una especie");
            return false;
        }
        if (txtRaza.getText().trim().isEmpty()) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "La raza es obligatoria");
            return false;
        }
        if (txtEdad.getText().trim().isEmpty()) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "La edad es obligatoria");
            return false;
        }
        try {
            int edad = Integer.parseInt(txtEdad.getText().trim());
            if (edad < 0) {
                 mostrarMensaje(AlertType.WARNING, "Advertencia", "La edad no puede ser negativa");
                 return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "La edad debe ser un número entero válido");
            return false;
        }

        if (txtPeso.getText().trim().isEmpty()) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "El peso es obligatorio");
            return false;
        }
        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());
            if (peso < 0) {
                mostrarMensaje(AlertType.WARNING, "Advertencia", "El peso no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "El peso debe ser un número válido");
            return false;
        }

        if (cmbCliente.getValue() == null) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "Debe seleccionar un dueño");
            return false;
        }
        return true;
    }

    @FXML
    private void cancelarOnAction() throws IOException {
        volverOnAction();
    }

    @FXML
    private void volverOnAction() throws IOException {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
} 