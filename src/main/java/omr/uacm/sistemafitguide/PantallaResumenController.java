/*
         * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
         * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
         */
package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/*
 * @author migue
 */

public class PantallaResumenController implements Initializable {

    @FXML private Button btnCerrarSesion;
    @FXML private Button btnInicio;
    @FXML private Button btnRegresar;
    @FXML private GridPane gridEjercicios;
    @FXML private Label lblTiempoTotal;



    public static List<HistorialEjercicio> ejerciciosCompletados = new ArrayList<>();
    public static int tiempoTotalSegundos = 0;

    // motores para el dao 
    private final HistorialRutinaDAO rutinaDAO = new HistorialRutinaDAO();
    private final HistorialEjercicioDAO ejercicioDAO = new HistorialEjercicioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // limpiamos los datos 
        gridEjercicios.getChildren().clear();
        gridEjercicios.getRowConstraints().clear();
        gridEjercicios.setVgap(12);
        gridEjercicios.setHgap(30);
        gridEjercicios.getChildren().clear();

        // formatear y mostrar el tiempo
        int minutos = tiempoTotalSegundos / 60;
        int segundos = tiempoTotalSegundos % 60;
        lblTiempoTotal.setText(String.format("Tiempo total: %02d:%02d min.!", minutos, segundos));

        int filaActual = 0;
        for (HistorialEjercicio ej : ejerciciosCompletados) {

            // Nombre del ejercicio
            Label lblNombre = new Label(ej.getNombreEjercicio() + ".");
            lblNombre.setTextFill(javafx.scene.paint.Color.WHITE);
            lblNombre.setFont(Font.font("System", FontWeight.BOLD, 20));

            //  repeticiones hechas
            Label lblReps = new Label(ej.getRepsReales() + " repeticiones.");
            lblReps.setTextFill(javafx.scene.paint.Color.WHITE);
            lblReps.setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC, 14));

            // inyectamos los componentes dinamicos en la cuadricula
            gridEjercicios.add(lblNombre, 0, filaActual);
            gridEjercicios.add(lblReps, 1, filaActual);

            filaActual++;
        }

        //guardamos la sesión automaticamente
        guardarEntrenamientoEnBaseDatos();
    }

    private void guardarEntrenamientoEnBaseDatos() {
        if (ejerciciosCompletados.isEmpty()) {
            System.out.println("Historial vacío. No se detectaron ejercicios para escribir en la BD.");
            return;
        }

        int idUsuarioLogueado = Sesion.idUsuarioActivo;
        String fechaHoy = LocalDate.now().toString();

        // generamos el objeto cabecera
        HistorialRutina cabecera = new HistorialRutina(idUsuarioLogueado, fechaHoy, tiempoTotalSegundos);

        System.out.println("Escribiendo registro en historial_rutinas...");
        int idHistorialGenerado = rutinaDAO.insertarHistorialRutina(cabecera);

        // Si la cabecera se inserto correctamente, unimos el desglose de ejercicios
        if (idHistorialGenerado > 0) {
            System.out.println("-> Éxito. ID de Historial asignado: " + idHistorialGenerado);

            for (HistorialEjercicio ej : ejerciciosCompletados) {
                ej.setIdHistorial(idHistorialGenerado); // vinculamos cada ejercicio a la rutina correspondiente
            }

            // Guardamos todos los ejercicios de un solo bloque
            ejercicioDAO.guardarListaEjercicios(ejerciciosCompletados);
            System.out.println("-> Desglose guardado. Persistencia completada al 100%.");
        } else {
            System.err.println("Error crítico: No se pudo persistir la rutina en la base de datos.");
        }
    }

    @FXML
    private void accionCerrarSesion(ActionEvent event) throws IOException {
        ejerciciosCompletados.clear();
        tiempoTotalSegundos = 0;
        Sesion.cerrarSesion();
        App.setRoot("PantallaInicio");
    }

    @FXML
    private void accionInicio(ActionEvent event) throws IOException {
        //limpiamos para el siguiente usuario
        ejerciciosCompletados.clear();
        tiempoTotalSegundos = 0;
        App.setRoot("PantallaBienvenidoUsuario");
    }

    @FXML
    private void accionRegresar(ActionEvent event) throws IOException {
        ejerciciosCompletados.clear();
        tiempoTotalSegundos = 0;
        App.setRoot("PantallaListaEjercicios");
    }
}
