/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package omr.uacm.sistemafitguide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("PantallaInicio"), 900, 600);

        try {
            Image icon = new Image(App.class.getResourceAsStream("/omr/uacm/sistemafitguide/imagenes/logo.jpg"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("No se encontró el ícono principal.");
        }

        stage.setScene(scene);
        stage.setTitle("FitGuide");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        ConexionDB.inicializarBaseDatos(); //conecion a al base de datos

        launch();
    }
}