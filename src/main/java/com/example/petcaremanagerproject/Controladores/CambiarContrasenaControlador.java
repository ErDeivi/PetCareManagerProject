package com.example.petcaremanagerproject.Controladores;

import com.example.petcaremanagerproject.App;
import com.example.petcaremanagerproject.Util.AdminConfigManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class CambiarContrasenaControlador {
    @FXML
    private PasswordField contrasenaVieja, contrasenaNueva, contrasenaNueva2;
    @FXML
    private Text contrasenaCoincidir,camposVacios,contrasenaAntigua, mensajeExito;
    @FXML
    private Button enviar,volver;
    @FXML
    public void initialize() {
        contrasenaCoincidir.setVisible(false);
        camposVacios.setVisible(false);
        contrasenaAntigua.setVisible(false);
        mensajeExito.setVisible(false);
    }

    public void cambiarContrasenaOnAction(ActionEvent actionEvent) {
        contrasenaCoincidir.setVisible(false);
        camposVacios.setVisible(false);
        contrasenaAntigua.setVisible(false);
        mensajeExito.setVisible(false);

        String vieja = contrasenaVieja.getText();
        String nueva = contrasenaNueva.getText();
        String nueva2 = contrasenaNueva2.getText();

        if (vieja.isEmpty() || nueva.isEmpty() || nueva2.isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Todos los campos son obligatorios.");
            return;
        }

        if (!nueva.equals(nueva2)) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Las nuevas contraseñas no coinciden.");
            return;
        }

        if (nueva.length() < 5) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "La nueva contraseña debe tener al menos 5 caracteres.");
            return;
        }

        String adminHashActual = AdminConfigManager.loadAdminHash();

        if (adminHashActual != null && BCrypt.checkpw(vieja, adminHashActual)) {
            String nuevoHash = BCrypt.hashpw(nueva, BCrypt.gensalt());

            AdminConfigManager.saveAdminHash(nuevoHash);

            mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Contraseña cambiada exitosamente.");
            
            contrasenaVieja.clear();
            contrasenaNueva.clear();
            contrasenaNueva2.clear();

        } else {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "La contraseña actual es incorrecta.");
        }
    }

    public void volverOnAction(ActionEvent actionEvent) throws IOException {
        App.setRoot("menuAdmin");
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
