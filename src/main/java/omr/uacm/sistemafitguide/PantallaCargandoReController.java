/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author MiguelDiaz
 */
public class PantallaCargandoReController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Temporizador de 3 segundos exactos
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));

        delay.setOnFinished(event -> {
            try {
                // Terminan los 3 segundos y salta a la Bienvenida
                App.setRoot("PantallaBienvenidoUsuario");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        delay.play();
    }

}