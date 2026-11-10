package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla completa para Agregar y Editar Ejercicios del Catálogo (Rol Coach).
 * Soporta AMBOS estilos de programación: PascalCase y camelCase.
 *
 * @author Coach Module — FitGuide
 */
public class PantallaCoachFormularioEjercicioController implements Initializable {

    // ── Variables Estáticas para Transferencia de Estado ─────────────────────
    public static EjercicioCoach ejercicioAEditar = null;
    public static boolean modoEdicion = false;

    // ── Componentes de la Interfaz (Notación Húngara) ────────────────────────
    @FXML private Label lblTituloPantalla;
    @FXML private Label lblSubtituloPantalla;

    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbZonaCorporal;
    @FXML private ComboBox<String> cmbNivel;
    @FXML private ComboBox<String> cmbTipoPeso;
    @FXML private TextField txtRepeticiones;
    @FXML private TextField txtSeries;
    @FXML private TextField txtDescanso;
    @FXML private TextField txtDuracion;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtUrlImagen;
    @FXML private TextField txtUrlVideo;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final EjercicioCatalogoDao dao = new EjercicioCatalogoDao();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        InicializarCombos();

        if (modoEdicion && ejercicioAEditar != null) {
            ConfigurarModoEdicion();
        } else {
            ConfigurarModoCreacion();
        }

        // Filtros para asegurar solo números en campos numéricos
        txtRepeticiones.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtRepeticiones.setText(newV.replaceAll("[^\\d]", ""));
        });
        txtSeries.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtSeries.setText(newV.replaceAll("[^\\d]", ""));
        });
        txtDescanso.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtDescanso.setText(newV.replaceAll("[^\\d]", ""));
        });
        txtDuracion.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtDuracion.setText(newV.replaceAll("[^\\d]", ""));
        });
    }

    private void InicializarCombos() {
        cmbZonaCorporal.getItems().addAll("Brazo", "Abdomen", "Pierna");
        cmbZonaCorporal.setValue("Brazo");

        cmbNivel.getItems().addAll("Básico", "Intermedio", "Avanzado");
        cmbNivel.setValue("Básico");

        cmbTipoPeso.getItems().addAll("Peso Propio", "Peso Extra");
        cmbTipoPeso.setValue("Peso Propio");
    }

    private void ConfigurarModoCreacion() {
        lblTituloPantalla.setText("Agregar Nuevo Ejercicio");
        lblSubtituloPantalla.setText("Ingresa los datos del nuevo ejercicio para integrarlo al catálogo oficial");
        btnGuardar.setText("💾 Guardar Nuevo Ejercicio");

        txtNombre.clear();
        txtRepeticiones.setText("12");
        txtSeries.setText("3");
        txtDescanso.setText("30");
        txtDuracion.setText("0");
        txtDescripcion.clear();
        txtUrlImagen.clear();
        txtUrlVideo.clear();
    }

    private void ConfigurarModoEdicion() {
        lblTituloPantalla.setText("Editar Ejercicio: " + ejercicioAEditar.getNombre());
        lblSubtituloPantalla.setText("Modifica los atributos del ejercicio (ID: " + ejercicioAEditar.getIdEjercicio() + ")");
        btnGuardar.setText("💾 Actualizar Ejercicio");

        txtNombre.setText(ejercicioAEditar.getNombre());
        cmbZonaCorporal.setValue(ejercicioAEditar.getZonaCorporal());
        cmbNivel.setValue(ejercicioAEditar.getNivel());
        cmbTipoPeso.setValue(ejercicioAEditar.getTipoPeso());
        txtRepeticiones.setText(String.valueOf(ejercicioAEditar.getRepeticiones()));
        txtSeries.setText(String.valueOf(ejercicioAEditar.getSeries()));
        txtDescanso.setText(String.valueOf(ejercicioAEditar.getDescanso()));
        txtDuracion.setText(String.valueOf(ejercicioAEditar.getDuracion()));
        txtDescripcion.setText(ejercicioAEditar.getDescripcion());
        txtUrlImagen.setText(ejercicioAEditar.getUrlImagen());
        txtUrlVideo.setText(ejercicioAEditar.getUrlVideo());
    }

    // ── Acciones de Guardar y Cancelar (PascalCase y camelCase) ──────────────

    @FXML
    public void GuardarEjercicio(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            MostrarAlerta("Campo Requerido", "El nombre del ejercicio es obligatorio.", Alert.AlertType.WARNING);
            return;
        }

        int reps = 12;
        int series = 3;
        int descanso = 30;
        int duracion = 0;

        try {
            if (!txtRepeticiones.getText().trim().isEmpty()) reps = Integer.parseInt(txtRepeticiones.getText().trim());
            if (!txtSeries.getText().trim().isEmpty()) series = Integer.parseInt(txtSeries.getText().trim());
            if (!txtDescanso.getText().trim().isEmpty()) descanso = Integer.parseInt(txtDescanso.getText().trim());
            if (!txtDuracion.getText().trim().isEmpty()) duracion = Integer.parseInt(txtDuracion.getText().trim());
        } catch (NumberFormatException e) {
            MostrarAlerta("Formato Inválido", "Por favor ingresa números válidos en repeticiones, series y descanso.", Alert.AlertType.ERROR);
            return;
        }

        if (reps <= 0 || series <= 0) {
            MostrarAlerta("Valores Fuera de Rango", "Las repeticiones y las series deben ser mayores a 0.", Alert.AlertType.WARNING);
            return;
        }

        String zona = cmbZonaCorporal.getValue();
        String nivel = cmbNivel.getValue();
        String tipoPeso = cmbTipoPeso.getValue();
        String descripcion = txtDescripcion.getText().trim();
        String urlImagen = txtUrlImagen.getText().trim();
        String urlVideo = txtUrlVideo.getText().trim();

        if (modoEdicion && ejercicioAEditar != null) {
            // Modo Actualizar
            EjercicioCoach actualizado = new EjercicioCoach(
                ejercicioAEditar.getIdEjercicio(),
                nombre, zona, nivel, tipoPeso,
                reps, series, duracion, descanso,
                descripcion, urlImagen, urlVideo
            );

            boolean exito = dao.Actualizar(actualizado);
            if (exito) {
                MostrarAlerta("Éxito", "El ejercicio '" + nombre + "' fue actualizado correctamente.", Alert.AlertType.INFORMATION);
                VolverAlCatalogo();
            } else {
                MostrarAlerta("Error", "No se pudo actualizar el ejercicio en la base de datos.", Alert.AlertType.ERROR);
            }
        } else {
            // Modo Insertar
            EjercicioCoach nuevo = new EjercicioCoach(
                nombre, zona, nivel, tipoPeso,
                reps, series, duracion, descanso,
                descripcion, urlImagen, urlVideo
            );

            boolean exito = dao.Agregar(nuevo);
            if (exito) {
                MostrarAlerta("Éxito", "El ejercicio '" + nombre + "' fue agregado exitosamente al catálogo.", Alert.AlertType.INFORMATION);
                VolverAlCatalogo();
            } else {
                MostrarAlerta("Error", "No se pudo insertar el ejercicio en la base de datos.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void Cancelar(ActionEvent event) {
        VolverAlCatalogo();
    }

    private void VolverAlCatalogo() {
        try {
            ejercicioAEditar = null;
            modoEdicion = false;
            App.setRoot("PantallaCoachCatalogoEjercicios");
        } catch (IOException e) {
            MostrarAlerta("Error de Navegación", "No se pudo regresar al catálogo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void MostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ── Alias camelCase para compatibilidad dual ─────────────────────────────
    @FXML public void guardarEjercicio(ActionEvent event) { GuardarEjercicio(event); }
    @FXML public void cancelar(ActionEvent event) { Cancelar(event); }
}