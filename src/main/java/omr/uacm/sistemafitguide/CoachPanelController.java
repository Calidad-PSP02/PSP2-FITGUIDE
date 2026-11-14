package omr.uacm.sistemafitguide;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador del Panel del Coach.
 * Incluye rotación de consejos y edición persistente en base de datos.
 */
public class CoachPanelController implements Initializable {

    public static String NombreCoach   = "Coach";
    public static int    IdCoachActual = -1;

    @FXML private Label  LblTituloCoach;
    @FXML private Button BtnCerrarSesion;
    @FXML private Label  LblNumEjercicios;
    @FXML private Label  LblNumUsuarios;
    @FXML private Label  LblNumSesiones;
    @FXML private Label  LblConsejoDia;
    @FXML private Button BtnIrEjercicios;
    @FXML private Button BtnEditarConsejo;
    @FXML private HBox   BtnAgregarEjercicios;
    @FXML private HBox   BtnRevisarUsuarios;
    @FXML private HBox   BtnVerEstadisticas;

    private final List<String> CONSEJOS = new ArrayList<>();
    private int indiceConsejoActual = 0;
    private Timeline timelineConsejos;
    private boolean rotacionPausada = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (LblTituloCoach != null) {
            LblTituloCoach.setText("¡Hola, " + NombreCoach + "!");
        }
        cargarStats();
        cargarConsejosDesdeBD();
        iniciarRotacionConsejos();
        configurarHoverBotones();
    }

    // =========================================================================
    //  EDITAR CONSEJO (con persistencia)
    // =========================================================================
    @FXML
    private void HandleEditarConsejo() {
        if (timelineConsejos != null) {
            timelineConsejos.stop();
            rotacionPausada = true;
        }

        TextArea textArea = new TextArea(LblConsejoDia.getText());
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        textArea.setPrefColumnCount(42);
        textArea.setStyle("-fx-font-size: 13px;");

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Editar Consejo del Día");
        dialog.setHeaderText("Modifica el consejo que se muestra");
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().setPrefWidth(480);

        ButtonType btnGuardar  = new ButtonType("Guardar",  ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        dialog.setResultConverter(button -> button == btnGuardar ? textArea.getText().trim() : null);

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(nuevoTexto -> {
            if (!nuevoTexto.isEmpty()) {
                LblConsejoDia.setText(nuevoTexto);

                if (indiceConsejoActual >= 0 && indiceConsejoActual < CONSEJOS.size()) {
                    CONSEJOS.set(indiceConsejoActual, nuevoTexto);
                }
                guardarConsejoEnBD(indiceConsejoActual, nuevoTexto);
            }
        });
    }

    // =========================================================================
    //  PERSISTENCIA DE CONSEJOS
    // =========================================================================
    private void cargarConsejosDesdeBD() {
        CONSEJOS.clear();
        String sql = "SELECT texto FROM consejos ORDER BY id";

        try (Connection con = abrirConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                CONSEJOS.add(rs.getString("texto"));
            }
        } catch (SQLException e) {
            System.err.println("[CoachPanel] Error cargando consejos: " + e.getMessage());
        }

        if (CONSEJOS.isEmpty()) {
            insertarConsejosPorDefecto();
        }
    }

    private void insertarConsejosPorDefecto() {
        List<String> defaults = List.of(
            "\"El progreso no siempre se trata de hacer más, sino de ser más constante.\"",
            "\"Cada repetición que completas hoy es una inversión en quien serás mañana.\"",
            "\"La disciplina vence al talento cuando el talento no se está esforzando.\"",
            "\"Bebe al menos 500 ml de agua antes de entrenar y 250 ml cada 20 minutos.\"",
            "\"Exhala al hacer el esfuerzo, inhala al relajar. Nunca contengas el aliento.\"",
            "\"No necesitas ser perfecto, solo constante. Cada esfuerzo cuenta.\"",
            "\"5 minutos de calentamiento reducen el riesgo de lesión en un 50%.\""
        );

        String sql = "INSERT INTO consejos (texto) VALUES (?)";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (String texto : defaults) {
                ps.setString(1, texto);
                ps.executeUpdate();
                CONSEJOS.add(texto);
            }
        } catch (SQLException e) {
            System.err.println("[CoachPanel] Error insertando consejos por defecto: " + e.getMessage());
            CONSEJOS.addAll(defaults);
        }
    }

    private void guardarConsejoEnBD(int indice, String nuevoTexto) {
        String sql = "UPDATE consejos SET texto = ? WHERE id = ?";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoTexto);
            ps.setInt(2, indice + 1);
            int filas = ps.executeUpdate();

            if (filas == 0) {
                try (PreparedStatement psIns = con.prepareStatement(
                        "INSERT INTO consejos (texto) VALUES (?)")) {
                    psIns.setString(1, nuevoTexto);
                    psIns.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("[CoachPanel] Error guardando consejo: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo guardar el consejo en la base de datos.");
        }
    }

    // =========================================================================
    //  STATS
    // =========================================================================
    private void cargarStats() {
        try (Connection con = abrirConexion()) {
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM historial_ejercicios")) {
                if (rs.next() && LblNumEjercicios != null)
                    LblNumEjercicios.setText(String.valueOf(rs.getInt(1)));
            } catch (SQLException ignored) {}

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
            if (LblNumEjercicios != null) LblNumEjercicios.setText("—");
            if (LblNumUsuarios   != null) LblNumUsuarios.setText("—");
            if (LblNumSesiones  != null) LblNumSesiones.setText("—");
        }
    }

    // =========================================================================
    //  ROTACIÓN
    // =========================================================================
    private void iniciarRotacionConsejos() {
        if (CONSEJOS.isEmpty()) return;
        actualizarConsejoConFade(CONSEJOS.get(0));

        timelineConsejos = new Timeline(
            new KeyFrame(Duration.seconds(10), e -> {
                if (rotacionPausada || CONSEJOS.isEmpty()) return;
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

    // =========================================================================
    //  HOVER + NAVEGACIÓN
    // =========================================================================
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
        String hover = base.replace("-fx-background-color: #2a7a6a;", "-fx-background-color: #1f6055;");
        boton.setOnMouseEntered(e -> boton.setStyle(hover));
        boton.setOnMouseExited(e  -> boton.setStyle(base));
    }

    @FXML private void HandleIrEjercicios()      { detenerTimeline(); navegarA("PantallaEjercicios"); }
    @FXML private void HandleAgregarEjercicios() { detenerTimeline(); navegarA("PantallaAgregarEjercicios"); }
    @FXML private void HandleRevisarUsuarios()    { detenerTimeline(); navegarA("PantallaUsuarios"); }
    @FXML private void HandleVerEstadisticas()   { detenerTimeline(); navegarA("PantallaEstadisticas"); }

    @FXML private void HandleCerrarSesion() {
        detenerTimeline();
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
            URL fxmlUrl = getClass().getResource("/omr/uacm/sistemafitguide/" + fxmlName + ".fxml");
            if (fxmlUrl == null) fxmlUrl = getClass().getResource("/" + fxmlName + ".fxml");
            if (fxmlUrl == null) {
                mostrarAlerta("Error de navegación", "No se encontró el archivo: " + fxmlName + ".fxml");
                return;
            }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = obtenerStage();
            if (stage != null) stage.getScene().setRoot(root);
        } catch (java.io.IOException e) {
            System.err.println("[CoachPanel] Error navegando a: " + fxmlName + " → " + e.getMessage());
            mostrarAlerta("Error de navegación", "No se pudo cargar: " + fxmlName);
        }
    }

    private javafx.stage.Stage obtenerStage() {
        javafx.scene.Node[] nodos = { LblTituloCoach, LblNumEjercicios, LblNumUsuarios, LblConsejoDia, BtnCerrarSesion };
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
}
