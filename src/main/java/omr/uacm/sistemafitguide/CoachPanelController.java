/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
/**
 *
 * @author migue
 */


public class CoachPanelController implements Initializable {

    public static String NombreCoach     = "Coach";
    public static int    IdCoachActual   = -1;

    @FXML private Label  LblTituloCoach;
    @FXML private Button BtnCerrarSesion;

    @FXML private Label LblNumEjercicios;
    @FXML private Label LblNumUsuarios;
    @FXML private Label LblNumSesiones;

    @FXML private Label LblConsejoDia;

    @FXML private Button BtnIrEjercicios;

    @FXML private HBox BtnAgregarEjercicios;
    @FXML private HBox BtnRevisarUsuarios;
    @FXML private HBox BtnVerEstadisticas;

    private static final List<String> CONSEJOS = List.of(
            "\"El progreso no siempre se trata de hacer más, sino de ser más constante.\"",
            "\"Cada repetición que completas hoy es una inversión en quien serás mañana.\"",
            "\"La disciplina vence al talento cuando el talento no se está esforzando.\"",
            "\"Bebe al menos 500 ml de agua antes de entrenar y 250 ml cada 20 minutos.\"",
            "\"Exhala al hacer el esfuerzo, inhala al relajar. Nunca contengas el aliento.\"",
            "\"No necesitas ser perfecto, solo constante. Cada esfuerzo cuenta.\"",
            "\"5 minutos de calentamiento reducen el riesgo de lesión en un 50%.\""
    );
    private int indiceConsejoActual = 0;
    private Timeline timelineConsejos;

    public void initialize(URL url, ResourceBundle rb) {


        if (LblTituloCoach != null)
            LblTituloCoach.setText("¡Hola, " + NombreCoach + "!");
        cargarStats();
        iniciarRotacionConsejos();

        configurarHoverBotones();
    }
    private void cargarStats() {
        try (Connection con = abrirConexion()) {

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM ejercicios_catalogo")) {
                if (rs.next() && LblNumEjercicios != null)
                    LblNumEjercicios.setText(String.valueOf(rs.getInt(1)));
            } catch (SQLException e) {

            }

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios")) {
                if (rs.next() && LblNumUsuarios != null)
                    LblNumUsuarios.setText(String.valueOf(rs.getInt(1)));
            }


            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM historial_rutinas")) {
                if (rs.next() && LblNumSesiones != null)
                    LblNumSesiones.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (SQLException e) {
            System.err.println("[CoachPanel] Error cargando stats: " + e.getMessage());
            // Valores de respaldo para que no quede vacío
            if (LblNumEjercicios != null) LblNumEjercicios.setText("—");
            if (LblNumUsuarios   != null) LblNumUsuarios.setText("—");
            if (LblNumSesiones   != null) LblNumSesiones.setText("—");
        }
    }

    private void iniciarRotacionConsejos() {
        actualizarConsejoConFade(CONSEJOS.get(0));

        timelineConsejos = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> {
                    indiceConsejoActual = (indiceConsejoActual + 1) % CONSEJOS.size();
                    actualizarConsejoConFade(CONSEJOS.get(indiceConsejoActual));
                })
        );
        timelineConsejos.setCycleCount(Timeline.INDEFINITE);
        timelineConsejos.play();
    }

    private void actualizarConsejoConFade(String texto) {
        if (LblConsejoDia == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), LblConsejoDia);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            LblConsejoDia.setText(texto);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), LblConsejoDia);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }


    private void configurarHoverBotones() {
        configurarHoverHBox(BtnAgregarEjercicios);
        configurarHoverHBox(BtnRevisarUsuarios);
        configurarHoverHBox(BtnVerEstadisticas);
        configurarHoverButton(BtnIrEjercicios);
    }

    private void configurarHoverHBox(HBox boton) {
        if (boton == null) return;
        String base  = boton.getStyle();
        String hover = base + "-fx-background-color: #e4f0f0;";
        boton.setOnMouseEntered(e -> boton.setStyle(hover));
        boton.setOnMouseExited(e  -> boton.setStyle(base));
    }

    private void configurarHoverButton(Button boton) {
        if (boton == null) return;
        String base  = boton.getStyle();
        String hover = base.replace("-fx-background-color: #2a7a6a;",
                "-fx-background-color: #1f6055;");
        boton.setOnMouseEntered(e -> boton.setStyle(hover));
        boton.setOnMouseExited(e  -> boton.setStyle(base));
    }

    @FXML
    private void HandleIrEjercicios() {
        detenerTimeline();
        navegarA("PantallaCoachCatalogoEjercicios");
    }

    @FXML
    private void HandleAgregarEjercicios() {
        detenerTimeline();
        PantallaCoachFormularioEjercicioController.modoEdicion = false;
        PantallaCoachFormularioEjercicioController.ejercicioAEditar = null;
        navegarA("PantallaCoachFormularioEjercicio");
    }

    @FXML
    private void HandleRevisarUsuarios() {
        detenerTimeline();
        navegarA("PantallaCoachGestionRutinas");
    }

    @FXML
    private void HandleVerEstadisticas() {
        detenerTimeline();
        navegarA("PantallaEstadisticas");
    }

    @FXML
    private void HandleCerrarSesion() {
        detenerTimeline();
        Sesion.cerrarSesion();
        NombreCoach   = "Coach";
        IdCoachActual = -1;
        navegarA("PantallaInicio");
    }


    private Connection abrirConexion() throws SQLException {
        return ConexionDB.conectar();
    }

    private void detenerTimeline() {
        if (timelineConsejos != null) timelineConsejos.stop();
    }

    private void navegarA(String fxmlName) {
        try {
            // Intentar con el paquete de tu proyecto
            java.net.URL fxmlUrl = getClass().getResource(
                    "/omr/uacm/sistemafitguide/" + fxmlName + ".fxml");

            // Si no lo encuentra, buscar en el paquete raíz de resources
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/" + fxmlName + ".fxml");
            }

            if (fxmlUrl == null) {
                mostrarAlerta("Error de navegación",
                        "No se encontró el archivo: " + fxmlName + ".fxml\n" +
                                "Verifica que esté en resources/omr/uacm/sistemafitguide/");
                return;
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Parent root = loader.load();

            // Obtener el Stage desde cualquier nodo visible
            javafx.stage.Stage stage = obtenerStage();
            if (stage != null) {
                stage.getScene().setRoot(root);
            }

        } catch (java.io.IOException e) {
            System.err.println("[CoachPanel] Error navegando a: " + fxmlName + " → " + e.getMessage());
            mostrarAlerta("Error de navegación", "No se pudo cargar: " + fxmlName);
        }
    }

    private javafx.stage.Stage obtenerStage() {
        javafx.scene.Node[] nodos = {
                LblTituloCoach, LblNumEjercicios, LblNumUsuarios,
                LblConsejoDia,  BtnCerrarSesion
        };
        for (javafx.scene.Node nodo : nodos) {
            if (nodo != null && nodo.getScene() != null && nodo.getScene().getWindow() != null) {
                return (javafx.stage.Stage) nodo.getScene().getWindow();
            }
        }
        return null;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private LocalDate hoy() {
        return LocalDate.now();
    }
}