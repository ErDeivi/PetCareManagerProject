package com.example.petcaremanagerproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.example.petcaremanagerproject.Modelo.Mascota;
import com.example.petcaremanagerproject.Modelo.Servicio;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage; // Almacenar el escenario principal
    private static Mascota mascotaModificar;
    private static Servicio servicioModificar;

    @Override
    public void start(Stage stage) throws IOException {
        // Guardar el escenario principal
        primaryStage = stage;
        
        // Cargar la vista inicial
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("PetCare Manager");
        stage.setScene(scene);
        stage.setMaximized(true); // Establecer la ventana maximizada
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setRoot(String fxml) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("El Stage principal no está inicializado");
        }
        
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
        // Añadir un pequeño retraso antes de maximizar
        javafx.application.Platform.runLater(() -> {
            primaryStage.setMaximized(true);
            primaryStage.centerOnScreen();
        });
    }

    public static void configurarVentanaModal(Stage stage) {
        stage.show();
        // Añadir un pequeño retraso antes de maximizar
        javafx.application.Platform.runLater(() -> {
            stage.setMaximized(true);
            stage.centerOnScreen();
        });
    }

    public static void setMascotaModificar(Mascota mascota) {
        mascotaModificar = mascota;
    }

    public static Mascota getMascotaModificar() {
        return mascotaModificar;
    }

    public static void setServicioModificar(Servicio servicio) {
        servicioModificar = servicio;
    }

    public static Servicio getServicioModificar() {
        return servicioModificar;
    }

    public static void mostrarMensaje(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

}