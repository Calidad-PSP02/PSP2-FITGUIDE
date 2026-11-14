/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package omr.uacm.sistemafitguide;
import java.util.List;
import java.util.ArrayList;

/**
 * JavaFX App 
 */
public class GestionRutina {
    private static GestionRutina instancia;
    private List<Ejercicio> listaCargada = new ArrayList<>();

    private int ejercicioActual = 0;
    private int totalRepeticionesReales = 0;
    private String musculoSeleccionado = "";
    private String nivelSeleccionado = "";
    private boolean usaMancuernas = false;
    private String llaveRutina = "";
    private double pesoExtraKg = 0.0;
    private int seriesTotales = 2;
    private int serieActual = 1;

    private GestionRutina() {}

    public static GestionRutina getInstance() {
        if (instancia == null) { instancia = new GestionRutina(); }
        return instancia;
    }

    public void configurarRutina() {

        List<Ejercicio> ejerciciosBase = CatalogoEjercicios.obtenerRutina(musculoSeleccionado, nivelSeleccionado, usaMancuernas);

        // limpiamos todo para iniciar una rutina desde cero
        this.listaCargada.clear();
        this.ejercicioActual = 0;
        this.serieActual = 1;
        this.totalRepeticionesReales = 0;
        this.segundosTranscurridos = 0;

        // obtenemos la configuración que guardaste en el mapa
        Rutina configUsuario = PantallaConfiguracionRutinaController.mapaRutinas.get(llaveRutina);

        if (configUsuario != null) {
            this.pesoExtraKg = configUsuario.getPesoExtraKg();

            // revisamos las series
            if (configUsuario.getSeries() > 0) {
                this.seriesTotales = configUsuario.getSeries();
            }

            // aplicamos las repeticiones personalizadas 
            if (ejerciciosBase != null && configUsuario.getMapaRepeticiones() != null) {
                for (Ejercicio ej : ejerciciosBase) {
                    if (configUsuario.getMapaRepeticiones().containsKey(ej.getNombre())) {
                        ej.setRepeticionesObjetivo(configUsuario.getMapaRepeticiones().get(ej.getNombre()));
                    }
                }
            }
        } else {
            this.seriesTotales = 2;
        }

        if (ejerciciosBase != null && !ejerciciosBase.isEmpty()) {
            for (int s = 0; s < this.seriesTotales; s++) {
                for (Ejercicio ejBase : ejerciciosBase) {
                    Ejercicio copiaEjercicio = new Ejercicio(
                            ejBase.getNombre(),
                            ejBase.getRutavideo(),
                            ejBase.getRutaImagen(),
                            ejBase.getIntrucciones(),
                            ejBase.getRepeticionesObjetivo()
                    );
                    this.listaCargada.add(copiaEjercicio);
                }
            }
        }
    }

    public int getTiempoDescansoConfigurado() {
        Rutina r = PantallaConfiguracionRutinaController.mapaRutinas.get(llaveRutina);
        return (r != null) ? r.getTiempoDescanso() : 30;
    }


    public Ejercicio getEjercicioActualData() {
        if (listaCargada != null && !listaCargada.isEmpty() && ejercicioActual < listaCargada.size()) {
            return listaCargada.get(ejercicioActual);
        }
        return null;
    }


    public Ejercicio getSiguienteEjercicioData() {
        if (listaCargada != null && (ejercicioActual + 1) < listaCargada.size()) {
            return listaCargada.get(ejercicioActual + 1);
        }
        return null;
    }


    public String obtenerSiguientePantalla() {
        ejercicioActual++;


        List<Ejercicio> base = CatalogoEjercicios.obtenerRutina(
                musculoSeleccionado, nivelSeleccionado, usaMancuernas
        );
        if (base != null && !base.isEmpty()) {
            this.serieActual = (ejercicioActual / base.size()) + 1;
        }

        if (ejercicioActual >= getTotalEjercicios()) {
            return "PantallaResumen";
        }
        return "PantallaDescanso";
    }

    public int getTotalEjercicios() { return listaCargada != null ? listaCargada.size() : 0; }

    public List<Ejercicio> getListaCargada() {
        return listaCargada;
    }

    public int getEjercicioActual() { return ejercicioActual + 1; }

    public void avanzarAlSiguienteEjercicio() {
        if (ejercicioActual < getTotalEjercicios()) {
            ejercicioActual++;


            List<Ejercicio> ejerciciosBase = CatalogoEjercicios.obtenerRutina(musculoSeleccionado, nivelSeleccionado, usaMancuernas);
            if (ejerciciosBase != null && !ejerciciosBase.isEmpty()) {
                this.serieActual = (ejercicioActual / ejerciciosBase.size()) + 1;
            }
        }
    }

    public void sumarRepeticiones(int reps) { if (reps > 0) { this.totalRepeticionesReales += reps; } }
    public int getTotalRepeticionesReales() { return totalRepeticionesReales; }
    public void setSeriesTotales(int seriesTotales) { this.seriesTotales = seriesTotales; }
    public int getSeriesTotales() { return seriesTotales; }
    public int getSerieActual() { return Math.min(serieActual, seriesTotales); }
    public void reiniciarRutina() {
        this.ejercicioActual = 0;
        this.serieActual = 1;
        this.totalRepeticionesReales = 0;
        this.listaCargada.clear();
        this.pesoExtraKg = 0.0;
    }

    public String getMusculoSeleccionado() { return musculoSeleccionado; }
    public String getNivelSeleccionado() { return nivelSeleccionado; }
    public boolean isUsaMancuernas() { return usaMancuernas; }
    public void setMusculoSeleccionado(String m) { this.musculoSeleccionado = m; }
    public void setNivelSeleccionado(String n) { this.nivelSeleccionado = n; }
    public void setUsaMancuernas(boolean u) { this.usaMancuernas = u; }
    public void setLlaveRutina(String l) { this.llaveRutina = l; }
    public String getLlaveRutina() { return llaveRutina; }

    public double getPesoExtraKg() { return pesoExtraKg; }
    public void setPesoExtraKg(double pesoExtraKg) { this.pesoExtraKg = pesoExtraKg; }
    //cronometro
    private int segundosTranscurridos = 0;

    public void iniciarCronometroGlobal() {
        segundosTranscurridos = 0; //reinicia al empezar rutina
    }
    public void sumarSegundo() {
        segundosTranscurridos++;
    }
    public int getSegundosTranscurridos() {
        return segundosTranscurridos;
    }
}