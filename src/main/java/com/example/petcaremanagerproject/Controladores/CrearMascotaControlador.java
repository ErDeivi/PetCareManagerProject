package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.Mascota;
import com.example.petcaremanagerproject.Modelo.Usuario;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.application.Platform;
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
    @FXML private ComboBox<Usuario> cmbCliente;
    @FXML private TextField txtImagenUrl;

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
        List<Usuario> duenos = obtenerDuenos();
        cmbCliente.getItems().addAll(duenos);
        
        cmbCliente.setCellFactory(param -> new ListCell<>() {
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
        
        cmbCliente.setButtonCell(new ListCell<>() {
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

    private List<Usuario> obtenerDuenos() {
        List<Usuario> duenos = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, u.telefono, u.contraseña, u.imagen_url FROM dueño d JOIN usuario u ON d.id_usuario = u.id_usuario ORDER BY u.nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                duenos.add(new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("contraseña"),
                    rs.getString("telefono"),
                    rs.getString("imagen_url")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(AlertType.ERROR, "Error", "Error al cargar dueños: " + e.getMessage());
        }
        return duenos;
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
            txtImagenUrl.setText(mascota.getImagenUrl());
            
            for (Usuario usuario : cmbCliente.getItems()) {
                if (usuario.getIdUsuario() == mascota.getIdDueno()) {
                    cmbCliente.setValue(usuario);
                    break;
                }
            }
        }
    }

    @FXML
    private void guardarOnAction() {
        if (validarCampos()) {
            try {
                int idDueno = cmbCliente.getValue().getIdUsuario();
                
                Mascota mascota = new Mascota(
                    mascotaAModificar != null ? mascotaAModificar.getIdMascota() : 0,
                    txtNombre.getText(),
                    cmbEspecie.getValue(),
                    txtRaza.getText(),
                    Integer.parseInt(txtEdad.getText()),
                    Double.parseDouble(txtPeso.getText()),
                    idDueno,
                    txtImagenUrl.getText().trim()
                );

                if (mascotaAModificar == null) {
                    insertar(mascota);
                    mostrarMensaje(AlertType.INFORMATION, "Éxito", "Mascota creada correctamente");
                } else {
                    actualizar(mascota);
                    mostrarMensaje(AlertType.INFORMATION, "Éxito", "Mascota actualizada correctamente");
                }
                
                volverOnAction();
            } catch (SQLException e) {
                mostrarMensaje(AlertType.ERROR, "Error", "Error al guardar la mascota: " + e.getMessage());
            } catch (IOException e) {
                mostrarMensaje(AlertType.ERROR, "Error", "Error al cerrar la ventana: " + e.getMessage());
            }
        }
    }

    private void insertar(Mascota mascota) throws SQLException {
        String sql = "INSERT INTO mascota (nombre, especie, raza, edad, peso, id_dueño, imagen_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mascota.getNombre());
            pstmt.setString(2, mascota.getEspecie());
            pstmt.setString(3, mascota.getRaza());
            pstmt.setInt(4, mascota.getEdad());
            pstmt.setDouble(5, mascota.getPeso());
            pstmt.setInt(6, mascota.getIdDueno());
            pstmt.setString(7, mascota.getImagenUrl());
            
            pstmt.executeUpdate();
        }
    }

    private void actualizar(Mascota mascota) throws SQLException {
        String sql = "UPDATE mascota SET nombre = ?, especie = ?, raza = ?, edad = ?, peso = ?, id_dueño = ?, imagen_url = ? WHERE id_mascota = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mascota.getNombre());
            pstmt.setString(2, mascota.getEspecie());
            pstmt.setString(3, mascota.getRaza());
            pstmt.setInt(4, mascota.getEdad());
            pstmt.setDouble(5, mascota.getPeso());
            pstmt.setInt(6, mascota.getIdDueno());
            pstmt.setString(7, mascota.getImagenUrl());
            pstmt.setInt(8, mascota.getIdMascota());
            
            pstmt.executeUpdate();
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(AlertType.WARNING, "Advertencia", "El nombre es obligatorio");
            return false;
        }
        // Eliminada validación de formato de nombre según el esquema proporcionado

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