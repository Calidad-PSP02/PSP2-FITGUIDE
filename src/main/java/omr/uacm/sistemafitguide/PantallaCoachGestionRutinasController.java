package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controlador para la gestión y edición de rutinas de los usuarios (Rol Coach).
 * Implementa el ajuste de progresión (incrementar series, modificar repeticiones,
 * peso extra y tiempos de descanso).
 *
 * @author Coach Module — FitGuide
 */
public class PantallaCoachGestionRutinasController implements Initializable {

    // ── Tabla Usuarios ────────────────────────────────────────────────────────
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Number> colUsuarioId;
    @FXML private TableColumn<Usuario, String> colUsuarioNombre;
    @FXML private TableColumn<Usuario, Number> colUsuarioPeso;
    @FXML private TableColumn<Usuario, Number> colUsuarioAltura;
    @FXML private TableColumn<Usuario, String> colUsuarioGenero;

    // ── Tabla Rutinas ─────────────────────────────────────────────────────────
    @FXML private TableView<Rutina> tablaRutinas;
    @FXML private TableColumn<Rutina, String> colRutinaGrupo;
    @FXML private TableColumn<Rutina, String> colRutinaNivel;
    @FXML private TableColumn<Rutina, String> colRutinaTipo;
    @FXML private TableColumn<Rutina, Number> colRutinaSeries;
    @FXML private TableColumn<Rutina, Number> colRutinaDescanso;
    @FXML private TableColumn<Rutina, Number> colRutinaPeso;

    // ── Tabla Detalle Repeticiones ────────────────────────────────────────────
    @FXML private TableView<DetalleEjercicioItem> tablaDetalle;
    @FXML private TableColumn<DetalleEjercicioItem, String> colDetalleNombre;
    @FXML private TableColumn<DetalleEjercicioItem, Number> colDetalleReps;

    @FXML private Label lblUsuarioSeleccionado;
    @FXML private Label lblRutinaSeleccionada;
    @FXML private Button btnEditarProgresion;

    private final CoachRutinaDao dao = new CoachRutinaDao();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private final ObservableList<Rutina> listaRutinas = FXCollections.observableArrayList();
    private final ObservableList<DetalleEjercicioItem> listaDetalle = FXCollections.observableArrayList();

    private Usuario usuarioActual = null;
    private Rutina rutinaActual = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ConfigurarTablaUsuarios();
        ConfigurarTablaRutinas();
        ConfigurarTablaDetalle();

        btnEditarProgresion.setDisable(true);

        CargarUsuarios();

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            usuarioActual = newVal;
            if (newVal != null) {
                lblUsuarioSeleccionado.setText("Usuario: " + newVal.getNombreUsuario());
                CargarRutinasDeUsuario(newVal.getIdUsuario());
            } else {
                lblUsuarioSeleccionado.setText("Selecciona un usuario");
                listaRutinas.clear();
                listaDetalle.clear();
            }
            btnEditarProgresion.setDisable(true);
        });

        tablaRutinas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            rutinaActual = newVal;
            if (newVal != null) {
                lblRutinaSeleccionada.setText("Rutina: " + newVal.getGrupoMuscular() + " · " + newVal.getNivel());
                CargarDetalleEjercicios(newVal);
                btnEditarProgresion.setDisable(false);
            } else {
                lblRutinaSeleccionada.setText("Selecciona una rutina");
                listaDetalle.clear();
                btnEditarProgresion.setDisable(true);
            }
        });
    }

    // ── Configuración de Tablas ──────────────────────────────────────────────

    private void ConfigurarTablaUsuarios() {
        colUsuarioId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdUsuario()));
        colUsuarioNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreUsuario()));
        colUsuarioPeso.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getPeso()));
        colUsuarioAltura.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getAltura()));
        colUsuarioGenero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenero()));
        tablaUsuarios.setItems(listaUsuarios);
    }

    private void ConfigurarTablaRutinas() {
        colRutinaGrupo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrupoMuscular()));
        colRutinaNivel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNivel()));
        colRutinaTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoPeso()));
        colRutinaSeries.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSeries()));
        colRutinaDescanso.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTiempoDescanso()));
        colRutinaPeso.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getPesoExtraKg()));
        tablaRutinas.setItems(listaRutinas);
    }

    private void ConfigurarTablaDetalle() {
        colDetalleNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colDetalleReps.setCellValueFactory(c -> c.getValue().repeticionesProperty());
        tablaDetalle.setItems(listaDetalle);
    }

    // ── Carga de Datos ───────────────────────────────────────────────────────

    public void CargarUsuarios() {
        listaUsuarios.clear();
        listaUsuarios.addAll(dao.ObtenerUsuarios());
    }

    public void CargarRutinasDeUsuario(int idUsuario) {
        listaRutinas.clear();
        listaDetalle.clear();
        listaRutinas.addAll(dao.ObtenerRutinasDeUsuario(idUsuario));
    }

    private void CargarDetalleEjercicios(Rutina rutina) {
        listaDetalle.clear();
        if (rutina.getMapaRepeticiones() != null) {
            for (Map.Entry<String, Integer> entry : rutina.getMapaRepeticiones().entrySet()) {
                listaDetalle.add(new DetalleEjercicioItem(entry.getKey(), entry.getValue()));
            }
        }
    }

    // ── Edición para Entrenamiento Progresivo ────────────────────────────────

    @FXML
    public void EditarRutinaProgresion(ActionEvent event) {
        if (usuarioActual == null || rutinaActual == null) {
            MostrarAlerta("Atención", "Selecciona primero un usuario y una rutina de las tablas.", Alert.AlertType.WARNING);
            return;
        }

        try {
            PantallaCoachEditarRutinaController.usuarioSeleccionado = usuarioActual;
            PantallaCoachEditarRutinaController.rutinaAEditar = rutinaActual;
            App.setRoot("PantallaCoachEditarRutina");
        } catch (IOException e) {
            MostrarAlerta("Error de Pantalla", "No se pudo abrir la pantalla de edición de rutina: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void RegresarMenu(ActionEvent event) throws IOException {
        App.setRoot("CoachPanel");
    }

    // ── Alias camelCase para compatibilidad con ambos estilos ────────────────
    public void cargarUsuarios() { CargarUsuarios(); }
    public void cargarRutinasDeUsuario(int idUsuario) { CargarRutinasDeUsuario(idUsuario); }
    @FXML public void editarRutinaProgresion(ActionEvent event) { EditarRutinaProgresion(event); }
    @FXML public void regresarMenu(ActionEvent event) throws IOException { RegresarMenu(event); }

    private void MostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ── Clase Auxiliar para Mostrar Detalle de Ejercicios en Tabla ────────────

    public static class DetalleEjercicioItem {
        private final SimpleStringProperty nombre;
        private final SimpleIntegerProperty repeticiones;

        public DetalleEjercicioItem(String nombre, int repeticiones) {
            this.nombre = new SimpleStringProperty(nombre);
            this.repeticiones = new SimpleIntegerProperty(repeticiones);
        }

        public String getNombre() { return nombre.get(); }
        public String GetNombre() { return getNombre(); }
        public SimpleStringProperty nombreProperty() { return nombre; }
        public int getRepeticiones() { return repeticiones.get(); }
        public int GetRepeticiones() { return getRepeticiones(); }
        public SimpleIntegerProperty repeticionesProperty() { return repeticiones; }
    }
}
