package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador de la pantalla de gestión del Catálogo de Ejercicios para el Coach.
 * Permite visualizar, agregar, editar, eliminar y filtrar ejercicios.
 *
 * @author Coach Module — FitGuide
 */
public class PantallaCoachCatalogoEjerciciosController implements Initializable {

    @FXML private TableView<EjercicioCoach> tablaEjercicios;
    @FXML private TableColumn<EjercicioCoach, Number> colId;
    @FXML private TableColumn<EjercicioCoach, String> colNombre;
    @FXML private TableColumn<EjercicioCoach, String> colZona;
    @FXML private TableColumn<EjercicioCoach, String> colNivel;
    @FXML private TableColumn<EjercicioCoach, String> colTipoPeso;
    @FXML private TableColumn<EjercicioCoach, Number> colRepeticiones;
    @FXML private TableColumn<EjercicioCoach, Number> colSeries;
    @FXML private TableColumn<EjercicioCoach, Number> colDescanso;
    @FXML private TableColumn<EjercicioCoach, Number> colDuracion;
    @FXML private TableColumn<EjercicioCoach, String> colDescripcion;

    @FXML private ComboBox<String> cmbFiltroZona;
    @FXML private ComboBox<String> cmbFiltroNivel;
    @FXML private ComboBox<String> cmbFiltroTipoPeso;
    @FXML private TextField txtBusqueda;
    @FXML private Label lblTotalRegistros;

    private final EjercicioCatalogoDao dao = new EjercicioCatalogoDao();
    private final ObservableList<EjercicioCoach> listaObservable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ConfigurarColumnas();
        ConfigurarFiltros();
        SembrarDatosInicialesSiVacio();
        CargarEjercicios();

        // Búsqueda en tiempo real
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> {
            BuscarEjercicio(newVal);
        });
    }

    // ── Configuración de UI ──────────────────────────────────────────────────

    private void ConfigurarColumnas() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idEjercicioProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colZona.setCellValueFactory(cellData -> cellData.getValue().zonaCorporalProperty());
        colNivel.setCellValueFactory(cellData -> cellData.getValue().nivelProperty());
        colTipoPeso.setCellValueFactory(cellData -> cellData.getValue().tipoPesoProperty());
        colRepeticiones.setCellValueFactory(cellData -> cellData.getValue().repeticionesProperty());
        colSeries.setCellValueFactory(cellData -> cellData.getValue().seriesProperty());
        colDescanso.setCellValueFactory(cellData -> cellData.getValue().descansoProperty());
        colDuracion.setCellValueFactory(cellData -> cellData.getValue().duracionProperty());
        colDescripcion.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());

        tablaEjercicios.setItems(listaObservable);
    }

    private void ConfigurarFiltros() {
        cmbFiltroZona.getItems().addAll("Todos", "Brazo", "Abdomen", "Pierna");
        cmbFiltroZona.setValue("Todos");
        cmbFiltroZona.setOnAction(e -> AplicarFiltros());

        cmbFiltroNivel.getItems().addAll("Todos", "Básico", "Intermedio", "Avanzado");
        cmbFiltroNivel.setValue("Todos");
        cmbFiltroNivel.setOnAction(e -> AplicarFiltros());

        cmbFiltroTipoPeso.getItems().addAll("Todos", "Peso Propio", "Peso Extra");
        cmbFiltroTipoPeso.setValue("Todos");
        cmbFiltroTipoPeso.setOnAction(e -> AplicarFiltros());
    }

    // ── Carga y Filtrado ─────────────────────────────────────────────────────

    public void CargarEjercicios() {
        listaObservable.clear();
        List<EjercicioCoach> ejercicios = dao.ObtenerTodos();
        listaObservable.addAll(ejercicios);
        ActualizarContador();
    }

    @FXML
    public void AplicarFiltros() {
        String zona = cmbFiltroZona.getValue();
        String nivel = cmbFiltroNivel.getValue();
        String tipo = cmbFiltroTipoPeso.getValue();

        listaObservable.clear();
        List<EjercicioCoach> filtrados = dao.ObtenerFiltrados(zona, nivel, tipo);
        listaObservable.addAll(filtrados);
        ActualizarContador();
    }

    public void BuscarEjercicio(String query) {
        if (query == null || query.trim().isEmpty()) {
            AplicarFiltros();
            return;
        }
        listaObservable.clear();
        List<EjercicioCoach> resultados = dao.BuscarPorNombre(query.trim());
        listaObservable.addAll(resultados);
        ActualizarContador();
    }

    private void ActualizarContador() {
        if (lblTotalRegistros != null) {
            lblTotalRegistros.setText("Total: " + listaObservable.size() + " ejercicios");
        }
    }

    // ── Operaciones CRUD ─────────────────────────────────────────────────────

    @FXML
    public void AgregarEjercicio(ActionEvent event) {
        try {
            PantallaCoachFormularioEjercicioController.modoEdicion = false;
            PantallaCoachFormularioEjercicioController.ejercicioAEditar = null;
            App.setRoot("PantallaCoachFormularioEjercicio");
        } catch (IOException e) {
            MostrarAlerta("Error de Pantalla", "No se pudo abrir la pantalla de formulario: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void EditarEjercicio(ActionEvent event) {
        EjercicioCoach seleccionado = tablaEjercicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            MostrarAlerta("Atención", "Por favor selecciona un ejercicio de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            PantallaCoachFormularioEjercicioController.modoEdicion = true;
            PantallaCoachFormularioEjercicioController.ejercicioAEditar = seleccionado;
            App.setRoot("PantallaCoachFormularioEjercicio");
        } catch (IOException e) {
            MostrarAlerta("Error de Pantalla", "No se pudo abrir la pantalla de edición: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void EliminarEjercicio(ActionEvent event) {
        EjercicioCoach seleccionado = tablaEjercicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            MostrarAlerta("Atención", "Por favor selecciona un ejercicio de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar el ejercicio?");
        confirmacion.setContentText("Se eliminará: " + seleccionado.getNombre() + " (ID: " + seleccionado.getIdEjercicio() + ")");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            if (dao.Eliminar(seleccionado.getIdEjercicio())) {
                MostrarAlerta("Éxito", "Ejercicio eliminado del catálogo.", Alert.AlertType.INFORMATION);
                AplicarFiltros();
            } else {
                MostrarAlerta("Error", "No se pudo eliminar el ejercicio.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void RegresarMenu(ActionEvent event) throws IOException {
        App.setRoot("CoachPanel");
    }

    // ── Alias camelCase para compatibilidad con ambos estilos ────────────────
    public void cargarEjercicios() { CargarEjercicios(); }
    @FXML public void aplicarFiltros() { AplicarFiltros(); }
    public void buscarEjercicio(String query) { BuscarEjercicio(query); }
    @FXML public void agregarEjercicio(ActionEvent event) { AgregarEjercicio(event); }
    @FXML public void editarEjercicio(ActionEvent event) { EditarEjercicio(event); }
    @FXML public void eliminarEjercicio(ActionEvent event) { EliminarEjercicio(event); }
    @FXML public void regresarMenu(ActionEvent event) throws IOException { RegresarMenu(event); }

    // ── Diálogo Modal de Formulario ──────────────────────────────────────────

    private Dialog<EjercicioCoach> CrearDialogoEjercicio(EjercicioCoach modelo) {
        Dialog<EjercicioCoach> dialog = new Dialog<>();
        dialog.setTitle(modelo == null ? "Nuevo Ejercicio" : "Editar Ejercicio");
        dialog.setHeaderText(modelo == null ? "Ingresa los datos del nuevo ejercicio" : "Modifica los datos del ejercicio");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 30, 10, 20));

        TextField txtNombre = new TextField(modelo != null ? modelo.getNombre() : "");
        ComboBox<String> cmbZona = new ComboBox<>();
        cmbZona.getItems().addAll("Brazo", "Abdomen", "Pierna");
        cmbZona.setValue(modelo != null ? modelo.getZonaCorporal() : "Brazo");

        ComboBox<String> cmbNivel = new ComboBox<>();
        cmbNivel.getItems().addAll("Básico", "Intermedio", "Avanzado");
        cmbNivel.setValue(modelo != null ? modelo.getNivel() : "Básico");

        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("Peso Propio", "Peso Extra");
        cmbTipo.setValue(modelo != null ? modelo.getTipoPeso() : "Peso Propio");

        TextField txtReps = new TextField(modelo != null ? String.valueOf(modelo.getRepeticiones()) : "12");
        TextField txtSeries = new TextField(modelo != null ? String.valueOf(modelo.getSeries()) : "3");
        TextField txtDescanso = new TextField(modelo != null ? String.valueOf(modelo.getDescanso()) : "30");
        TextField txtDuracion = new TextField(modelo != null ? String.valueOf(modelo.getDuracion()) : "0");
        TextArea txtDesc = new TextArea(modelo != null ? modelo.getDescripcion() : "");
        txtDesc.setPrefRowCount(3);
        TextField txtUrlImg = new TextField(modelo != null ? modelo.getUrlImagen() : "");
        TextField txtUrlVid = new TextField(modelo != null ? modelo.getUrlVideo() : "");

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Zona Corporal:"), 0, 1);
        grid.add(cmbZona, 1, 1);
        grid.add(new Label("Nivel:"), 0, 2);
        grid.add(cmbNivel, 1, 2);
        grid.add(new Label("Tipo de Peso:"), 0, 3);
        grid.add(cmbTipo, 1, 3);
        grid.add(new Label("Repeticiones:"), 0, 4);
        grid.add(txtReps, 1, 4);
        grid.add(new Label("Series:"), 0, 5);
        grid.add(txtSeries, 1, 5);
        grid.add(new Label("Descanso (s):"), 0, 6);
        grid.add(txtDescanso, 1, 6);
        grid.add(new Label("Duración (s):"), 0, 7);
        grid.add(txtDuracion, 1, 7);
        grid.add(new Label("Descripción:"), 0, 8);
        grid.add(txtDesc, 1, 8);
        grid.add(new Label("URL Imagen:"), 0, 9);
        grid.add(txtUrlImg, 1, 9);
        grid.add(new Label("URL Video / GIF:"), 0, 10);
        grid.add(txtUrlVid, 1, 10);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    MostrarAlerta("Validación", "El nombre del ejercicio no puede estar vacío.", Alert.AlertType.WARNING);
                    return null;
                }
                int reps = 10;
                int series = 3;
                int descanso = 30;
                int duracion = 0;
                try {
                    reps = Integer.parseInt(txtReps.getText().trim());
                    series = Integer.parseInt(txtSeries.getText().trim());
                    descanso = Integer.parseInt(txtDescanso.getText().trim());
                    duracion = Integer.parseInt(txtDuracion.getText().trim());
                } catch (NumberFormatException ignored) {}

                return new EjercicioCoach(
                    nombre,
                    cmbZona.getValue(),
                    cmbNivel.getValue(),
                    cmbTipo.getValue(),
                    reps,
                    series,
                    duracion,
                    descanso,
                    txtDesc.getText().trim(),
                    txtUrlImg.getText().trim(),
                    txtUrlVid.getText().trim()
                );
            }
            return null;
        });

        return dialog;
    }

    private void MostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ── Siembra Inicial de Datos ─────────────────────────────────────────────

    private void SembrarDatosInicialesSiVacio() {
        List<EjercicioCoach> actuales = dao.ObtenerTodos();
        if (actuales.isEmpty()) {
            System.out.println("Sembrando ejercicios iniciales en la base de datos...");
            dao.Agregar(new EjercicioCoach("Flexiones en pared", "Brazo", "Básico", "Peso Propio", 15, 3, 0, 30, "Apoya las manos en la pared, flexiona los codos acercando el pecho y empuja.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/pared_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/basico/flexion_pared.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones cobra", "Brazo", "Básico", "Peso Propio", 12, 3, 0, 30, "Boca abajo, empuja con las manos para levantar el pecho.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/flexiones_cobra_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/basico/flexiones_cobra.gif"));
            dao.Agregar(new EjercicioCoach("Plancha antebrazo a mano", "Brazo", "Básico", "Peso Propio", 12, 3, 0, 30, "Desde plancha de antebrazos, sube apoyando una mano a la vez.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/plancha_ab_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/basico/plancha_antebrazo_mano.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones elevadas", "Brazo", "Intermedio", "Peso Propio", 15, 3, 0, 30, "Apoya manos en una superficie elevada y haz la flexión.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/elevadas_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/intermedio/flexiones_elevadas.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones diamante", "Brazo", "Intermedio", "Peso Propio", 15, 3, 0, 30, "Junta las manos bajo el pecho y baja con los codos pegados al torso.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/flex_cerradas_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/intermedio/FlexDiamante.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones cerradas", "Brazo", "Intermedio", "Peso Propio", 15, 3, 0, 30, "Coloca tus manos juntas y flexiona.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/flex_cerradas_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/intermedio/flexiones_cerradas.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones a una mano", "Brazo", "Avanzado", "Peso Propio", 10, 3, 0, 45, "Flexiones apoyando el peso en una sola mano.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/avanzado/una_mano_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/avanzado/a_una_mano.gif"));
            dao.Agregar(new EjercicioCoach("Curl de bíceps", "Brazo", "Básico", "Peso Extra", 12, 3, 0, 30, "Flexiona los codos subiendo las mancuernas a los hombros.", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_pre.png", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/basico/curl_de_biseps.gif"));
            dao.Agregar(new EjercicioCoach("Curl martillo", "Brazo", "Básico", "Peso Extra", 15, 3, 0, 30, "Levanta el peso con agarre neutro.", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_mar_pre.png", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/basico/curl_martillo.gif"));
            dao.Agregar(new EjercicioCoach("Crunch abdominal", "Abdomen", "Básico", "Peso Propio", 15, 3, 0, 30, "Eleva los hombros contrayendo el abdomen.", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/crunch_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/basico/crinch_abdo.gif"));
            dao.Agregar(new EjercicioCoach("Plancha", "Abdomen", "Básico", "Peso Propio", 15, 3, 0, 30, "Apoya antebrazos y mantén todo el cuerpo recto.", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/plancha_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/basico/plancha.gif"));
            dao.Agregar(new EjercicioCoach("Sentadilla", "Pierna", "Básico", "Peso Propio", 15, 3, 0, 30, "Baja la cadera como si fueras a sentarte, con la espalda recta.", "/omr/uacm/sistemafitguide/preview/Pierna/pp/basico/sentadilla_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/pierna/basico/sentadilla.gif"));
            dao.Agregar(new EjercicioCoach("Zancadas", "Pierna", "Básico", "Peso Propio", 15, 3, 0, 30, "Da un paso al frente y flexiona ambas rodillas a 90 grados.", "/omr/uacm/sistemafitguide/preview/Pierna/pp/basico/zancadas_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/pierna/basico/zancadas.gif"));
        }
    }
}
