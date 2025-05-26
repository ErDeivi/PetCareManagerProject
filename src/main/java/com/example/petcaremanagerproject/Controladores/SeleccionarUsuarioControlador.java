package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SeleccionarUsuarioControlador {
    
    @FXML
    private Button usuario;
    @FXML
    private Button administrador;
    @FXML
    private Button cerrarAplicacion;

    public void usuarioOnAction(ActionEvent actionEvent) {
        App.setRoot("inicio");
    }

    public void administradorOnAction(ActionEvent actionEvent) {
        App.setRoot("menuAdmin");
    }

    public void cerrarAplicacionOnAction(ActionEvent actionEvent) {
        System.exit(0);
    }
}