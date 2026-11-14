package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Set;
import java.time.LocalDate;
import java.time.YearMonth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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

    // ===== IMC =====
    @FXML private Label lblIMCValor;
    @FXML private Label lblIMCClasificacion;

    // Rangos en los que un dato se considera real. Fuera de ellos no se calcula el IMC.
    private static final double PESO_MIN_KG   = 10.0;
    private static final double PESO_MAX_KG   = 400.0;
    private static final double ALTURA_MIN_M  = 0.5;
    private static final double ALTURA_MAX_M  = 2.8;
    private static final double IMC_MIN        = 8.0;
    private static final double IMC_MAX        = 80.0;

    // Conectores de datos
    private final HistorialRutinaDAO historialDAO = new HistorialRutinaDAO();

    // VARIABLE CLAVE PARA LA EXCEPCIÓN
    public static boolean rutinaConfigurada = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        generarCalendarioDinamico();
        calcularYMostrarIMC();          // ← Calcula y muestra el IMC al entrar
    }

    // =========================================================================
    //  CÁLCULO Y VISUALIZACIÓN DEL IMC
    // =========================================================================
    private void calcularYMostrarIMC() {
        int idUsuario = Sesion.idUsuarioActivo;

        if (idUsuario <= 0) {
            mostrarIMCSinDatos("Sin sesión");
            return;
        }

        String sql = "SELECT peso, altura FROM usuarios WHERE id_usuario = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double peso   = rs.getDouble("peso");     // kg
                double altura = rs.getDouble("altura");   // metros (1.75) o centímetros (175)

                if (peso <= 0 || altura <= 0) {
                    mostrarIMCSinDatos("Datos incompletos");
                    return;
                }

                // Acepta la altura en metros o en centímetros (antes se asumía solo cm)
                double alturaMetros = alturaAMetros(altura);

                if (peso < PESO_MIN_KG || peso > PESO_MAX_KG || alturaMetros < 0) {
                    System.err.println("[IMC] Datos fuera de rango: peso=" + peso
                            + " altura=" + altura);
                    mostrarIMCSinDatos("Datos fuera de rango");
                    return;
                }

                double imc = calcularIMC(peso, alturaMetros);

                // Red de seguridad: nunca mostrar un IMC imposible
                if (imc < IMC_MIN || imc > IMC_MAX) {
                    System.err.println("[IMC] Resultado imposible (" + imc + ") con peso="
                            + peso + " altura=" + altura);
                    mostrarIMCSinDatos("Datos fuera de rango");
                    return;
                }

                actualizarEtiquetasIMC(imc);

            } else {
                mostrarIMCSinDatos("Usuario no encontrado");
            }

        } catch (SQLException e) {
            System.err.println("[IMC] Error al consultar datos: " + e.getMessage());
            mostrarIMCSinDatos("Error de conexión");
        }
    }

    /**
     * Convierte la altura guardada a metros, sea cual sea la unidad en que se registró.
     *   0.5 – 2.8   → ya está en metros   (ej. 1.75)
     *   50  – 280   → está en centímetros (ej. 175)
     *
     * @return la altura en metros, o -1 si el valor no es una altura humana posible
     */
    static double alturaAMetros(double altura) {
        if (altura >= ALTURA_MIN_M && altura <= ALTURA_MAX_M) {
            return altura;
        }
        if (altura >= ALTURA_MIN_M * 100 && altura <= ALTURA_MAX_M * 100) {
            return altura / 100.0;
        }
        return -1;
    }

    /** IMC = peso (kg) / altura² (m). */
    static double calcularIMC(double pesoKg, double alturaMetros) {
        return pesoKg / (alturaMetros * alturaMetros);
    }

    private void actualizarEtiquetasIMC(double imc) {
        String imcTexto = String.format("%.1f", imc);
        lblIMCValor.setText("IMC: " + imcTexto);

        String clasificacion;
        String colorFondo;
        String colorTexto = "white";

        if (imc < 18.5) {
            clasificacion = "Bajo peso";
            colorFondo = "#FFC107";       // Amarillo
            colorTexto = "#333333";
        } else if (imc < 25.0) {
            clasificacion = "Normal";
            colorFondo = "#4CAF50";       // Verde
        } else if (imc < 30.0) {
            clasificacion = "Sobrepeso";
            colorFondo = "#FFC107";       // Amarillo
            colorTexto = "#333333";
        } else {
            clasificacion = "Obesidad";
            colorFondo = "#E53935";       // Rojo
        }

        lblIMCClasificacion.setText(clasificacion);
        lblIMCClasificacion.setStyle(
            "-fx-background-color: " + colorFondo + ";" +
            "-fx-text-fill: " + colorTexto + ";" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 5 16;"
        );
    }

    private void mostrarIMCSinDatos(String mensaje) {
        lblIMCValor.setText("IMC: --");
        lblIMCClasificacion.setText(mensaje);
        lblIMCClasificacion.setStyle(
            "-fx-background-color: #9E9E9E;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 5 16;"
        );
    }

    // =========================================================================
    //  RESTO DE MÉTODOS (sin cambios funcionales)
    // =========================================================================
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
        Sesion.cerrarSesion();
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

        int racha = historialDAO.calcularRachaActual(idUsuario);
        Set<LocalDate> fechasEntrenadas = historialDAO.obtenerFechasEntrenamiento(idUsuario);

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

            if (fechaCelda.equals(fechaActual)) {
                if (fechasEntrenadas.contains(fechaCelda)) {
                    lblDia.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 50%; -fx-border-color: #FF9800; -fx-border-radius: 50%; -fx-border-width: 2;");
                } else {
                    lblDia.setStyle("-fx-background-color: #FFEB3B; -fx-text-fill: #333333; -fx-background-radius: 50%; -fx-border-color: #4CAF50; -fx-border-radius: 50%; -fx-border-width: 2;");
                }
            }
            else if (fechasEntrenadas.contains(fechaCelda)) {
                lblDia.setStyle("-fx-background-color: #2D5A27; -fx-text-fill: white; -fx-background-radius: 5;");
            }
            else if (fechaCelda.isBefore(fechaActual)) {
                lblDia.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #9E9E9E; -fx-background-radius: 5;");
            }
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
