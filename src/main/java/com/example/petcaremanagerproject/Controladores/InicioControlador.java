package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
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

    // Aquí almacenaremos el hash de la contraseña del administrador
    // DEBES REEMPLAZAR ESTO CON EL HASH REAL GENERADO POR TI
    private static final String ADMIN_PASSWORD_HASH = "$2a$10$HAhys42b0nQjLSTB.rQ48u5kvMnw93tGhMl7Nt7PMN8hAqcTsJbK6"; // <<< REEMPLAZAR CON EL HASH BCrypt
    private static final String ADMIN_USER = "admin";

    @FXML
    public void initialize() {
        textoUsuario.setVisible(false);
        textoContrasena.setVisible(false);
        errorInicio.setVisible(false);
    }

    @FXML
    public void iniciarSesion() {
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

        // La lógica de validación se implementará aquí usando BCrypt
        if (validarCredenciales(usuarioText, contrasenaText)) {
            App.setRoot("menuAdmin"); // Asumimos que el admin va al menuAdmin
        } else {
            errorInicio.setText("Usuario o contraseña incorrectos");
            errorInicio.setVisible(true);
        }
    }

    private boolean validarCredenciales(String usuarioIngresado, String passwordIngresada) {
        // Verificar si el usuario ingresado es el admin y si la contraseña coincide con el hash
        if (ADMIN_USER.equals(usuarioIngresado)) {
            // Usar BCrypt.checkpw para comparar la contraseña ingresada con el hash almacenado
            // Esto es seguro porque maneja el 'salt' automáticamente desde el hash.
            return BCrypt.checkpw(passwordIngresada, ADMIN_PASSWORD_HASH);
        }
        // Si el usuario no es el admin, o si hubiera otros usuarios en BD (no aplica en este caso simplificado),
        // se manejaría aquí. Para este caso, cualquier otro usuario o contraseña es inválido.
        return false;
    }
} 