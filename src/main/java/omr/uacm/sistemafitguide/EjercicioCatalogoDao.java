package omr.uacm.sistemafitguide;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO del catalogo de ejercicios (tabla ejercicios_catalogo).
 * Soporta AMBOS estilos de programación: PascalCase y camelCase (Java Standard).
 *
 * @author FitGuide
 */
public class EjercicioCatalogoDao {

    private static final String COLUMNAS = "id_ejercicio, nombre, zona_corporal, nivel, tipo_peso, repeticiones, series, duracion, descanso, descripcion, url_imagen, url_video";

    // ── Estilo PascalCase ────────────────────────────────────────────────────

    public boolean Agregar(EjercicioCoach ejercicio) {
        String sql = "INSERT INTO ejercicios_catalogo (nombre, zona_corporal, nivel, tipo_peso, repeticiones, series, duracion, descanso, descripcion, url_imagen, url_video) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ejercicio.getNombre());
            pstmt.setString(2, ejercicio.getZonaCorporal());
            pstmt.setString(3, ejercicio.getNivel());
            pstmt.setString(4, ejercicio.getTipoPeso());
            pstmt.setInt(5, ejercicio.getRepeticiones());
            pstmt.setInt(6, ejercicio.getSeries());
            pstmt.setInt(7, ejercicio.getDuracion());
            pstmt.setInt(8, ejercicio.getDescanso());
            pstmt.setString(9, ejercicio.getDescripcion());
            pstmt.setString(10, ejercicio.getUrlImagen());
            pstmt.setString(11, ejercicio.getUrlVideo());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar ejercicio al catalogo: " + e.getMessage());
            return false;
        }
    }

    public boolean Actualizar(EjercicioCoach ejercicio) {
        String sql = "UPDATE ejercicios_catalogo SET nombre = ?, zona_corporal = ?, nivel = ?, tipo_peso = ?, repeticiones = ?, series = ?, duracion = ?, descanso = ?, descripcion = ?, url_imagen = ?, url_video = ? WHERE id_ejercicio = ?;";
        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ejercicio.getNombre());
            pstmt.setString(2, ejercicio.getZonaCorporal());
            pstmt.setString(3, ejercicio.getNivel());
            pstmt.setString(4, ejercicio.getTipoPeso());
            pstmt.setInt(5, ejercicio.getRepeticiones());
            pstmt.setInt(6, ejercicio.getSeries());
            pstmt.setInt(7, ejercicio.getDuracion());
            pstmt.setInt(8, ejercicio.getDescanso());
            pstmt.setString(9, ejercicio.getDescripcion());
            pstmt.setString(10, ejercicio.getUrlImagen());
            pstmt.setString(11, ejercicio.getUrlVideo());
            pstmt.setInt(12, ejercicio.getIdEjercicio());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar ejercicio del catalogo: " + e.getMessage());
            return false;
        }
    }

    public boolean Eliminar(int idEjercicio) {
        String sql = "DELETE FROM ejercicios_catalogo WHERE id_ejercicio = ?;";
        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEjercicio);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar ejercicio del catalogo: " + e.getMessage());
            return false;
        }
    }

    public List<EjercicioCoach> ObtenerTodos() {
        return ObtenerFiltrados("Todos", "Todos", "Todos");
    }

    public List<EjercicioCoach> ObtenerFiltrados(String zona, String nivel, String tipoPeso) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNAS + " FROM ejercicios_catalogo WHERE 1 = 1");
        List<String> parametros = new ArrayList<>();
        agregarFiltro(sql, parametros, "zona_corporal", zona);
        agregarFiltro(sql, parametros, "nivel", nivel);
        agregarFiltro(sql, parametros, "tipo_peso", tipoPeso);
        sql.append(" ORDER BY zona_corporal, nivel, nombre;");
        return consultar(sql.toString(), parametros);
    }

    public List<EjercicioCoach> BuscarPorNombre(String texto) {
        List<String> parametros = new ArrayList<>();
        parametros.add("%" + texto + "%");
        return consultar("SELECT " + COLUMNAS + " FROM ejercicios_catalogo WHERE nombre LIKE ? ORDER BY zona_corporal, nivel, nombre;", parametros);
    }

    // ── Estilo camelCase (Aliases estándar para compatibilidad dual) ─────────

    public boolean agregar(EjercicioCoach ejercicio) { return Agregar(ejercicio); }
    public boolean actualizar(EjercicioCoach ejercicio) { return Actualizar(ejercicio); }
    public boolean eliminar(int idEjercicio) { return Eliminar(idEjercicio); }
    public List<EjercicioCoach> obtenerTodos() { return ObtenerTodos(); }
    public List<EjercicioCoach> obtenerFiltrados(String zona, String nivel, String tipoPeso) { return ObtenerFiltrados(zona, nivel, tipoPeso); }
    public List<EjercicioCoach> buscarPorNombre(String texto) { return BuscarPorNombre(texto); }

    // ── Métodos Privados Auxiliares ──────────────────────────────────────────

    private void agregarFiltro(StringBuilder sql, List<String> parametros, String columna, String valor) {
        if (valor != null && !valor.isEmpty() && !valor.equals("Todos")) {
            sql.append(" AND ").append(columna).append(" = ?");
            parametros.add(valor);
        }
    }

    private List<EjercicioCoach> consultar(String sql, List<String> parametros) {
        List<EjercicioCoach> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setString(i + 1, parametros.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new EjercicioCoach(rs.getInt("id_ejercicio"), rs.getString("nombre"), rs.getString("zona_corporal"), rs.getString("nivel"), rs.getString("tipo_peso"), rs.getInt("repeticiones"), rs.getInt("series"), rs.getInt("duracion"), rs.getInt("descanso"), rs.getString("descripcion"), rs.getString("url_imagen"), rs.getString("url_video")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar el catalogo de ejercicios: " + e.getMessage());
        }
        return lista;
    }
}
