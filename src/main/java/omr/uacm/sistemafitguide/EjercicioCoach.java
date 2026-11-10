package omr.uacm.sistemafitguide;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modelo POJO con JavaFX Properties para representar un ejercicio del catálogo
 * persistido en la tabla ejercicios_catalogo.
 * Soporta AMBOS estilos de programación: PascalCase y camelCase (Java Standard).
 *
 * @author Coach Module — FitGuide
 */
public class EjercicioCoach {

    private final IntegerProperty idEjercicio = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty zonaCorporal = new SimpleStringProperty();
    private final StringProperty nivel = new SimpleStringProperty();
    private final StringProperty tipoPeso = new SimpleStringProperty();
    private final IntegerProperty repeticiones = new SimpleIntegerProperty();
    private final IntegerProperty series = new SimpleIntegerProperty();
    private final IntegerProperty duracion = new SimpleIntegerProperty();
    private final IntegerProperty descanso = new SimpleIntegerProperty();
    private final StringProperty descripcion = new SimpleStringProperty();
    private final StringProperty urlImagen = new SimpleStringProperty();
    private final StringProperty urlVideo = new SimpleStringProperty();

    // ── Constructores (PascalCase) ───────────────────────────────────────────

    public EjercicioCoach() {
    }

    public EjercicioCoach(int idEjercicio, String nombre, String zonaCorporal, String nivel,
                          String tipoPeso, int repeticiones, int series, int duracion,
                          int descanso, String descripcion, String urlImagen, String urlVideo) {
        this.idEjercicio.set(idEjercicio);
        this.nombre.set(nombre);
        this.zonaCorporal.set(zonaCorporal);
        this.nivel.set(nivel);
        this.tipoPeso.set(tipoPeso);
        this.repeticiones.set(repeticiones);
        this.series.set(series);
        this.duracion.set(duracion);
        this.descanso.set(descanso);
        this.descripcion.set(descripcion);
        this.urlImagen.set(urlImagen);
        this.urlVideo.set(urlVideo);
    }

    // Constructor sin ID (para nuevos ejercicios antes de insertar)
    public EjercicioCoach(String nombre, String zonaCorporal, String nivel,
                          String tipoPeso, int repeticiones, int series, int duracion,
                          int descanso, String descripcion, String urlImagen, String urlVideo) {
        this(0, nombre, zonaCorporal, nivel, tipoPeso, repeticiones, series,
             duracion, descanso, descripcion, urlImagen, urlVideo);
    }

    // ── ESTILO 1: camelCase (Estándar Java & JavaFX Properties) ──────────────

    public int getIdEjercicio() { return idEjercicio.get(); }
    public void setIdEjercicio(int value) { idEjercicio.set(value); }
    public IntegerProperty idEjercicioProperty() { return idEjercicio; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String value) { nombre.set(value); }
    public StringProperty nombreProperty() { return nombre; }

    public String getZonaCorporal() { return zonaCorporal.get(); }
    public void setZonaCorporal(String value) { zonaCorporal.set(value); }
    public StringProperty zonaCorporalProperty() { return zonaCorporal; }

    public String getNivel() { return nivel.get(); }
    public void setNivel(String value) { nivel.set(value); }
    public StringProperty nivelProperty() { return nivel; }

    public String getTipoPeso() { return tipoPeso.get(); }
    public void setTipoPeso(String value) { tipoPeso.set(value); }
    public StringProperty tipoPesoProperty() { return tipoPeso; }

    public int getRepeticiones() { return repeticiones.get(); }
    public void setRepeticiones(int value) { repeticiones.set(value); }
    public IntegerProperty repeticionesProperty() { return repeticiones; }

    public int getSeries() { return series.get(); }
    public void setSeries(int value) { series.set(value); }
    public IntegerProperty seriesProperty() { return series; }

    public int getDuracion() { return duracion.get(); }
    public void setDuracion(int value) { duracion.set(value); }
    public IntegerProperty duracionProperty() { return duracion; }

    public int getDescanso() { return descanso.get(); }
    public void setDescanso(int value) { descanso.set(value); }
    public IntegerProperty descansoProperty() { return descanso; }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String value) { descripcion.set(value); }
    public StringProperty descripcionProperty() { return descripcion; }

    public String getUrlImagen() { return urlImagen.get(); }
    public void setUrlImagen(String value) { urlImagen.set(value); }
    public StringProperty urlImagenProperty() { return urlImagen; }

    public String getUrlVideo() { return urlVideo.get(); }
    public void setUrlVideo(String value) { urlVideo.set(value); }
    public StringProperty urlVideoProperty() { return urlVideo; }

    // ── ESTILO 2: PascalCase (UpperCamelCase para métodos y propiedades) ─────

    public int GetIdEjercicio() { return getIdEjercicio(); }
    public void SetIdEjercicio(int value) { setIdEjercicio(value); }

    public String GetNombre() { return getNombre(); }
    public void SetNombre(String value) { setNombre(value); }

    public String GetZonaCorporal() { return getZonaCorporal(); }
    public void SetZonaCorporal(String value) { setZonaCorporal(value); }

    public String GetNivel() { return getNivel(); }
    public void SetNivel(String value) { setNivel(value); }

    public String GetTipoPeso() { return getTipoPeso(); }
    public void SetTipoPeso(String value) { setTipoPeso(value); }

    public int GetRepeticiones() { return getRepeticiones(); }
    public void SetRepeticiones(int value) { setRepeticiones(value); }

    public int GetSeries() { return getSeries(); }
    public void SetSeries(int value) { setSeries(value); }

    public int GetDuracion() { return getDuracion(); }
    public void SetDuracion(int value) { setDuracion(value); }

    public int GetDescanso() { return getDescanso(); }
    public void SetDescanso(int value) { setDescanso(value); }

    public String GetDescripcion() { return getDescripcion(); }
    public void SetDescripcion(String value) { setDescripcion(value); }

    public String GetUrlImagen() { return getUrlImagen(); }
    public void SetUrlImagen(String value) { setUrlImagen(value); }

    public String GetUrlVideo() { return getUrlVideo(); }
    public void SetUrlVideo(String value) { setUrlVideo(value); }

    @Override
    public String toString() {
        return getNombre() + " [" + getZonaCorporal() + " / " + getNivel() + "]";
    }
}
