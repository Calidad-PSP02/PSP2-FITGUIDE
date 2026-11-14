/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package omr.uacm.sistemafitguide;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author MiguelDiaz
 */
public class PantallaDescansoController implements Initializable {

    @FXML private Label lblTiempo;
    @FXML private Circle circuloProgreso;
    @FXML private ProgressBar barraProgreso;
    @FXML private Label lblMensaje;
    @FXML private Button btnPausar;
    @FXML private Button btnSaltar;

    @FXML private ImageView imgSiguiente;
    @FXML private Label lblSiguienteNombre;

    @FXML private Label lblSeries;
    @FXML private Label lblReps;
    @FXML private Label lblProgresoTexto;
    @FXML private ProgressBar barraRutina;


    public static int tiempoDescansoPersonalizado = 35;
    private int tiempoTotalDescanso;
    private int tiempoRestante;

    private Timeline timeline;
    private boolean estaPausado = false;
    private double circunferencia;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tiempoTotalDescanso = GestionRutina.getInstance().getTiempoDescansoConfigurado();

        tiempoRestante = tiempoTotalDescanso;

        if (circuloProgreso != null) {
            circunferencia = 2 * Math.PI * circuloProgreso.getRadius();
            circuloProgreso.getStrokeDashArray().setAll(circunferencia, circunferencia);
        }

        if (lblTiempo != null) {
            lblTiempo.setText(String.valueOf(tiempoRestante));
        }

        cargarPreviewSiguienteEjercicio();
        actualizarProgresoRutina();
        iniciarCronometro();
    }

    private void iniciarCronometro() {
        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoRestante--;
            GestionRutina.getInstance().sumarSegundo();

            if (lblTiempo != null) lblTiempo.setText(String.valueOf(tiempoRestante));

            double proporcion = (double) tiempoRestante / tiempoTotalDescanso;

            if (circuloProgreso != null) {
                circuloProgreso.setStrokeDashOffset(-circunferencia * (1 - proporcion));
            }

            if (barraProgreso != null) barraProgreso.setProgress(proporcion);

            if (tiempoRestante <= 0) {
                timeline.stop();
                irASiguienteEjercicio();
            }
        }));

        timeline.setCycleCount(tiempoTotalDescanso);
        timeline.play();
    }

    private void cargarPreviewSiguienteEjercicio() {
        GestionRutina gestion = GestionRutina.getInstance();


        if (lblSeries != null) {
            lblSeries.setText(String.valueOf(gestion.getSeriesTotales()));
        }

        Ejercicio ejSiguiente = gestion.getEjercicioActualData();

        if (ejSiguiente != null) {
            String nombreEj = ejSiguiente.getNombre();
            if (lblSiguienteNombre != null) lblSiguienteNombre.setText(nombreEj);

            // repeticiones del siguiente ejercicio
            if (lblReps != null) {
                String llaveCompuesta = gestion.getLlaveRutina();
                Rutina rutinaActiva = PantallaConfiguracionRutinaController.mapaRutinas.get(llaveCompuesta);
                boolean repeticionAsignada = false;

                if (rutinaActiva != null && rutinaActiva.getMapaRepeticiones().containsKey(nombreEj)) {
                    lblReps.setText(String.valueOf(rutinaActiva.getMapaRepeticiones().get(nombreEj)));
                    repeticionAsignada = true;
                }
                if (!repeticionAsignada) {
                    lblReps.setText(String.valueOf(ejSiguiente.getRepeticionesObjetivo()));
                }
            }

            // imagen del siguiente ejercicio
            String rutaImagen = ejSiguiente.getRutaImagen();
            try {
                URL resource = null;
                if (rutaImagen != null && !rutaImagen.trim().isEmpty()) {
                    if (!rutaImagen.startsWith("/")) rutaImagen = "/" + rutaImagen;
                    resource = getClass().getResource(rutaImagen);
                }
                if (resource == null) {
                    resource = getClass().getResource("/omr/uacm/sistemafitguide/imagenes/Logo ajolote.png");
                }
                if (resource != null && imgSiguiente != null) {
                    imgSiguiente.setImage(new Image(resource.toExternalForm()));
                    Rectangle clip = new Rectangle(260.0, 200.0);
                    clip.setArcWidth(30);
                    clip.setArcHeight(30);
                    imgSiguiente.setClip(clip);
                }
            } catch (Exception ex) {
                System.err.println("Error carga de imagen: " + ex.getMessage());
            }

        } else {
            if (lblSiguienteNombre != null) lblSiguienteNombre.setText("¡Último esfuerzo!");
            if (imgSiguiente != null) imgSiguiente.setImage(null);
            if (lblReps != null) lblReps.setText("-");
        }
    }

    private void actualizarProgresoRutina() {
        GestionRutina gestion = GestionRutina.getInstance();
        int actual = gestion.getEjercicioActual();
        int total = gestion.getTotalEjercicios();

        if (lblProgresoTexto != null) lblProgresoTexto.setText(actual + " / " + total);
        if (barraRutina != null && total > 0) barraRutina.setProgress((double) actual / total);
    }

    @FXML
    public void pausarReanudar() {
        if (timeline == null) return;
        if (estaPausado) {
            timeline.play();
            estaPausado = false;
        } else {
            timeline.pause();
            estaPausado = true;
        }
    }

    @FXML
    public void saltarDescanso() {
        if (timeline != null) {
            // contramos los segundos que pasan antes de saltar
            int segundosReales = tiempoTotalDescanso - tiempoRestante;
            timeline.stop();
        }
        irASiguienteEjercicio();
    }

    private void irASiguienteEjercicio() {
        try {
            App.setRoot("PantallaEjecucion");
        } catch (IOException e) {
            System.err.println("Error al cargar Ejecucion: " + e.getMessage());
        }
    }
}
