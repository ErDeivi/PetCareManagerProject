package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginControlador {
    @FXML
    private TextField usuario;
    
    @FXML
    private PasswordField contrasena;
    
    @FXML
    private Text error;
    
    @FXML
    private void iniciarSesion() {
        String user = usuario.getText();
        String pass = contrasena.getText();
        if (user.equals("admin") && pass.equals("admin")) {
            error.setVisible(false);
            App.setRoot("menuAdmin");
        } else {
            error.setVisible(true);
        }
    }
} 