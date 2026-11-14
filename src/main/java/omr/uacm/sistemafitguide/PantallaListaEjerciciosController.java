/*
         * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
         * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
         */

package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author MiguelDiaz
 */
public class PantallaListaEjerciciosController implements Initializable {


    @FXML private Button btnTabBrazo;
    @FXML private Button btnTabAbdomen;
    @FXML private Button btnTabPierna;
    @FXML private Label  lblMusculoActivo;


    @FXML private HBox  filaCardsPropio;
    @FXML private HBox  filaCardsExtra;
    @FXML private VBox  seccionPesoPropio;
    @FXML private VBox  seccionPesoExtra;
    @FXML private VBox  panelSinRutinas;


    private String musculoActivo = "Brazo";


    private static final String ESTILO_TAB_ACTIVO =
            "-fx-background-color: white; -fx-text-fill: #2E7D32; " +
                    "-fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 20; -fx-cursor: hand;";
    private static final String ESTILO_TAB_INACTIVO =
            "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 20; -fx-cursor: hand;";


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarRutinas("Brazo");
    }



    @FXML public void seleccionarBrazo()   { cambiarTab("Brazo",   btnTabBrazo);   }
    @FXML public void seleccionarAbdomen() { cambiarTab("Abdomen", btnTabAbdomen); }
    @FXML public void seleccionarPierna()  { cambiarTab("Pierna",  btnTabPierna);  }

    private void cambiarTab(String musculo, Button tabActivo) {
        musculoActivo = musculo;
        btnTabBrazo.setStyle(ESTILO_TAB_INACTIVO);
        btnTabAbdomen.setStyle(ESTILO_TAB_INACTIVO);
        btnTabPierna.setStyle(ESTILO_TAB_INACTIVO);
        tabActivo.setStyle(ESTILO_TAB_ACTIVO);
        lblMusculoActivo.setText(musculo);
        cargarRutinas(musculo);
    }

    private void cargarRutinas(String musculo) {
        filaCardsPropio.getChildren().clear();
        filaCardsExtra.getChildren().clear();

        String[] niveles    = {"Básico", "Intermedio", "Avanzado"};
        String[] modalidades = {"Peso Propio", "Peso Extra"};

        int cardsPropio = 0;
        int cardsExtra  = 0;

        for (String modalidad : modalidades) {
            for (String nivel : niveles) {
                String llave = musculo + "_" + nivel + "_" + modalidad;
                Rutina r = PantallaConfiguracionRutinaController.mapaRutinas.get(llave);

                if (r == null) continue; // solo mostrar rutinas configuradas

                // calcular numero de ejercicios base
                boolean esMancuerna = modalidad.equals("Peso Extra");
                List<Ejercicio> base = CatalogoEjercicios.obtenerRutina(
                        musculo, nivel, esMancuerna
                );
                int numEjercicios = (base != null) ? base.size() : 0;

                // construir la imagen 
                String rutaImg = obtenerImagenCard(musculo, nivel, modalidad);

                VBox card = crearCard(musculo, nivel, modalidad, r, numEjercicios, rutaImg, llave);

                if (modalidad.equals("Peso Propio")) {
                    filaCardsPropio.getChildren().add(card);
                    cardsPropio++;
                } else {
                    filaCardsExtra.getChildren().add(card);
                    cardsExtra++;
                }
            }
        }

        // Mostraro cocultar la tarjeta
        seccionPesoPropio.setVisible(cardsPropio > 0);
        seccionPesoPropio.setManaged(cardsPropio > 0);
        seccionPesoExtra.setVisible(cardsExtra > 0);
        seccionPesoExtra.setManaged(cardsExtra > 0);

        boolean sinNada = (cardsPropio + cardsExtra) == 0;
        panelSinRutinas.setVisible(sinNada);
        panelSinRutinas.setManaged(sinNada);
    }


    private VBox crearCard(String musculo, String nivel, String modalidad,
                           Rutina r, int numEjercicios,
                           String rutaImg, String llave) {


        String colorNivel;
        switch (nivel) {
            case "Básico":      colorNivel = "#2E7D32"; break;
            case "Intermedio":  colorNivel = "#E65100"; break;
            default:            colorNivel = "#B71C1C"; break;
        }


        ImageView img = new ImageView();
        img.setFitWidth(210.0);
        img.setFitHeight(130.0);
        img.setPreserveRatio(false);
        try {
            URL res = getClass().getResource(rutaImg);
            if (res != null) img.setImage(new Image(res.toExternalForm()));
        } catch (Exception ignored) {}

        Label chipNivel = new Label(nivel.toUpperCase());
        chipNivel.setStyle("-fx-background-color: " + colorNivel + "; -fx-text-fill: white; " +
                "-fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-background-radius: 6; -fx-padding: 3 10;");

        javafx.scene.layout.StackPane imgPane = new javafx.scene.layout.StackPane(img, chipNivel);
        javafx.scene.layout.StackPane.setAlignment(chipNivel, javafx.geometry.Pos.TOP_LEFT);
        javafx.scene.layout.StackPane.setMargin(chipNivel, new javafx.geometry.Insets(8, 0, 0, 8));
        imgPane.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 14 14 0 0;");


        VBox cuerpo = new VBox(8);
        cuerpo.setStyle("-fx-padding: 12 14 14 14;");

        Label lblTitulo = new Label(musculo + " · " + nivel);
        lblTitulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1B3A2D;");

        boolean esPesoExtra = modalidad.equals("Peso Extra");
        Label chipModal = new Label(esPesoExtra ? " Peso Extra" : " Peso Propio");
        chipModal.setStyle("-fx-background-color: " + (esPesoExtra ? "#FFEBEE" : "#E8F5E9") + "; " +
                "-fx-text-fill: " + (esPesoExtra ? "#C62828" : "#2E7D32") + "; " +
                "-fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-padding: 3 10;");

        // Stats: series · ejercicios · descanso
        HBox stats = new HBox(10);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.getChildren().addAll(
                crearStat(String.valueOf(r.getSeries()), "series"),
                crearStat(String.valueOf(numEjercicios), "ejercicios"),
                crearStat(r.getTiempoDescanso() + "s", "descanso")
        );

        // Botón iniciar
        Button btnIniciar = new Button("▶  Iniciar");
        btnIniciar.setPrefHeight(36.0);
        btnIniciar.setPrefWidth(210.0);
        btnIniciar.setStyle("-fx-background-color: " + colorNivel + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 13px; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        final String llaveF = llave;
        btnIniciar.setOnAction(e -> iniciarRutina(musculo, nivel, modalidad, llaveF));

        cuerpo.getChildren().addAll(lblTitulo, chipModal, stats, btnIniciar);

        VBox card = new VBox(0);
        card.setPrefWidth(210.0);
        card.setMinWidth(210.0);
        card.setMaxWidth(210.0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4);");
        card.getChildren().addAll(imgPane, cuerpo);

        return card;
    }

    private VBox crearStat(String valor, String etiqueta) {
        Label lv = new Label(valor);
        lv.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1B3A2D;");
        Label le = new Label(etiqueta);
        le.setStyle("-fx-font-size: 10px; -fx-text-fill: #777;");
        VBox v = new VBox(1, lv, le);
        v.setAlignment(Pos.CENTER);
        v.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 8; -fx-padding: 6 10;");
        return v;
    }


    private void iniciarRutina(String musculo, String nivel, String modalidad, String llave) {
        Rutina r = PantallaConfiguracionRutinaController.mapaRutinas.get(llave);
        if (r == null) return;

        boolean usaMancuernas = modalidad.equals("Peso Extra");

        GestionRutina gestion = GestionRutina.getInstance();
        gestion.setMusculoSeleccionado(musculo);
        gestion.setNivelSeleccionado(nivel);
        gestion.setUsaMancuernas(usaMancuernas);
        gestion.setLlaveRutina(llave);
        gestion.configurarRutina();

        try {
            App.setRoot("PantallaEjecucion");
        } catch (IOException e) {
            System.err.println("Error al iniciar rutina: " + e.getMessage());
        }
    }


    private String obtenerImagenCard(String musculo, String nivel, String modalidad) {
        String base = "/omr/uacm/sistemafitguide/imagenes/";
        boolean extra = modalidad.equals("Peso Extra");

        switch (musculo.toLowerCase()) {
            case "brazo":
                if (extra) return base + "hombre_brazo_peso_extra.jpg";
                switch (nivel) {
                    case "Básico":     return base + "mujer_brazo_basico_pero_propio.jpg";
                    case "Intermedio": return base + "hombre_brazo_intermedio_peso_propio.jpg";
                    default:           return base + "hombre_brazo_avanzado_peso_propio.jpg";
                }
            case "abdomen":
                if (extra) return base + "mujer_hombre_abdomen_peso_extra.jpg";
                switch (nivel) {
                    case "Básico":     return base + "hombre_abdomen_basico_peso_propio.jpg";
                    case "Intermedio": return base + "hombre_abdomen_intermedio_peso_propio.jpg";
                    default:           return base + "mujer_abdomen_avanzado_peso_propio.jpg";
                }
            case "pierna":
                if (extra) return base + "hombre_pierna_peso_extra.jpg";
                switch (nivel) {
                    case "Básico":     return base + "hombre_pierna_basico_peso_propio.jpg";
                    case "Intermedio": return base + "mujer_pierna_intermedio_peso_propio.jpg";
                    default:           return base + "hombre_pierna_avanzado_peso_propio.jpg";
                }
            default:
                return base + "logo.jpg";
        }
    }


    @FXML
    public void regresarMenu(ActionEvent event) throws IOException {
        App.setRoot("PantallaBienvenidoUsuario");
    }
}
