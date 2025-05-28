package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.AdminConfigManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class InicioControlador {
    @FXML
    private TextField usuario;
    @FXML
    private PasswordField contrasena;
    @FXML
    private Text textoUsuario,textoContrasena,errorInicio;

    private static final String ADMIN_USER = "admin";

    @FXML
    public void initialize() {
        textoUsuario.setVisible(false);
        textoContrasena.setVisible(false);
        errorInicio.setVisible(false);
        AdminConfigManager.loadAdminHash();
    }

    @FXML
    public void iniciarSesion() throws IOException {
        textoUsuario.setVisible(false);
        textoContrasena.setVisible(false);
        errorInicio.setVisible(false);

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
            App.setRoot("menuAdmin");
        } else {
            errorInicio.setText("Usuario o contraseña incorrectos");
            errorInicio.setVisible(true);
        }
    }

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