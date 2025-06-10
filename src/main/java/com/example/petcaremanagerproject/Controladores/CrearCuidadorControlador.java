package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.Modelo.Cuidador;
import com.example.petcaremanagerproject.Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Controlador para la creación y modificación de cuidadores en el sistema.
 * Gestiona la interfaz de usuario para añadir nuevos cuidadores o modificar los existentes.
 */
public class CrearCuidadorControlador {
    @FXML private Label lblTitulo,lblContrasena,lblContrasenaActual,lblNuevaContrasena;
    @FXML private TextField txtNombre,txtCorreo,txtTelefono,txtImagenUrl;
    @FXML private PasswordField pwdContrasena, pwdContrasenaActual, pwdNuevaContrasena;

    private Cuidador cuidadorAModificar;

    /**
     * Inicializa el controlador. No requiere configuración adicional.
     */
    @FXML
    public void initialize() {
        // No hay campos adicionales que ocultar según el esquema actual.
    }

    /**
     * Configura el controlador para crear un nuevo cuidador o modificar uno existente.
     * @param cuidador El cuidador a modificar, o null si se está creando uno nuevo
     */
    public void setCuidadorAModificar(Cuidador cuidador) {
        this.cuidadorAModificar = cuidador;
        if (cuidador != null) {
            lblTitulo.setText("Modificar Cuidador");
            txtNombre.setText(cuidador.getNombre());
            txtCorreo.setText(cuidador.getCorreo());
            txtTelefono.setText(cuidador.getTelefono());
            txtImagenUrl.setText(cuidador.getImagenUrl());
            
            // Ocultar campo de contraseña simple y mostrar campos de modificación
            lblContrasena.setVisible(false);
            pwdContrasena.setVisible(false);
            lblContrasenaActual.setVisible(true);
            pwdContrasenaActual.setVisible(true);
            lblNuevaContrasena.setVisible(true);
            pwdNuevaContrasena.setVisible(true);
        } else {
             lblTitulo.setText("Nuevo Cuidador");
              // Si es un nuevo cuidador, mostrar solo el campo de contraseña simple
            lblContrasena.setVisible(true);
            pwdContrasena.setVisible(true);
            lblContrasenaActual.setVisible(false);
            pwdContrasenaActual.setVisible(false);
            lblNuevaContrasena.setVisible(false);
            pwdNuevaContrasena.setVisible(false);
        }
    }

    /**
     * Maneja el evento de guardar los datos del cuidador.
     * Crea un nuevo cuidador o actualiza uno existente según corresponda.
     */
    @FXML
    private void guardarOnAction() {
        if (validarCampos()) {
            try {
                if (cuidadorAModificar == null) {
                    crear();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cuidador creado correctamente");
                } else {
                    actualizar();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cuidador actualizado correctamente");
                }
                cerrarVentana();
            } catch (SQLException e) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                    "Error al guardar el cuidador: " + e.getMessage());
            } catch (RuntimeException e) { 
                 mostrarMensaje(Alert.AlertType.ERROR, "Error", 
                    "Error al guardar el cuidador: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja el evento de cancelar la operación actual.
     * Cierra la ventana sin guardar cambios.
     */
    @FXML
    private void cancelarOnAction() {
        cerrarVentana();
    }

    /**
     * Valida todos los campos del formulario.
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El nombre es obligatorio");
            return false;
        }

        // Validar correo electrónico
        if (txtCorreo.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El correo es obligatorio");
            return false;
        }
        if (!txtCorreo.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El formato del correo electrónico no es válido");
            return false;
        }

        // Validar teléfono
        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El teléfono es obligatorio");
            return false;
        }
        if (!txtTelefono.getText().trim().matches("^[6-9]\\d{8}$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "El teléfono debe ser un número de 9 dígitos que comience por 6, 7, 8 o 9");
            return false;
        }

        // Validación de contraseñas
        if (cuidadorAModificar == null) {
            // Validación para nuevo cuidador
            if (pwdContrasena.getText().trim().isEmpty()) {
                mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La contraseña es obligatoria para nuevos cuidadores");
                return false;
            }
            if (pwdContrasena.getText().length() < 6) {
                mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La contraseña debe tener al menos 6 caracteres");
                return false;
            }
        } else {
            // Validación para modificar contraseña
            if (!pwdContrasenaActual.getText().trim().isEmpty() || !pwdNuevaContrasena.getText().trim().isEmpty()) {
                // Si se intenta cambiar la contraseña
                if (pwdContrasenaActual.getText().trim().isEmpty()) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe ingresar la contraseña actual");
                    return false;
                }
                if (pwdNuevaContrasena.getText().trim().isEmpty()) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Debe ingresar la nueva contraseña");
                    return false;
                }
                if (pwdNuevaContrasena.getText().length() < 6) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La nueva contraseña debe tener al menos 6 caracteres");
                    return false;
                }
                if (!validarContrasenaActual()) {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error", "La contraseña actual es incorrecta");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Valida que la contraseña actual proporcionada sea correcta.
     * @return true si la contraseña es correcta, false en caso contrario
     */
    private boolean validarContrasenaActual() {
        String sql = "SELECT contraseña FROM usuario WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cuidadorAModificar.getIdUsuario());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String contrasenaActual = rs.getString("contraseña");
                return contrasenaActual.equals(pwdContrasenaActual.getText().trim());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Crea un nuevo cuidador en la base de datos.
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
    private void crear() throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (nombre, correo, contraseña, telefono, imagen_url) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                pstmtUsuario.setString(1, txtNombre.getText().trim());
                pstmtUsuario.setString(2, txtCorreo.getText().trim());
                pstmtUsuario.setString(3, pwdContrasena.getText().trim());
                pstmtUsuario.setString(4, txtTelefono.getText().trim());
                pstmtUsuario.setString(5, txtImagenUrl.getText().trim());
                pstmtUsuario.executeUpdate();
                
                ResultSet rs = pstmtUsuario.getGeneratedKeys();
                int idUsuario = -1;
                if (rs.next()) {
                    idUsuario = rs.getInt(1);
                }

                if (idUsuario == -1) {
                    conn.rollback();
                    throw new SQLException("No se pudo obtener el ID del usuario insertado");
                }

                String sqlCuidadorSimple = "INSERT INTO cuidador (id_usuario) VALUES (?)";
                try (PreparedStatement pstmtCuidador = conn.prepareStatement(sqlCuidadorSimple)) {
                    pstmtCuidador.setInt(1, idUsuario);
                    pstmtCuidador.executeUpdate();
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error al crear cuidador: " + e.getMessage());
            }
        }
    }

    private void actualizar() throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Actualizar datos básicos en la tabla usuario
            String sqlUsuario = "UPDATE usuario SET nombre = ?, correo = ?, telefono = ?, imagen_url = ?";
            // Si se está cambiando la contraseña, añadirla a la actualización
            if (!pwdNuevaContrasena.getText().trim().isEmpty()) {
                sqlUsuario += ", contraseña = ?";
            }
            sqlUsuario += " WHERE id_usuario = ?";

            try (PreparedStatement pstmtUsuario = conn.prepareStatement(sqlUsuario)) {
                int paramIndex = 1;
                pstmtUsuario.setString(paramIndex++, txtNombre.getText().trim());
                pstmtUsuario.setString(paramIndex++, txtCorreo.getText().trim());
                pstmtUsuario.setString(paramIndex++, txtTelefono.getText().trim());
                pstmtUsuario.setString(paramIndex++, txtImagenUrl.getText().trim());

                if (!pwdNuevaContrasena.getText().trim().isEmpty()) {
                    pstmtUsuario.setString(paramIndex++, pwdNuevaContrasena.getText().trim());
                }

                pstmtUsuario.setInt(paramIndex, cuidadorAModificar.getIdUsuario());
                pstmtUsuario.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
            throw new SQLException("Error al actualizar cuidador: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
        }
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