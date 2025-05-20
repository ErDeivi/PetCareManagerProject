package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CambiarContrasenaControlador {
    @FXML
    private PasswordField contrasenaVieja, contrasenaNueva, contrasenaNueva2;
    @FXML
    private Text contrasenaCoincidir,camposVacios,contrasenaAntigua;
    @FXML
    private Button enviar,volver;
    @FXML
    public void initialize() {
        contrasenaCoincidir.setVisible(false);
        camposVacios.setVisible(false);
        contrasenaAntigua.setVisible(false);
    }

    public void cambiarContrasenaOnAction(ActionEvent actionEvent) {
        contrasenaCoincidir.setVisible(false);
        camposVacios.setVisible(false);
        contrasenaAntigua.setVisible(false);

        if (contrasenaVieja.getText().isEmpty() || contrasenaNueva.getText().isEmpty() || contrasenaNueva2.getText().isEmpty()) {
            camposVacios.setVisible(true);
            return;
        }

        if (!contrasenaNueva.getText().equals(contrasenaNueva2.getText())) {
            contrasenaCoincidir.setVisible(true);
            return;
        }

        if (actualizarContrasena(contrasenaVieja.getText(), contrasenaNueva.getText())) {
            App.setRoot("menuAdmin");
        } else {
            contrasenaAntigua.setVisible(true);
        }
    }

    private boolean actualizarContrasena(String contrasenaVieja, String contrasenaNueva) {
        String usuarioGuardado = null;
        String contrasenaGuardada = null;

        // Leer las credenciales actuales
        try (DataInputStream dis = new DataInputStream(new FileInputStream("credenciales.bin"))) {
            usuarioGuardado = dis.readUTF();
            contrasenaGuardada = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Verificar si la contraseña vieja coincide
        if (contrasenaGuardada != null && contrasenaGuardada.equals(contrasenaVieja)) {
            // Escribir las nuevas credenciales
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("credenciales.bin"))) {
                dos.writeUTF(usuarioGuardado);
                dos.writeUTF(contrasenaNueva);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public void volverOnAction(ActionEvent actionEvent) {
        App.setRoot("menuAdmin");
    }
}
