package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.AdminConfigManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

/**
 * Controlador para la pantalla de inicio de sesión.
 * Maneja la autenticación de usuarios y la validación de credenciales.
 */
public class LoginControlador {
    @FXML
    private TextField usuario;
    
    @FXML
    private PasswordField contrasena;
    
    @FXML
    private Text textoUsuario, textoContrasena, error;

    /** Nombre de usuario administrador por defecto */
    private static final String ADMIN_USER = "admin";

    /**
     * Inicializa el controlador configurando la visibilidad de los mensajes de error
     * y cargando el hash de la contraseña del administrador.
     */
    @FXML
    public void initialize() {
        textoUsuario.setVisible(false);
        textoContrasena.setVisible(false);
        error.setVisible(false);
        AdminConfigManager.loadAdminHash();
    }
    
    /**
     * Maneja el evento de inicio de sesión.
     * Valida los campos de entrada y autentica al usuario.
     * @throws IOException si hay un error al cambiar de vista
     */
    @FXML
    private void iniciarSesion() throws IOException {
        textoUsuario.setVisible(false);
        textoContrasena.setVisible(false);
        error.setVisible(false);

        String usuarioText = usuario.getText();
        String contrasenaText = contrasena.getText();

        boolean hayError = false;
        if (usuarioText.isEmpty()) {
            textoUsuario.setVisible(true);
            hayError = true;
        }
        if (contrasenaText.isEmpty()) {
            textoContrasena.setVisible(true);
            hayError = true;
        }
        if (hayError) {
            return;
        }

        if (validarCredenciales(usuarioText, contrasenaText)) {
            error.setVisible(false);
            App.setRoot("menuAdmin");
        } else {
            error.setText("Usuario o contraseña incorrectos");
            error.setVisible(true);
        }
    }

    /**
     * Valida las credenciales del usuario contra el hash almacenado.
     * @param usuarioIngresado Nombre de usuario ingresado
     * @param passwordIngresada Contraseña ingresada
     * @return true si las credenciales son válidas, false en caso contrario
     */
    private boolean validarCredenciales(String usuarioIngresado, String passwordIngresada) {
        if (ADMIN_USER.equals(usuarioIngresado)) {
            String adminHash = AdminConfigManager.loadAdminHash();
            if (adminHash != null) {
                return BCrypt.checkpw(passwordIngresada, adminHash);
            }
        }
        return false;
    }
}