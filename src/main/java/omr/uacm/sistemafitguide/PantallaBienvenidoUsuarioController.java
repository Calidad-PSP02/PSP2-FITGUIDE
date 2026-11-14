/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author MiguelDiaz
 */

public class PantallaBienvenidoUsuarioController implements Initializable {

    @FXML private Button btnEmpezar;
    @FXML private Button btnCerrarSesion;
    @FXML private VBox panelNotificacion;
    @FXML private Label lblNotificacionTitulo;
    @FXML private Label lblNotificacionMensaje;
    @FXML private GridPane gridCalendario;
    @FXML private Label lblRachaTexto;

    // Conectores de datos
    private final HistorialRutinaDAO historialDAO = new HistorialRutinaDAO();

    // VARIABLE CLAVE PARA LA EXCEPCIÓN
    public static boolean rutinaConfigurada = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        generarCalendarioDinamico();
    }

    @FXML
    private void accionEmpezarRutina() {
        try {
            if (!rutinaConfigurada) {
                throw new RutinaNoConfiguradaException("No puedes iniciar el entrenamiento. ¡Primero debes ir a 'PERSONALIZAR RUTINA' para personalizar tus ejercicios!");
            }
            App.setRoot("PantallaListaEjercicios");

        } catch (RutinaNoConfiguradaException e) {
            mostrarAlertaError("Rutina Inexistente", e.getMessage());
        } catch (IOException e) {
            mostrarAlertaError("Error de Navegación", "No se pudo cargar la pantalla de ejercicios.");
        }
    }

    @FXML
    private void accionCerrarSesion(ActionEvent event) throws IOException {
        Sesion.cerrarSesion(); // Limpiamos las credenciales globales al salir
        App.setRoot("PantallaInicio");
    }

    // consejos de motivacion y salud
    @FXML public void mostrarNotificacionDisciplina() {
        mostrarTarjeta("La disciplina", "La disciplina vence al talento cuando el talento no se está esforzando.");
    }

    @FXML public void mostrarNotificacionRindas() {
        mostrarTarjeta("No te rindas", "Vas muy bien, sigue asi y veras tus grandes resultados.");
    }

    @FXML public void mostrarNotificacionHoyCuenta() {
        mostrarTarjeta("¡Hoy y todos los días cuenta!", "Todos tus esfuerzos diarios sin importar que tan grande sean cuentan.");
    }

    @FXML public void mostrarNotificacionMaximo() {
        mostrarTarjeta("¡Da tu máximo siempre!", "Recuerda todos los dias siempre puedes dar todo de ti. ");
    }

    @FXML public void mostrarNotificacionHidratate() {
        mostrarTarjeta("¡A tomar agua!", "Mantente hidratado durante todo el día, ayuda a la recuperación muscular y a tu nivel de energía.");
    }

    @FXML public void mostrarNotificacionDolor() {
        mostrarTarjeta("¡Cuidado con los dolores!", "Es normal sentir un poco de dolor al realizar ejercicio pero si el dolor es grande o punzante acude a un doctor.");
    }

    @FXML public void mostrarNotificacionRespiracion() {
        mostrarTarjeta("¡Siempre respira hondo!", "Durante toda tu rutina procura siempre respirar ondo para tener mejor concentración.");
    }

    @FXML public void mostrarNotificacionPostura() {
        mostrarTarjeta("¡TU postura importa!", "Procura tener siempre una postura recta al realizar cualquier actividad y más si es fisica.");
    }

    @FXML
    private void accionConfigurarRutina() {
        try {
            App.setRoot("PantallaConfiguracionRutina");
        } catch (IOException e) {
            System.err.println("Fallo al intentar cargar configuración de rutina: " + e.getMessage());
        }
    }

    private void mostrarTarjeta(String titulo, String mensaje) {
        lblNotificacionTitulo.setText(titulo);
        lblNotificacionMensaje.setText(mensaje);
        panelNotificacion.setVisible(true);
    }

    @FXML
    public void accionCerrarNotificacion() {
        panelNotificacion.setVisible(false);
    }

    // logica para el calendario dinamico
    private void generarCalendarioDinamico() {
        gridCalendario.getChildren().clear();

        int idUsuario = Sesion.idUsuarioActivo;

        // obtenemos la racha real y los dias entrenados desde la base de datos
        int racha = historialDAO.calcularRachaActual(idUsuario);
        Set<LocalDate> fechasEntrenadas = historialDAO.obtenerFechasEntrenamiento(idUsuario);

        // actualizamos las rachas en la base
        if (racha == 1) {
            lblRachaTexto.setText(racha + " DÍA DE RACHA");
        } else {
            lblRachaTexto.setText(racha + " DÍAS DE RACHA");
        }

        LocalDate fechaActual = LocalDate.now();
        YearMonth mesActual = YearMonth.from(fechaActual);
        int diasEnElMes = mesActual.lengthOfMonth();

        LocalDate primerDiaDelMes = mesActual.atDay(1);
        int columnaInicial = primerDiaDelMes.getDayOfWeek().getValue() - 1;

        int filaActual = 1;
        int columnaActual = columnaInicial;

        for (int dia = 1; dia <= diasEnElMes; dia++) {
            LocalDate fechaCelda = mesActual.atDay(dia);

            Label lblDia = new Label(String.valueOf(dia));
            lblDia.setAlignment(Pos.CENTER);
            lblDia.setPrefSize(28, 28);
            lblDia.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 12));

            // evaluar si es el dia de hoy
            if (fechaCelda.equals(fechaActual)) {
                if (fechasEntrenadas.contains(fechaCelda)) {
                    // ya entreno hoy (verde con borde naranja)
                    lblDia.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 50%; -fx-border-color: #FF9800; -fx-border-radius: 50%; -fx-border-width: 2;");
                } else {
                    //no ha antrenado (amarillo de advertencia/atencion con borde verde)
                    lblDia.setStyle("-fx-background-color: #FFEB3B; -fx-text-fill: #333333; -fx-background-radius: 50%; -fx-border-color: #4CAF50; -fx-border-radius: 50%; -fx-border-width: 2;");
                }
            }
            //si es un dia pasado y el usuario entreno (verde oscuro completo)
            else if (fechasEntrenadas.contains(fechaCelda)) {
                lblDia.setStyle("-fx-background-color: #2D5A27; -fx-text-fill: white; -fx-background-radius: 5;");
            }
            // Ssi es un dia pasado y no entreno (gris apagado)
            else if (fechaCelda.isBefore(fechaActual)) {
                lblDia.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #9E9E9E; -fx-background-radius: 5;");
            }
            // dias futuros (transparente )
            else {
                lblDia.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333;");
            }

            gridCalendario.add(lblDia, columnaActual, filaActual);

            columnaActual++;
            if (columnaActual > 6) {
                columnaActual = 0;
                filaActual++;
            }
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}