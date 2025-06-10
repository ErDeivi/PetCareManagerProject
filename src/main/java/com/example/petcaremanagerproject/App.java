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

/**
 * Clase principal de la aplicación PetCare Manager.
 * Gestiona la inicialización y configuración de la interfaz gráfica JavaFX,
 * así como el manejo de escenas y mensajes al usuario.
 */
public class App extends Application {

    /** Escenario principal de la aplicación */
    private static Stage primaryStage;
    /** Mascota seleccionada para modificar */
    private static Mascota mascotaModificar;
    /** Servicio seleccionado para modificar */
    private static Servicio servicioModificar;
    /** Escena actual de la aplicación */
    private static Scene scene;
    /** Escenario de la aplicación */
    private static Stage stage;

    /**
     * Inicializa la aplicación y configura la ventana principal.
     * @param stage Escenario principal de la aplicación
     * @throws IOException si ocurre un error al cargar el archivo FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        scene = new Scene(loadFXML("login"));
        stage.setTitle("PetCare Manager");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Punto de entrada principal de la aplicación.
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Cambia la escena actual de la aplicación.
     * @param fxml Nombre del archivo FXML a cargar
     * @throws IOException si ocurre un error al cargar el archivo FXML
     */
    public static void setRoot(String fxml) throws IOException {
        try {
            scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga un archivo FXML y devuelve su contenido como Parent.
     * @param fxml Nombre del archivo FXML a cargar
     * @return Contenido del archivo FXML como Parent
     * @throws IOException si ocurre un error al cargar el archivo FXML
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Establece la mascota seleccionada para modificar.
     * @param mascota Mascota a modificar
     */
    public static void setMascotaModificar(Mascota mascota) {
        mascotaModificar = mascota;
    }

    /**
     * Obtiene la mascota seleccionada para modificar.
     * @return Mascota seleccionada
     */
    public static Mascota getMascotaModificar() {
        return mascotaModificar;
    }

    /**
     * Establece el servicio seleccionado para modificar.
     * @param servicio Servicio a modificar
     */
    public static void setServicioModificar(Servicio servicio) {
        servicioModificar = servicio;
    }

    /**
     * Obtiene el servicio seleccionado para modificar.
     * @return Servicio seleccionado
     */
    public static Servicio getServicioModificar() {
        return servicioModificar;
    }

    /**
     * Obtiene el escenario principal de la aplicación.
     * @return Escenario principal
     */
    public static Stage getStage() {
        return primaryStage;
    }

    /**
     * Muestra un mensaje de información al usuario.
     * @param titulo Título del mensaje
     * @param header Encabezado del mensaje
     * @param contenido Contenido del mensaje
     */
    public static void mostrarMensaje(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}