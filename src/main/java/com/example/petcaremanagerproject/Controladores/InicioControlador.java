package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class InicioControlador {
    @FXML
    private TextField usuario;
    
    @FXML
    private PasswordField contrasena;
    
    @FXML
    private Text textoUsuario,textoContrasena,errorInicio;

    @FXML
    public void initialize() {
        // Ocultar los mensajes de error al inicio
        textoUsuario.setVisible(false);
        textoContrasena.setVisible(false);
        errorInicio.setVisible(false);
        
        // Crear credenciales por defecto si no existen
        try {
            if (!credencialesExisten()) {
                escribirCredenciales("admin", "12345");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean credencialesExisten() {
        try (DataInputStream dis = new DataInputStream(new FileInputStream("credenciales.bin"))) {
            return dis.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @FXML
    public void iniciarSesion() {
        // Reiniciar mensajes de error
        textoUsuario.setVisible(false);
        textoContrasena.setVisible(false);
        errorInicio.setVisible(false);

        String usuarioText = usuario.getText();
        String contrasenaText = contrasena.getText();

        // Validar campos vacíos
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

        try {
            if (validarCredenciales(usuarioText, contrasenaText)) {
                App.setRoot("menuAdmin");
            } else {
                errorInicio.setVisible(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorInicio.setText("Error del sistema: No se pudo verificar las credenciales");
            errorInicio.setVisible(true);
        }
    }

    private boolean validarCredenciales(String usuario, String password) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream("credenciales.bin"))) {
            while (dis.available() > 0) {
                String usuarioArchivo = dis.readUTF();
                String passwordArchivo = dis.readUTF();
                
                if (usuarioArchivo.equals(usuario) && passwordArchivo.equals(password)) {
                    return true;
                }
            }
        } catch (EOFException e) {
            // Fin del archivo alcanzado
        }
        return false;
    }

    private void escribirCredenciales(String usuario, String password) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("credenciales.bin"))) {
            dos.writeUTF(usuario);
            dos.writeUTF(password);
        }
    }
} 