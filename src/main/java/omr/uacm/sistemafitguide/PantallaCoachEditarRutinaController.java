package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

/**
 * Controlador de la pantalla completa para la edición y ajuste progresivo de rutinas (Rol Coach).
 * Permite al Coach modificar series, descansos, peso adicional y repeticiones específicas
 * por cada ejercicio para un entrenamiento progresivo individualizado.
 * Soporta AMBOS estilos: PascalCase y camelCase.
 *
 * @author Coach Module — FitGuide
 */
public class PantallaCoachEditarRutinaController implements Initializable {

    // ── Variables Estáticas para Transferencia de Estado ─────────────────────
    public static Usuario usuarioSeleccionado = null;
    public static Rutina rutinaAEditar = null;

    // ── Componentes de la Interfaz (Notación Húngara) ────────────────────────
    @FXML private Label lblTituloPantalla;
    @FXML private Label lblInfoUsuario;
    @FXML private Label lblInfoRutina;

    @FXML private Spinner<Integer> spSeries;
    @FXML private Spinner<Integer> spDescanso;
    @FXML private TextField txtPesoExtra;

    @FXML private TableView<ItemEjercicioReps> tablaEjerciciosReps;
    @FXML private TableColumn<ItemEjercicioReps, String> colNombreEjercicio;
    @FXML private TableColumn<ItemEjercicioReps, Integer> colRepsObjetivo;

    @FXML private Button btnGuardarProgresion;
    @FXML private Button btnCancelar;

    private final CoachRutinaDao rutinaDao = new CoachRutinaDao();
    private final ObservableList<ItemEjercicioReps> listaEjerciciosReps = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ConfigurarTabla();
        CargarDatos();

        // Validar formato numérico en campo peso extra
        txtPesoExtra.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*\\.?\\d*")) {
                txtPesoExtra.setText(oldV);
            }
        });
    }

    private void ConfigurarTabla() {
        colNombreEjercicio.setCellValueFactory(c -> c.getValue().nombreProperty());
        colRepsObjetivo.setCellValueFactory(c -> c.getValue().repeticionesProperty().asObject());

        // Hacer editable la columna de repeticiones directamente en la tabla
        tablaEjerciciosReps.setEditable(true);
        colRepsObjetivo.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colRepsObjetivo.setOnEditCommit(event -> {
            Integer nuevoValor = event.getNewValue();
            if (nuevoValor != null && nuevoValor > 0) {
                event.getRowValue().setRepeticiones(nuevoValor);
            } else {
                event.getRowValue().setRepeticiones(event.getOldValue());
                MostrarAlerta("Valor Inválido", "Las repeticiones deben ser un número entero mayor a 0.", Alert.AlertType.WARNING);
            }
        });

        tablaEjerciciosReps.setItems(listaEjerciciosReps);
    }

    private void CargarDatos() {
        if (usuarioSeleccionado == null || rutinaAEditar == null) {
            lblTituloPantalla.setText("Editar Rutina de Usuario");
            lblInfoUsuario.setText("No hay usuario seleccionado");
            lblInfoRutina.setText("No hay rutina seleccionada");
            return;
        }

        lblTituloPantalla.setText("Ajuste de Entrenamiento Progresivo");
        lblInfoUsuario.setText("Atleta: " + usuarioSeleccionado.getNombreUsuario()
                + "  |  Peso: " + usuarioSeleccionado.getPeso() + " kg"
                + "  |  Estatura: " + usuarioSeleccionado.getAltura() + " m"
                + "  |  Género: " + usuarioSeleccionado.getGenero());

        lblInfoRutina.setText("Rutina: " + rutinaAEditar.getGrupoMuscular().toUpperCase()
                + "  ·  Nivel: " + rutinaAEditar.getNivel()
                + "  ·  Modalidad: " + rutinaAEditar.getTipoPeso());

        // Configurar Spinners
        spSeries.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, Math.max(1, rutinaAEditar.getSeries())));
        spDescanso.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 300, Math.max(10, rutinaAEditar.getTiempoDescanso()), 5));
        txtPesoExtra.setText(String.valueOf(rutinaAEditar.getPesoExtraKg()));

        // Cargar lista de ejercicios y repeticiones
        listaEjerciciosReps.clear();
        Map<String, Integer> mapa = rutinaAEditar.getMapaRepeticiones();
        if (mapa != null && !mapa.isEmpty()) {
            for (Map.Entry<String, Integer> entry : mapa.entrySet()) {
                listaEjerciciosReps.add(new ItemEjercicioReps(entry.getKey(), entry.getValue()));
            }
        } else {
            // Si el mapa estaba vacío, obtener ejercicios sugeridos del catálogo
            boolean usaMancuernas = "Peso Extra".equalsIgnoreCase(rutinaAEditar.getTipoPeso());
            List<Ejercicio> base = CatalogoEjercicios.obtenerRutina(
                rutinaAEditar.getGrupoMuscular(), rutinaAEditar.getNivel(), usaMancuernas
            );
            if (base != null) {
                for (Ejercicio ej : base) {
                    listaEjerciciosReps.add(new ItemEjercicioReps(ej.getNombre(), ej.getRepeticionesObjetivo()));
                }
            }
        }
    }

    // ── Guardar y Cancelar (PascalCase y camelCase) ──────────────────────────

    @FXML
    public void GuardarProgresion(ActionEvent event) {
        if (usuarioSeleccionado == null || rutinaAEditar == null) {
            MostrarAlerta("Error", "No hay datos de usuario o rutina para guardar.", Alert.AlertType.ERROR);
            return;
        }

        int series = spSeries.getValue();
        int descanso = spDescanso.getValue();
        double pesoExtra = 0.0;

        try {
            String txtP = txtPesoExtra.getText().trim();
            if (!txtP.isEmpty()) pesoExtra = Double.parseDouble(txtP);
        } catch (NumberFormatException e) {
            MostrarAlerta("Formato Inválido", "Por favor ingresa un número válido en el peso extra.", Alert.AlertType.ERROR);
            return;
        }

        if (pesoExtra < 0) {
            MostrarAlerta("Valor Inválido", "El peso extra no puede ser negativo.", Alert.AlertType.WARNING);
            return;
        }

        // Armar el nuevo mapa de repeticiones
        Map<String, Integer> nuevoMapa = new HashMap<>();
        for (ItemEjercicioReps item : listaEjerciciosReps) {
            nuevoMapa.put(item.getNombre(), item.getRepeticiones());
        }

        // Crear objeto Rutina actualizado
        Rutina rutinaActualizada = new Rutina(rutinaAEditar.getGrupoMuscular());
        rutinaActualizada.setNivel(rutinaAEditar.getNivel());
        rutinaActualizada.setTipoPeso(rutinaAEditar.getTipoPeso());
        rutinaActualizada.setSeries(series);
        rutinaActualizada.setTiempoDescanso(descanso);
        rutinaActualizada.setPesoExtraKg(pesoExtra);
        rutinaActualizada.setMapaRepeticiones(nuevoMapa);

        // Actualizar en base de datos SQLite
        boolean exito = rutinaDao.ActualizarRutinaProgresion(usuarioSeleccionado.getIdUsuario(), rutinaActualizada);

        if (exito) {
            // Actualizar mapa en memoria si el usuario activo es este
            String llave = rutinaAEditar.getGrupoMuscular() + "_" + rutinaAEditar.getNivel() + "_" + rutinaAEditar.getTipoPeso();
            PantallaConfiguracionRutinaController.mapaRutinas.put(llave, rutinaActualizada);

            MostrarAlerta("Éxito", "La rutina de " + usuarioSeleccionado.getNombreUsuario()
                    + " ha sido actualizada con éxito para entrenamiento progresivo.", Alert.AlertType.INFORMATION);
            VolverAGestionRutinas();
        } else {
            MostrarAlerta("Error", "No se pudo guardar la rutina en la base de datos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void Cancelar(ActionEvent event) {
        VolverAGestionRutinas();
    }

    private void VolverAGestionRutinas() {
        try {
            usuarioSeleccionado = null;
            rutinaAEditar = null;
            App.setRoot("PantallaCoachGestionRutinas");
        } catch (IOException e) {
            MostrarAlerta("Error de Navegación", "No se pudo volver a la pantalla de rutinas: " + e.getMessage(), Alert.AlertType.ERROR);
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
    @FXML public void guardarProgresion(ActionEvent event) { GuardarProgresion(event); }
    @FXML public void cancelar(ActionEvent event) { Cancelar(event); }

    // ── Clase Auxiliar de Elemento de Tabla (Soporta Ambos Estilos) ──────────

    public static class ItemEjercicioReps {
        private final SimpleStringProperty nombre;
        private final SimpleIntegerProperty repeticiones;

        public ItemEjercicioReps(String nombre, int repeticiones) {
            this.nombre = new SimpleStringProperty(nombre);
            this.repeticiones = new SimpleIntegerProperty(repeticiones);
        }

        public String getNombre() { return nombre.get(); }
        public String GetNombre() { return getNombre(); }
        public void setNombre(String value) { nombre.set(value); }
        public void SetNombre(String value) { setNombre(value); }
        public SimpleStringProperty nombreProperty() { return nombre; }

        public int getRepeticiones() { return repeticiones.get(); }
        public int GetRepeticiones() { return getRepeticiones(); }
        public void setRepeticiones(int value) { repeticiones.set(value); }
        public void SetRepeticiones(int value) { setRepeticiones(value); }
        public SimpleIntegerProperty repeticionesProperty() { return repeticiones; }
    }
}
