package com.example.petcaremanagerproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private static Scene scene;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        // Guardar el escenario principal
        primaryStage = stage;

        scene = new Scene(loadFXML("login"));
        stage.setTitle("PetCare Manager");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setRoot(String fxml) throws IOException {
        try {
            scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
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

    public static Stage getStage() {
        return primaryStage;
    }

    public static void mostrarMensaje(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

}