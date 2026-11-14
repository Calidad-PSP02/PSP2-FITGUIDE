package omr.uacm.sistemafitguide;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controlador del menú principal del rol de Coach.
 * Permite navegar hacia el catálogo de ejercicios y hacia la gestión de rutinas.
 *
 * @author Coach Module — FitGuide
 */
public class PantallaCoachMenuController {

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblSubtitulo;

    @FXML
    public void IrACatalogoEjercicios(ActionEvent event) throws IOException {
        App.setRoot("PantallaCoachCatalogoEjercicios");
    }

    @FXML
    public void IrAGestionRutinas(ActionEvent event) throws IOException {
        App.setRoot("PantallaCoachGestionRutinas");
    }

    @FXML
    public void CerrarSesion(ActionEvent event) throws IOException {
        Sesion.cerrarSesion();
        App.setRoot("PantallaInicio");
    }

    @FXML
    public void IrAMenuUsuario(ActionEvent event) throws IOException {
        App.setRoot("PantallaBienvenidoUsuario");
    }

    // Alias camelCase para compatibilidad
    @FXML public void irACatalogoEjercicios(ActionEvent event) throws IOException { IrACatalogoEjercicios(event); }
    @FXML public void irAGestionRutinas(ActionEvent event) throws IOException { IrAGestionRutinas(event); }
    @FXML public void cerrarSesion(ActionEvent event) throws IOException { CerrarSesion(event); }
    @FXML public void irAMenuUsuario(ActionEvent event) throws IOException { IrAMenuUsuario(event); }
}