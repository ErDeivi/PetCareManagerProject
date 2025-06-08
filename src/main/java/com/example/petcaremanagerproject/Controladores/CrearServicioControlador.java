package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Modelo.*;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CrearServicioControlador {
    @FXML private Label lblTitulo;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private TextArea txtDescripcionCategoria;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextArea txtObservaciones;
    @FXML private ComboBox<Mascota> cmbMascota;
    @FXML private ComboBox<Usuario> cmbCuidador;
    @FXML private ComboBox<Usuario> cmbDueno;
    @FXML private DatePicker dpFechaSolicitud;
    @FXML private Spinner<Integer> spnHoraSolicitud;
    @FXML private Spinner<Integer> spnMinutoSolicitud;
    @FXML private DatePicker dpFechaProgramada;
    @FXML private Spinner<Integer> spnHoraProgramada;
    @FXML private Spinner<Integer> spnMinutoProgramada;

    private Servicio servicioAModificar;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        configurarComboBoxes();
        configurarSpinners();
        cargarDatos();
    }

    private void configurarSpinners() {
        // Configurar formato de los spinners
        spnHoraSolicitud.setEditable(true);
        spnMinutoSolicitud.setEditable(true);
        spnHoraProgramada.setEditable(true);
        spnMinutoProgramada.setEditable(true);

        // Establecer valores por defecto
        LocalTime ahora = LocalTime.now();
        spnHoraSolicitud.getValueFactory().setValue(ahora.getHour());
        spnMinutoSolicitud.getValueFactory().setValue(ahora.getMinute());
        spnHoraProgramada.getValueFactory().setValue(ahora.plusHours(1).getHour());
        spnMinutoProgramada.getValueFactory().setValue(ahora.getMinute());
    }

    private void configurarComboBoxes() {
        // Configurar estados
        cmbEstado.getItems().addAll("Pendiente", "En proceso", "Completado", "Cancelado");
        
        // Configurar listener para mostrar la descripción de la categoría
        cmbCategoria.setOnAction(event -> {
            Categoria categoriaSeleccionada = cmbCategoria.getValue();
            if (categoriaSeleccionada != null) {
                txtDescripcionCategoria.setText(categoriaSeleccionada.getDescripcion());
            } else {
                txtDescripcionCategoria.clear();
            }
        });

        // Configurar cell factories para mostrar nombres en lugar de IDs
        cmbMascota.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Mascota mascota, boolean empty) {
                super.updateItem(mascota, empty);
                if (empty || mascota == null) {
                    setText(null);
                } else {
                    setText(mascota.getNombre() + " (" + mascota.getEspecie() + ")");
                }
            }
        });

        cmbCuidador.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario cuidador, boolean empty) {
                super.updateItem(cuidador, empty);
                if (empty || cuidador == null) {
                    setText(null);
                } else {
                    setText(cuidador.getNombre());
                }
            }
        });

        cmbDueno.setCellFactory(param -> new ListCell<>() {
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

        // Configurar button cells
        cmbMascota.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Mascota mascota, boolean empty) {
                super.updateItem(mascota, empty);
                if (empty || mascota == null) {
                    setText(null);
                } else {
                    setText(mascota.getNombre() + " (" + mascota.getEspecie() + ")");
                }
            }
        });

        cmbCuidador.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Usuario cuidador, boolean empty) {
                super.updateItem(cuidador, empty);
                if (empty || cuidador == null) {
                    setText(null);
                } else {
                    setText(cuidador.getNombre());
                }
            }
        });

        cmbDueno.setButtonCell(new ListCell<>() {
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
        cargarCategorias();
        cargarMascotas();
        cargarCuidadores();
        cargarDuenos();
        
        // Establecer valores por defecto
        dpFechaSolicitud.setValue(LocalDate.now());
        dpFechaProgramada.setValue(LocalDate.now());
        cmbEstado.setValue("Pendiente");
    }

    private void cargarCategorias() {
        String sql = "SELECT * FROM categoria ORDER BY tipo";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cmbCategoria.getItems().add(new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("tipo"),
                    rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al cargar categorías: " + e.getMessage());
        }
    }

    private void cargarMascotas() {
        String sql = "SELECT m.*, u.nombre as dueno_nombre FROM mascota m JOIN dueño d ON m.id_dueño = d.id_usuario JOIN usuario u ON d.id_usuario = u.id_usuario ORDER BY m.nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cmbMascota.getItems().add(new Mascota(
                    rs.getInt("id_mascota"),
                    rs.getString("nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getInt("edad"),
                    rs.getDouble("peso"),
                    rs.getInt("id_dueño"),
                    rs.getString("imagen_url")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al cargar mascotas: " + e.getMessage());
        }
    }

    private void cargarCuidadores() {
        String sql = "SELECT u.* FROM usuario u JOIN cuidador c ON u.id_usuario = c.id_usuario ORDER BY u.nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cmbCuidador.getItems().add(new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("contraseña"),
                    rs.getString("telefono"),
                    rs.getString("imagen_url")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al cargar cuidadores: " + e.getMessage());
        }
    }

    private void cargarDuenos() {
        String sql = "SELECT u.* FROM usuario u JOIN dueño d ON u.id_usuario = d.id_usuario ORDER BY u.nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cmbDueno.getItems().add(new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("contraseña"),
                    rs.getString("telefono"),
                    rs.getString("imagen_url")
                ));
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al cargar dueños: " + e.getMessage());
        }
    }

    public void setServicioAModificar(Servicio servicio) {
        this.servicioAModificar = servicio;
        lblTitulo.setText("Modificar Servicio");
        cargarDatosServicio();
    }

    private void cargarDatosServicio() {
        if (servicioAModificar != null) {
            // Buscar y seleccionar la categoría
            for (Categoria categoria : cmbCategoria.getItems()) {
                if (categoria.getIdCategoria() == servicioAModificar.getIdCategoria()) {
                    cmbCategoria.setValue(categoria);
                    break;
                }
            }

            cmbEstado.setValue(servicioAModificar.getEstado());
            txtObservaciones.setText(servicioAModificar.getObservaciones());

            // Buscar y seleccionar la mascota
            for (Mascota mascota : cmbMascota.getItems()) {
                if (mascota.getIdMascota() == servicioAModificar.getIdMascota()) {
                    cmbMascota.setValue(mascota);
                    break;
                }
            }

            // Buscar y seleccionar el cuidador
            for (Usuario cuidador : cmbCuidador.getItems()) {
                if (cuidador.getIdUsuario() == servicioAModificar.getIdCuidador()) {
                    cmbCuidador.setValue(cuidador);
                    break;
                }
            }

            // Buscar y seleccionar el dueño
            for (Usuario usuario : cmbDueno.getItems()) {
                if (usuario.getIdUsuario() == servicioAModificar.getIdDueno()) {
                    cmbDueno.setValue(usuario);
                    break;
                }
            }

            // Establecer fechas y horas
            dpFechaSolicitud.setValue(servicioAModificar.getFechaSolicitud().toLocalDate());
            spnHoraSolicitud.getValueFactory().setValue(servicioAModificar.getFechaSolicitud().getHour());
            spnMinutoSolicitud.getValueFactory().setValue(servicioAModificar.getFechaSolicitud().getMinute());
            
            dpFechaProgramada.setValue(servicioAModificar.getFechaProgramada().toLocalDate());
            spnHoraProgramada.getValueFactory().setValue(servicioAModificar.getFechaProgramada().getHour());
            spnMinutoProgramada.getValueFactory().setValue(servicioAModificar.getFechaProgramada().getMinute());
        }
    }

    @FXML
    private void guardarOnAction() {
        try {
            if (validarCampos()) {
                if (servicioAModificar == null) {
                    crearServicio();
                } else {
                    modificarServicio();
                }
                cerrarVentana();
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Error al guardar el servicio: " + e.getMessage());
        }
    }

    @FXML
    private void cancelarOnAction() {
        cerrarVentana();
    }

    private boolean validarCampos() {
        if (cmbCategoria.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una categoría");
            return false;
        }
        if (cmbEstado.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un estado");
            return false;
        }
        if (cmbMascota.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una mascota");
            return false;
        }
        if (cmbCuidador.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un cuidador");
            return false;
        }
        if (cmbDueno.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un dueño");
            return false;
        }
        if (dpFechaSolicitud.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una fecha de solicitud");
            return false;
        }
        if (spnHoraSolicitud.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una hora de solicitud");
            return false;
        }
        if (dpFechaProgramada.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una fecha programada");
            return false;
        }
        if (spnHoraProgramada.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una hora programada");
            return false;
        }

        // Validar que la fecha programada no sea anterior a la fecha de solicitud
        LocalDateTime fechaSolicitud = LocalDateTime.of(
            dpFechaSolicitud.getValue(),
            LocalTime.of(spnHoraSolicitud.getValue(), spnMinutoSolicitud.getValue())
        );
        LocalDateTime fechaProgramada = LocalDateTime.of(
            dpFechaProgramada.getValue(),
            LocalTime.of(spnHoraProgramada.getValue(), spnMinutoProgramada.getValue())
        );

        if (fechaProgramada.isBefore(fechaSolicitud)) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", 
                "La fecha programada no puede ser anterior a la fecha de solicitud");
            return false;
        }

        return true;
    }

    private void crearServicio() throws SQLException {
        String sql = "INSERT INTO servicio (id_categoria, estado, observaciones, id_mascota, id_cuidador, id_dueño, fecha_solicitud, fecha_programada) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cmbCategoria.getValue().getIdCategoria());
            pstmt.setString(2, cmbEstado.getValue());
            pstmt.setString(3, txtObservaciones.getText().trim());
            pstmt.setInt(4, cmbMascota.getValue().getIdMascota());
            pstmt.setInt(5, cmbCuidador.getValue().getIdUsuario());
            pstmt.setInt(6, cmbDueno.getValue().getIdUsuario());
            
            LocalDateTime fechaSolicitud = LocalDateTime.of(
                dpFechaSolicitud.getValue(),
                LocalTime.of(spnHoraSolicitud.getValue(), spnMinutoSolicitud.getValue())
            );
            LocalDateTime fechaProgramada = LocalDateTime.of(
                dpFechaProgramada.getValue(),
                LocalTime.of(spnHoraProgramada.getValue(), spnMinutoProgramada.getValue())
            );
            
            pstmt.setTimestamp(7, Timestamp.valueOf(fechaSolicitud));
            pstmt.setTimestamp(8, Timestamp.valueOf(fechaProgramada));

            pstmt.executeUpdate();
            mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Servicio creado correctamente");
        }
    }

    private void modificarServicio() throws SQLException {
        String sql = "UPDATE servicio SET id_categoria = ?, estado = ?, observaciones = ?, id_mascota = ?, id_cuidador = ?, id_dueño = ?, fecha_solicitud = ?, fecha_programada = ? WHERE id_servicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cmbCategoria.getValue().getIdCategoria());
            pstmt.setString(2, cmbEstado.getValue());
            pstmt.setString(3, txtObservaciones.getText().trim());
            pstmt.setInt(4, cmbMascota.getValue().getIdMascota());
            pstmt.setInt(5, cmbCuidador.getValue().getIdUsuario());
            pstmt.setInt(6, cmbDueno.getValue().getIdUsuario());
            
            LocalDateTime fechaSolicitud = LocalDateTime.of(
                dpFechaSolicitud.getValue(),
                LocalTime.of(spnHoraSolicitud.getValue(), spnMinutoSolicitud.getValue())
            );
            LocalDateTime fechaProgramada = LocalDateTime.of(
                dpFechaProgramada.getValue(),
                LocalTime.of(spnHoraProgramada.getValue(), spnMinutoProgramada.getValue())
            );
            
            pstmt.setTimestamp(7, Timestamp.valueOf(fechaSolicitud));
            pstmt.setTimestamp(8, Timestamp.valueOf(fechaProgramada));
            pstmt.setInt(9, servicioAModificar.getIdServicio());

            pstmt.executeUpdate();
            mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Servicio modificado correctamente");
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
      //  App.configurarVentanaModal(stage);
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