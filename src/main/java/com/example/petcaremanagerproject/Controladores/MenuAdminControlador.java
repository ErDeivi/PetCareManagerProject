package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import javafx.event.ActionEvent;

public class MenuAdminControlador {

        public void gestionarVideojuegoOnAction(ActionEvent actionEvent) {
                App.setRoot("listadoVideojuegos");
        }

        public void gestionarProgramadorOnAction(ActionEvent actionEvent) {
                App.setRoot("listadoProgramadores");
        }

        public void gestionarBandaSonoraOnAction(ActionEvent actionEvent) {
                App.setRoot("listadoBandasSonoras");
        }

        public void exportarBD(ActionEvent actionEvent) {
                // ... código para exportar base de datos ...
        }

        public void cambiarContrasenaOnAction(ActionEvent actionEvent) {
                App.setRoot("cambiarContrasena");

        }

        public void cerrarSesionOnAction(ActionEvent actionEvent) {
                App.setRoot("inicio");

        }

        public void importarBD(ActionEvent actionEvent) {

        }
}
