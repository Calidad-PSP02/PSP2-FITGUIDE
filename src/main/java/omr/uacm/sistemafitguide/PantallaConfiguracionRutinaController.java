/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * FXML Controller class
 *
 * @author MiguelDiaz 
 * @co-author SaulAngulo
 */
public class PantallaConfiguracionRutinaController implements Initializable {

    @FXML private Label lblTituloConfig;
    @FXML private Button btnVolverBienvenida, btnConfigurarDescanso, btnIrBienvenida, btnIniciarRutinaFinal;

    @FXML private HBox paso1Musculos;
    @FXML private VBox paso2Peso, paso3Nivel, paso4Ejercicios, paso5Descanso;
    @FXML private FlowPane flowPaneEjercicios;
    @FXML private TextField txtDescanso;

    @FXML private ImageView imgBrazo, imgAbdomen, imgPierna;
    @FXML private ImageView imgPesoPropio, imgPesoExtra;
    @FXML private ImageView imgBasico, imgIntermedio, imgAvanzado;

    private String grupoMuscularSeleccionado = "";
    private String tipoPesoSeleccionado      = "";
    private String nivelSeleccionado         = "";

    private List<TextField> cajasTextoActuales = new ArrayList<>();

    private VBox panelKilos = null;
    private double kilosSeleccionados = 0.0;


    private int seriesSeleccionadas = 2;


    public static Map<String, Rutina> mapaRutinas = new HashMap<>();
    public static String ultimoGrupoSeleccionadoG = "";
    public static int idUsuarioLogueado = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (!mapaRutinas.containsKey("Brazo")) mapaRutinas.put("Brazo", new Rutina("Brazo"));
        if (!mapaRutinas.containsKey("Abdomen")) mapaRutinas.put("Abdomen", new Rutina("Abdomen"));
        if (!mapaRutinas.containsKey("Pierna")) mapaRutinas.put("Pierna", new Rutina("Pierna"));

        cargarImagen(imgBrazo, "/omr/uacm/sistemafitguide/imagenes/hombrelagartijabrazo.jpeg");
        cargarImagen(imgAbdomen, "/omr/uacm/sistemafitguide/imagenes/hombreabdominalesabdominal.jpeg");
        cargarImagen(imgPierna, "/omr/uacm/sistemafitguide/imagenes/hombresentadillapierna.jpeg");

        btnIrBienvenida.disableProperty().bind(txtDescanso.textProperty().isEmpty());
        btnIniciarRutinaFinal.disableProperty().bind(txtDescanso.textProperty().isEmpty());

        txtDescanso.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) txtDescanso.setText(newV.replaceAll("[^\\d]", ""));
        });

        volverPaso1();
    }

    @FXML private void accionVolverBienvenida() throws IOException {
        App.setRoot("PantallaBienvenidoUsuario");
    }

    @FXML private void seleccionarBrazo() { grupoMuscularSeleccionado = "Brazo"; ultimoGrupoSeleccionadoG = "Brazo"; avanzarPaso2(); }
    @FXML private void seleccionarAbdomen() { grupoMuscularSeleccionado = "Abdomen"; ultimoGrupoSeleccionadoG = "Abdomen"; avanzarPaso2(); }
    @FXML private void seleccionarPierna() { grupoMuscularSeleccionado = "Pierna"; ultimoGrupoSeleccionadoG = "Pierna"; avanzarPaso2(); }

    private void avanzarPaso2() {
        lblTituloConfig.setText("PASO 2: TIPO DE ENTRENAMIENTO (" + grupoMuscularSeleccionado.toUpperCase() + ")");
        quitarPanelKilos();
        mostrarPaso(paso2Peso);

        switch (grupoMuscularSeleccionado.toLowerCase()) {
            case "brazo":
                cargarImagen(imgPesoPropio, "/omr/uacm/sistemafitguide/imagenes/hombre_brazo_peso_propio.jpeg");
                cargarImagen(imgPesoExtra, "/omr/uacm/sistemafitguide/imagenes/hombre_brazo_peso_extra.jpg");
                break;
            case "abdomen":
                cargarImagen(imgPesoPropio, "/omr/uacm/sistemafitguide/imagenes/hombre_abdomen_peso_propio.jpg");
                cargarImagen(imgPesoExtra, "/omr/uacm/sistemafitguide/imagenes/mujer_hombre_abdomen_peso_extra.jpg");
                break;
            case "pierna":
                cargarImagen(imgPesoPropio, "/omr/uacm/sistemafitguide/imagenes/mujer_pierna_peso_propio.jpg");
                cargarImagen(imgPesoExtra, "/omr/uacm/sistemafitguide/imagenes/hombre_pierna_peso_extra.jpeg");
                break;
        }
    }

    @FXML private void volverPaso1() {
        lblTituloConfig.setText("PASO 1: SELECCIONA GRUPO MUSCULAR");
        quitarPanelKilos();
        mostrarPaso(paso1Musculos);
    }

    @FXML
    private void seleccionarPesoPropio() {
        tipoPesoSeleccionado = "Peso Propio";
        mapaRutinas.get(grupoMuscularSeleccionado).setTipoPeso("Peso Propio");
        kilosSeleccionados = 0.0;
        quitarPanelKilos();
        avanzarPaso3();
    }

    @FXML
    private void seleccionarPesoExtra() {
        tipoPesoSeleccionado = "Peso Extra";
        mapaRutinas.get(grupoMuscularSeleccionado).setTipoPeso("Peso Extra");
        kilosSeleccionados = 0.0;
        mostrarSelectorKilos();
    }


    private void mostrarSelectorKilos() {
        quitarPanelKilos();

        Label lblTitulo = new Label("¿Cuánto peso extra cargarás?");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTitulo.setStyle("-fx-text-fill: #2D3E40;");
        lblTitulo.setMaxWidth(Double.MAX_VALUE);
        lblTitulo.setAlignment(Pos.CENTER);

        javafx.scene.layout.Region lineaSep = new javafx.scene.layout.Region();
        lineaSep.setMaxWidth(Double.MAX_VALUE);
        lineaSep.setPrefHeight(1);
        lineaSep.setStyle("-fx-background-color: #D0D0D0;");

        Label lblCustom = new Label("Escribe el peso:");
        lblCustom.setStyle("-fx-text-fill: #5F5E5A; -fx-font-size: 14px; -fx-font-weight: bold;");

        TextField txtCustom = new TextField();
        txtCustom.setPromptText("ej. 7.5");
        txtCustom.setPrefWidth(100);
        txtCustom.setMaxWidth(100);
        txtCustom.setPrefHeight(36);
        txtCustom.setAlignment(Pos.CENTER);
        txtCustom.setStyle("-fx-font-size: 14px; -fx-background-radius: 6px;");

        txtCustom.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*\\.?\\d*")) txtCustom.setText(oldV);
        });

        txtCustom.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.isEmpty()) {
                try {
                    double val = Double.parseDouble(newV);
                    kilosSeleccionados = (val > 0) ? val : 0.0;
                } catch (NumberFormatException ignored) {
                    kilosSeleccionados = 0.0;
                }
            } else {
                kilosSeleccionados = 0.0;
            }
        });

        Label lblKg = new Label("kg");
        lblKg.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2D3E40;");

        HBox filaCustom = new HBox(12, lblCustom, txtCustom, lblKg);
        filaCustom.setAlignment(Pos.CENTER);
        filaCustom.setMaxWidth(Double.MAX_VALUE);
        filaCustom.setPadding(new Insets(10, 0, 10, 0));

        Button btnContinuar = new Button("Continuar ");
        btnContinuar.setPrefWidth(220);
        btnContinuar.setStyle(
                "-fx-background-color: #387373; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-font-size: 14px; " +
                        "-fx-background-radius: 20px; -fx-padding: 10 28 10 28; -fx-cursor: hand;"
        );

        btnContinuar.setOnAction(e -> {
            if (kilosSeleccionados <= 0) {
                mostrarAlertaError("Peso no válido", "Por favor introduce un peso mayor a 0 kg antes de continuar.");
                return;
            }
            avanzarPaso3();
        });

        HBox filaContinuar = new HBox(btnContinuar);
        filaContinuar.setAlignment(Pos.CENTER);

        panelKilos = new VBox(16, lineaSep, lblTitulo, filaCustom, filaContinuar);
        panelKilos.setAlignment(Pos.CENTER);
        panelKilos.setMaxWidth(Double.MAX_VALUE);
        panelKilos.setPadding(new Insets(16, 40, 16, 40));

        paso2Peso.getChildren().add(panelKilos);
    }

    private void quitarPanelKilos() {
        if (panelKilos != null) {
            paso2Peso.getChildren().remove(panelKilos);
            panelKilos = null;
        }
    }

    private String formatKg(double kg) {
        return (kg == Math.floor(kg)) ? String.valueOf((int) kg) : String.valueOf(kg);
    }

    private void avanzarPaso3() {
        lblTituloConfig.setText("PASO 3: SELECCIONA NIVEL (" + grupoMuscularSeleccionado.toUpperCase() + ")");
        mostrarPaso(paso3Nivel);

        switch (grupoMuscularSeleccionado.toLowerCase()) {
            case "brazo":
                cargarImagen(imgBasico, "/omr/uacm/sistemafitguide/imagenes/mujer_brazo_basico_pero_propio.jpg");
                cargarImagen(imgIntermedio, "/omr/uacm/sistemafitguide/imagenes/hombre_brazo_intermedio_peso_propio.jpg");
                cargarImagen(imgAvanzado, "/omr/uacm/sistemafitguide/imagenes/hombre_brazo_avanzado_peso_propio.jpg");
                break;
            case "abdomen":
                cargarImagen(imgBasico, "/omr/uacm/sistemafitguide/imagenes/hombre_abdomen_basico_peso_propio.jpg");
                cargarImagen(imgIntermedio, "/omr/uacm/sistemafitguide/imagenes/hombre_abdomen_intermedio_peso_propio.jpg");
                cargarImagen(imgAvanzado, "/omr/uacm/sistemafitguide/imagenes/mujer_abdomen_avanzado_peso_propio.jpg");
                break;
            case "pierna":
                cargarImagen(imgBasico, "/omr/uacm/sistemafitguide/imagenes/hombre_pierna_basico_peso_propio.jpg");
                cargarImagen(imgIntermedio, "/omr/uacm/sistemafitguide/imagenes/mujer_pierna_intermedio_peso_propio.jpg");
                cargarImagen(imgAvanzado, "/omr/uacm/sistemafitguide/imagenes/hombre_pierna_avanzado_peso_propio.jpg");
                break;
        }
    }

    @FXML private void volverPaso2() { avanzarPaso2(); }

    @FXML private void seleccionarBasico() { nivelSeleccionado = "Básico"; mapaRutinas.get(grupoMuscularSeleccionado).setNivel("Básico"); avanzarPaso4(); }
    @FXML private void seleccionarIntermedio() { nivelSeleccionado = "Intermedio"; mapaRutinas.get(grupoMuscularSeleccionado).setNivel("Intermedio"); avanzarPaso4(); }
    @FXML private void seleccionarAvanzado() { nivelSeleccionado = "Avanzado"; mapaRutinas.get(grupoMuscularSeleccionado).setNivel("Avanzado"); avanzarPaso4(); }

    private void avanzarPaso4() {
        String infoPeso = tipoPesoSeleccionado.equals("Peso Extra") && kilosSeleccionados > 0
                ? " · " + formatKg(kilosSeleccionados) + " kg"
                : "";

        lblTituloConfig.setText("PASO 4: REPETICIONES Y SERIES (" + nivelSeleccionado.toUpperCase() + infoPeso + ")");
        mostrarPaso(paso4Ejercicios);

        btnConfigurarDescanso.setVisible(true);
        btnConfigurarDescanso.setDisable(false);

        flowPaneEjercicios.getChildren().clear();
        cajasTextoActuales.clear();

        List<String[]> ejercicios = obtenerEjerciciosPorFiltro();
        Rutina rutinaActual = mapaRutinas.get(grupoMuscularSeleccionado);

        for (String[] ej : ejercicios) {
            VBox tarjeta = crearMiniTarjetaEjercicio(ej[0], ej[1], ej[2]);
            flowPaneEjercicios.getChildren().add(tarjeta);

            HBox hbox = (HBox) tarjeta.getChildren().get(tarjeta.getChildren().size() - 1);
            TextField tf = (TextField) hbox.getChildren().get(0);

            if (rutinaActual.getMapaRepeticiones().containsKey(ej[0])) {
                tf.setText(String.valueOf(rutinaActual.getMapaRepeticiones().get(ej[0])));
            }
            cajasTextoActuales.add(tf);
        }

        VBox panelSeries = new VBox(10);
        panelSeries.setAlignment(Pos.CENTER);
        panelSeries.setPrefSize(180, 220);
        panelSeries.setStyle("-fx-background-color: #E8F5E9; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label lblTitSeries = new Label("¿Cuántas Series?");
        lblTitSeries.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTitSeries.setStyle("-fx-text-fill: #2D5A27;");

        ComboBox<Integer> comboSeries = new ComboBox<>();
        comboSeries.getItems().addAll(1, 2, 3, 4, 5);
        comboSeries.setValue(rutinaActual.getSeries() > 0 ? rutinaActual.getSeries() : 2);
        comboSeries.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        comboSeries.setOnAction(e -> {
            seriesSeleccionadas = comboSeries.getValue();
        });

        Label lblAyuda = new Label("(Vueltas al circuito)");
        lblAyuda.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");

        panelSeries.getChildren().addAll(lblTitSeries, comboSeries, lblAyuda);
        flowPaneEjercicios.getChildren().add(panelSeries);
    }

    @FXML private void volverPaso3() { btnConfigurarDescanso.setVisible(false); avanzarPaso3(); }

    @FXML
    private void volverPaso4() {
        btnConfigurarDescanso.setVisible(true);
        String infoPeso = tipoPesoSeleccionado.equals("Peso Extra") && kilosSeleccionados > 0
                ? " · " + formatKg(kilosSeleccionados) + " kg"
                : "";
        lblTituloConfig.setText("PASO 4: REPETICIONES Y SERIES (" + nivelSeleccionado.toUpperCase() + infoPeso + ")");
        mostrarPaso(paso4Ejercicios);
    }

    @FXML
    private void avanzarPaso5Descanso() {
        try {
            Rutina rutinaActual = mapaRutinas.get(grupoMuscularSeleccionado);
            if (rutinaActual == null) {
                mostrarAlertaError("Error", "No se encontró configuración para: " + grupoMuscularSeleccionado);
                return;
            }

            rutinaActual.getMapaRepeticiones().clear();

            List<String[]> ejerciciosOriginales = obtenerEjerciciosPorFiltro();

            for (int i = 0; i < cajasTextoActuales.size(); i++) {
                TextField tf = cajasTextoActuales.get(i);
                if (tf.getText() == null || tf.getText().trim().isEmpty()) {
                    throw new SeleccionIncompletaException("Debes indicar el número de repeticiones en todos los ejercicios antes de continuar.");
                }
                String nombreEjercicio = ejerciciosOriginales.get(i)[0];
                rutinaActual.getMapaRepeticiones().put(nombreEjercicio, Integer.parseInt(tf.getText().trim()));
            }

            btnConfigurarDescanso.setVisible(false);
            lblTituloConfig.setText("PASO 5: TIEMPO DE DESCANSO (" + grupoMuscularSeleccionado.toUpperCase() + ")");

            txtDescanso.setText(String.valueOf(rutinaActual.getTiempoDescanso()));
            mostrarPaso(paso5Descanso);

        } catch (SeleccionIncompletaException e) {
            mostrarAlertaError("Selección Incompleta", e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlertaError("Formato Incorrecto", "Por favor, ingresa solo números válidos en las repeticiones.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarPaso(javafx.scene.Node pasoVisible) {
        paso1Musculos.setVisible(false); paso1Musculos.setManaged(false);
        paso2Peso.setVisible(false); paso2Peso.setManaged(false);
        paso3Nivel.setVisible(false); paso3Nivel.setManaged(false);
        paso4Ejercicios.setVisible(false); paso4Ejercicios.setManaged(false);
        paso5Descanso.setVisible(false); paso5Descanso.setManaged(false);

        pasoVisible.setVisible(true); pasoVisible.setManaged(true);
    }

    private void cargarImagen(ImageView view, String ruta) {
        try {
            view.setImage(new Image(getClass().getResourceAsStream(ruta)));
        } catch (Exception e) {
            System.err.println("No se encontró: " + ruta);
        }
    }

    private VBox crearMiniTarjetaEjercicio(String nombre, String rutaImagen, String reps) {
        VBox minitarjeta = new VBox(5);
        minitarjeta.setAlignment(Pos.CENTER);
        minitarjeta.setPrefSize(180, 220);
        minitarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        ImageView img = new ImageView();
        cargarImagen(img, rutaImagen);
        img.setFitHeight(100); img.setFitWidth(100); img.setPreserveRatio(true);

        Label lblNombre = new Label(nombre);
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNombre.setWrapText(true);
        lblNombre.setAlignment(Pos.CENTER);

        if (tipoPesoSeleccionado.equals("Peso Extra") && kilosSeleccionados > 0) {
            Label lblKgInfo = new Label("peso: " + formatKg(kilosSeleccionados) + " kg");
            lblKgInfo.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-background-color: #387373;" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 2 8 2 8;" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-weight: bold;"
            );
            minitarjeta.getChildren().addAll(img, lblNombre, lblKgInfo);
        } else {
            minitarjeta.getChildren().addAll(img, lblNombre);
        }

        TextField txtReps = new TextField();
        txtReps.setPromptText(reps);
        txtReps.setPrefWidth(50);
        txtReps.setAlignment(Pos.CENTER);
        txtReps.setText("");

        txtReps.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) txtReps.setText(newVal.replaceAll("[^\\d]", ""));
        });

        HBox hbox = new HBox(8, txtReps, new Label("reps"));
        hbox.setAlignment(Pos.CENTER);

        minitarjeta.getChildren().add(hbox);
        return minitarjeta;
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private List<String[]> obtenerEjerciciosPorFiltro() {
        List<String[]> lista = new ArrayList<>();
        String m = grupoMuscularSeleccionado.toLowerCase();
        String n = nivelSeleccionado.toLowerCase();
        boolean usaMancuernas = tipoPesoSeleccionado.equals("Peso Extra");

        if (m.equals("brazo")) {
            if (!usaMancuernas) { // Peso Propio
                if (n.contains("basico") || n.contains("básico")) {
                    lista.add(new String[]{"Flexiones en pared", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/pared_pre.png", "15"});
                    lista.add(new String[]{"Flexiones cobra", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/flexiones_cobra_pre.png", "12"});
                    lista.add(new String[]{"Plancha antebrazo a mano", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/plancha_ab_pre.png", "12"});
                } else if (n.contains("intermedio") || n.contains("medio")) {
                    lista.add(new String[]{"Flexiones elevadas", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/elevadas_pre.png", "15"});
                    lista.add(new String[]{"Flexiones diamante", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/flex_cerradas_pre.png", "15"});
                    lista.add(new String[]{"Flexiones cerradas", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/flex_cerradas_pre.png", "15"});
                } else if (n.contains("avanzado")) {
                    lista.add(new String[]{"Flexiones a una mano", "/omr/uacm/sistemafitguide/preview/Brazo/pp/avanzado/una_mano_pre.png", "10"});
                    lista.add(new String[]{"Flexiones en pared", "/omr/uacm/sistemafitguide/preview/Brazo/pp/avanzado/pared_abajo_pre.png", "8"});
                    lista.add(new String[]{"Plancha full brazo", "/omr/uacm/sistemafitguide/preview/Brazo/pp/avanzado/plancha_full_pre.png", "8"});
                }
            } else { // Con Peso Extra / Mancuernas 
                if (n.contains("basico") || n.contains("básico")) {
                    lista.add(new String[]{"Caminata", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/granjero_pre.png", "15"});
                    lista.add(new String[]{"Curl de bíceps", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_pre.png", "12"});
                    lista.add(new String[]{"Elevación de antebrazo", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_ante_pre.png", "15"});
                    lista.add(new String[]{"Curl martillo", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_mar_pre.png", "15"});
                } else if (n.contains("intermedio") || n.contains("medio")) {
                    lista.add(new String[]{"Curl sostenido", "/omr/uacm/sistemafitguide/preview/Brazo/pe/medio/curl_sos_pre.png", "15"});
                    lista.add(new String[]{"Curl concentrado", "/omr/uacm/sistemafitguide/preview/Brazo/pe/medio/curl_consent_pre.png", "12"});
                    lista.add(new String[]{"Squeeze", "/omr/uacm/sistemafitguide/preview/Brazo/pe/medio/squeezee.png", "12"});
                    lista.add(new String[]{"Zottman", "/omr/uacm/sistemafitguide/preview/Brazo/pe/medio/zottman_pre.png", "12"});
                } else if (n.contains("avanzado")) {
                    // trabajando
                }
            }
        }
        else if (m.equals("abdomen")) {
            if (!usaMancuernas) { // Peso Propio
                if (n.contains("basico") || n.contains("básico")) {
                    lista.add(new String[]{"Crunch abdominal", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/crunch_pre.png", "15"});
                    lista.add(new String[]{"Elevación de piernas", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/elevacion_pre.png", "15"});
                    lista.add(new String[]{"Plancha", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/plancha_pre.png", "15"});
                } else if (n.contains("intermedio") || n.contains("medio")) {
                    lista.add(new String[]{"V-Ups (Abdominales en V)", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/medio/v_pre.png", "12"});
                    lista.add(new String[]{"Crunch bicicleta", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/medio/bici_pre.png", "15"});
                    lista.add(new String[]{"Plancha con rotacion", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/medio/rot_pre.png", "12"});
                } else if (n.contains("avanzado")) {
                    lista.add(new String[]{"Bandera", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/avanzado/bandera_pre.png", "12"});
                }
            } else { // Con Peso Extra / Mancuernas
                // en preparacion
            }
        }
        else if (m.equals("pierna")) {
            if (!usaMancuernas) { // Peso Propio
                if (n.contains("basico") || n.contains("básico")) {
                    lista.add(new String[]{"Sentadilla", "/omr/uacm/sistemafitguide/preview/Pierna/pp/basico/sentadilla_pre.png", "15"});
                    lista.add(new String[]{"Zancadas", "/omr/uacm/sistemafitguide/preview/Pierna/pp/basico/zancadas_pre.png", "15"});
                } else if (n.contains("intermedio") || n.contains("medio")) {
                    lista.add(new String[]{"Sentadilla búlgara", "/omr/uacm/sistemafitguide/preview/Pierna/pp/medio/sentadilla_bulgara_pre.png", "12"});
                    lista.add(new String[]{"Zancadas con salto", "/omr/uacm/sistemafitguide/preview/Pierna/pp/medio/sentasalto_pre.png", "12"});
                } else if (n.contains("avanzado")) {
                    // En preparación para avanzado de pierna
                }
            } else { // Con Peso Extra / Mancuernas
                // En preparación
            }
        }
        return lista;
    }


    //  GUARDAR DESCANSO Y REDIRECCIÓN 


    @FXML
    private void accionTerminarVolverMenu() {
        if (guardarYValidarDescanso()) {
            try {
                App.setRoot("PantallaBienvenidoUsuario");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void accionTerminarIniciarRutina() {
        if (guardarYValidarDescanso()) {
            try {
                App.setRoot("PantallaListaEjercicios");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean guardarYValidarDescanso() {
        try {
            Rutina rutinaBase = mapaRutinas.get(grupoMuscularSeleccionado);
            if (rutinaBase == null) return false;

            String textoDescanso = txtDescanso.getText().trim();
            if (textoDescanso.isEmpty()) {
                mostrarAlertaError("Campo Vacío", "Por favor, ingresa los segundos de descanso.");
                return false;
            }
            int segundos = Integer.parseInt(textoDescanso);
            if (segundos <= 0) {
                mostrarAlertaError("Valor Inválido", "El tiempo de descanso debe ser mayor a 0 segundos.");
                return false;
            }

            String llaveCompuesta = grupoMuscularSeleccionado + "_" + nivelSeleccionado + "_" + tipoPesoSeleccionado;

            Rutina rutinaNueva = new Rutina(grupoMuscularSeleccionado);
            rutinaNueva.setNivel(nivelSeleccionado);
            rutinaNueva.setTipoPeso(tipoPesoSeleccionado);
            rutinaNueva.setTiempoDescanso(segundos);

            // Se guardan las series en el objeto Rutina
            rutinaNueva.setSeries(seriesSeleccionadas);

            if (tipoPesoSeleccionado.equals("Peso Extra") && kilosSeleccionados > 0) {
                rutinaNueva.setPesoExtraKg(kilosSeleccionados);
            }

            rutinaNueva.setMapaRepeticiones(new java.util.HashMap<>(rutinaBase.getMapaRepeticiones()));

            mapaRutinas.put(llaveCompuesta, rutinaNueva);

            if (Sesion.idUsuarioActivo != -1) {
                new RutinaGuardadaDAO().guardarOActualizar(
                        Sesion.idUsuarioActivo, llaveCompuesta, rutinaNueva
                );
            }


            GestionRutina.getInstance().setMusculoSeleccionado(grupoMuscularSeleccionado);
            GestionRutina.getInstance().setNivelSeleccionado(nivelSeleccionado);
            GestionRutina.getInstance().setUsaMancuernas(tipoPesoSeleccionado.equals("Peso Extra"));
            GestionRutina.getInstance().setLlaveRutina(llaveCompuesta);

            PantallaBienvenidoUsuarioController.rutinaConfigurada = true;
            return true;

        } catch (NumberFormatException e) {
            mostrarAlertaError("Formato Incorrecto", "Por favor, ingresa un número entero válido.");
            return false;
        }
    }
}