package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla de gestión del Catálogo de Ejercicios para el Coach.
 * Cumple con [HU-03]:
 * - Filtrado en tiempo real por grupo muscular (zona corporal).
 * - Filtrado reactivo por tiempo de descanso máximo.
 * - Reflejo inmediato de cambios de repeticiones tras editar.
 * - Ordenación de ejercicios por duración, descanso, repeticiones y dificultad,
 *   tanto por selector ComboBox como mediante clic en las cabeceras de columna.
 * - Compatibilidad total con ambos estilos de programación: PascalCase y camelCase.
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
    @FXML private TableColumn<EjercicioCoach, String> colDescripcion;

    @FXML private ComboBox<String> cmbFiltroZona;
    @FXML private ComboBox<String> cmbFiltroNivel;
    @FXML private ComboBox<String> cmbFiltroTipoPeso;
    @FXML private ComboBox<String> cmbOrdenar;
    @FXML private TextField txtBusqueda;
    @FXML private TextField txtFiltroDescanso;
    @FXML private Label lblTotalRegistros;

    private final EjercicioCatalogoDao dao = new EjercicioCatalogoDao();
    private final ObservableList<EjercicioCoach> listaBase = FXCollections.observableArrayList();
    private FilteredList<EjercicioCoach> listaFiltrada;
    private SortedList<EjercicioCoach> listaOrdenada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ConfigurarColumnas();
        ConfigurarFiltros();
        SembrarDatosInicialesSiVacio();
        CargarEjercicios();
        ConfigurarEventosTiempoReal();
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
        colDescripcion.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());

        // Cada columna es ordenable interactivamente al hacer clic en su cabecera
        colId.setSortable(true);
        colNombre.setSortable(true);
        colZona.setSortable(true);
        colNivel.setSortable(true);
        colTipoPeso.setSortable(true);
        colRepeticiones.setSortable(true);
        colSeries.setSortable(true);
        colDescanso.setSortable(true);
        colDescripcion.setSortable(true);

        // Vinculación de lista observable filtrada y ordenada
        listaFiltrada = new FilteredList<>(listaBase, p -> true);
        listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaEjercicios.comparatorProperty());
        tablaEjercicios.setItems(listaOrdenada);
    }

    private void ConfigurarFiltros() {
        // [HU-03] Filtro de Zona Muscular (exclusivo para el grupo muscular seleccionado)
        cmbFiltroZona.getItems().addAll("Todos", "Brazo", "Abdomen", "Pierna");
        cmbFiltroZona.setValue("Todos");
        cmbFiltroZona.setOnAction(e -> AplicarFiltrosTiempoReal());

        // Filtro por nivel
        cmbFiltroNivel.getItems().addAll("Todos", "Básico", "Intermedio", "Avanzado");
        cmbFiltroNivel.setValue("Todos");
        cmbFiltroNivel.setOnAction(e -> AplicarFiltrosTiempoReal());

        // Filtro por tipo de carga
        cmbFiltroTipoPeso.getItems().addAll("Todos", "Peso Propio", "Peso Extra");
        cmbFiltroTipoPeso.setValue("Todos");
        cmbFiltroTipoPeso.setOnAction(e -> AplicarFiltrosTiempoReal());

        // [HU-03] Selector de ordenación rápida (duración, descanso, repeticiones, dificultad)
        if (cmbOrdenar != null) {
            cmbOrdenar.getItems().addAll(
                    "Por defecto",
                    "Duración (Menor a Mayor)",
                    "Duración (Mayor a Menor)",
                    "Descanso (Menor a Mayor)",
                    "Descanso (Mayor a Menor)",
                    "Repeticiones (Menor a Mayor)",
                    "Repeticiones (Mayor a Menor)",
                    "Dificultad (A-Z)"
            );
            cmbOrdenar.setValue("Por defecto");
            cmbOrdenar.setOnAction(e -> AplicarOrdenacion());
        }
    }

    private void ConfigurarEventosTiempoReal() {
        // [HU-03] Búsqueda por texto en tiempo real
        if (txtBusqueda != null) {
            txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> {
                AplicarFiltrosTiempoReal();
            });
        }

        // [HU-03] Filtrado reactivo por descanso máximo en tiempo real
        if (txtFiltroDescanso != null) {
            txtFiltroDescanso.textProperty().addListener((obs, oldVal, newVal) -> {
                AplicarFiltrosTiempoReal();
            });
        }
    }

    // ── Carga y Filtrado HU-03 ───────────────────────────────────────────────

    public void CargarEjercicios() {
        listaBase.clear();
        List<EjercicioCoach> ejercicios = dao.ObtenerTodos();
        listaBase.addAll(ejercicios);
        AplicarFiltrosTiempoReal();
    }

    /**
     * Aplica los filtros de búsqueda, zona muscular, nivel, tipo de carga
     * y tiempo de descanso en tiempo real conforme a [HU-03].
     */
    @FXML
    public void AplicarFiltrosTiempoReal() {
        String zona = cmbFiltroZona != null ? cmbFiltroZona.getValue() : "Todos";
        String nivel = cmbFiltroNivel != null ? cmbFiltroNivel.getValue() : "Todos";
        String tipo = cmbFiltroTipoPeso != null ? cmbFiltroTipoPeso.getValue() : "Todos";
        String query = txtBusqueda != null ? txtBusqueda.getText() : "";
        String descansoTxt = txtFiltroDescanso != null ? txtFiltroDescanso.getText() : "";

        Integer maxDescanso = null;
        if (descansoTxt != null && !descansoTxt.trim().isEmpty()) {
            try {
                maxDescanso = Integer.parseInt(descansoTxt.trim());
            } catch (NumberFormatException ignored) {
                maxDescanso = null;
            }
        }
        final Integer filtroDescanso = maxDescanso;

        listaFiltrada.setPredicate(ej -> {
            // Criterio 1: Zona muscular (exclusivo para la zona seleccionada)
            if (zona != null && !zona.equals("Todos") && !zona.equalsIgnoreCase(ej.getZonaCorporal())) {
                return false;
            }
            // Criterio 2: Nivel de dificultad
            if (nivel != null && !nivel.equals("Todos") && !nivel.equalsIgnoreCase(ej.getNivel())) {
                return false;
            }
            // Criterio 3: Tipo de peso
            if (tipo != null && !tipo.equals("Todos") && !tipo.equalsIgnoreCase(ej.getTipoPeso())) {
                return false;
            }
            // Criterio 4: Tiempo de descanso máximo
            if (filtroDescanso != null && ej.getDescanso() > filtroDescanso) {
                return false;
            }
            // Criterio 5: Búsqueda por nombre
            if (query != null && !query.trim().isEmpty()) {
                if (!ej.getNombre().toLowerCase().contains(query.trim().toLowerCase())) {
                    return false;
                }
            }
            return true;
        });

        ActualizarContador();
    }

    /**
     * Aplica la ordenación seleccionada en el ComboBox activando el orden correspondiente
     * en la columna del TableView.
     */
    @FXML
    public void AplicarOrdenacion() {
        String seleccion = cmbOrdenar != null ? cmbOrdenar.getValue() : "Por defecto";
        tablaEjercicios.getSortOrder().clear();

        if ("Duración (Menor a Mayor)".equals(seleccion)) {
            listaOrdenada.comparatorProperty().unbind();
            listaOrdenada.setComparator(java.util.Comparator.comparingInt(EjercicioCoach::getDuracion));
        } else if ("Duración (Mayor a Menor)".equals(seleccion)) {
            listaOrdenada.comparatorProperty().unbind();
            listaOrdenada.setComparator((a, b) -> Integer.compare(b.getDuracion(), a.getDuracion()));
        } else {
            listaOrdenada.comparatorProperty().bind(tablaEjercicios.comparatorProperty());
            if ("Descanso (Menor a Mayor)".equals(seleccion)) {
                colDescanso.setSortType(TableColumn.SortType.ASCENDING);
                tablaEjercicios.getSortOrder().add(colDescanso);
            } else if ("Descanso (Mayor a Menor)".equals(seleccion)) {
                colDescanso.setSortType(TableColumn.SortType.DESCENDING);
                tablaEjercicios.getSortOrder().add(colDescanso);
            } else if ("Repeticiones (Menor a Mayor)".equals(seleccion)) {
                colRepeticiones.setSortType(TableColumn.SortType.ASCENDING);
                tablaEjercicios.getSortOrder().add(colRepeticiones);
            } else if ("Repeticiones (Mayor a Menor)".equals(seleccion)) {
                colRepeticiones.setSortType(TableColumn.SortType.DESCENDING);
                tablaEjercicios.getSortOrder().add(colRepeticiones);
            } else if ("Dificultad (A-Z)".equals(seleccion)) {
                colNivel.setSortType(TableColumn.SortType.ASCENDING);
                tablaEjercicios.getSortOrder().add(colNivel);
            }
        }
    }

    private void ActualizarContador() {
        if (lblTotalRegistros != null) {
            lblTotalRegistros.setText("Total: " + listaFiltrada.size() + " ejercicios");
        }
    }

    // ── Operaciones CRUD y Navegación ────────────────────────────────────────

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
                CargarEjercicios();
            } else {
                MostrarAlerta("Error", "No se pudo eliminar el ejercicio.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void RegresarMenu(ActionEvent event) throws IOException {
        App.setRoot("CoachPanel");
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
            dao.Agregar(new EjercicioCoach("Flexiones en pared", "Brazo", "Básico", "Peso Propio", 15, 3, 20, 30, "Apoya las manos en la pared, flexiona los codos acercando el pecho y empuja.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/pared_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/basico/flexion_pared.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones cobra", "Brazo", "Básico", "Peso Propio", 12, 3, 25, 30, "Boca abajo, empuja con las manos para levantar el pecho.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/flexiones_cobra_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/basico/flexiones_cobra.gif"));
            dao.Agregar(new EjercicioCoach("Plancha antebrazo a mano", "Brazo", "Básico", "Peso Propio", 12, 3, 30, 30, "Desde plancha de antebrazos, sube apoyando una mano a la vez.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/plancha_ab_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/basico/plancha_antebrazo_mano.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones elevadas", "Brazo", "Intermedio", "Peso Propio", 15, 3, 25, 30, "Apoya manos en una superficie elevada y haz la flexión.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/elevadas_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/intermedio/flexiones_elevadas.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones diamante", "Brazo", "Intermedio", "Peso Propio", 15, 3, 30, 30, "Junta las manos bajo el pecho y baja con los codos pegados al torso.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/flex_cerradas_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/intermedio/FlexDiamante.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones cerradas", "Brazo", "Intermedio", "Peso Propio", 15, 3, 25, 30, "Coloca tus manos juntas y flexiona.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/flex_cerradas_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/intermedio/flexiones_cerradas.gif"));
            dao.Agregar(new EjercicioCoach("Flexiones a una mano", "Brazo", "Avanzado", "Peso Propio", 10, 3, 35, 45, "Flexiones apoyando el peso en una sola mano.", "/omr/uacm/sistemafitguide/preview/Brazo/pp/avanzado/una_mano_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/avanzado/a_una_mano.gif"));
            dao.Agregar(new EjercicioCoach("Curl de bíceps", "Brazo", "Básico", "Peso Extra", 12, 3, 30, 30, "Flexiona los codos subiendo las mancuernas a los hombros.", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_pre.png", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/basico/curl_de_biseps.gif"));
            dao.Agregar(new EjercicioCoach("Curl martillo", "Brazo", "Básico", "Peso Extra", 15, 3, 30, 30, "Levanta el peso con agarre neutro.", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_mar_pre.png", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/basico/curl_martillo.gif"));
            dao.Agregar(new EjercicioCoach("Crunch abdominal", "Abdomen", "Básico", "Peso Propio", 15, 3, 20, 20, "Eleva los hombros contrayendo el abdomen.", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/crunch_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/basico/crinch_abdo.gif"));
            dao.Agregar(new EjercicioCoach("Plancha", "Abdomen", "Básico", "Peso Propio", 15, 3, 40, 30, "Apoya antebrazos y mantén todo el cuerpo recto.", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/plancha_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/basico/plancha.gif"));
            dao.Agregar(new EjercicioCoach("Sentadilla", "Pierna", "Básico", "Peso Propio", 15, 3, 30, 30, "Baja la cadera como si fueras a sentarte, con la espalda recta.", "/omr/uacm/sistemafitguide/preview/Pierna/pp/basico/sentadilla_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/pierna/basico/sentadilla.gif"));
            dao.Agregar(new EjercicioCoach("Zancadas", "Pierna", "Básico", "Peso Propio", 15, 3, 30, 30, "Da un paso al frente y flexiona ambas rodillas a 90 grados.", "/omr/uacm/sistemafitguide/preview/Pierna/pp/basico/zancadas_pre.png", "/omr/uacm/sistemafitguide/videos/peso_propio/pierna/basico/zancadas.gif"));
        }
    }

    // ── Alias camelCase para compatibilidad dual (PascalCase + camelCase) ─────
    public void cargarEjercicios() { CargarEjercicios(); }
    @FXML public void aplicarFiltros() { AplicarFiltrosTiempoReal(); }
    @FXML public void aplicarFiltrosTiempoReal() { AplicarFiltrosTiempoReal(); }
    public void buscarEjercicio(String query) { AplicarFiltrosTiempoReal(); }
    @FXML public void agregarEjercicio(ActionEvent event) { AgregarEjercicio(event); }
    @FXML public void editarEjercicio(ActionEvent event) { EditarEjercicio(event); }
    @FXML public void eliminarEjercicio(ActionEvent event) { EliminarEjercicio(event); }
    @FXML public void regresarMenu(ActionEvent event) throws IOException { RegresarMenu(event); }
    @FXML public void aplicarOrdenacion() { AplicarOrdenacion(); }
}

