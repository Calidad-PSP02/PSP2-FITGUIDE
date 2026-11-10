package omr.uacm.sistemafitguide;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para que el Coach pueda consultar y editar las rutinas de los usuarios.
 * Permite implementar entrenamiento progresivo modificando series, reps, descanso y peso.
 * Soporta AMBOS estilos de programación: PascalCase y camelCase (Java Standard).
 *
 * @author Coach Module — FitGuide
 */
public class CoachRutinaDao {

    // ═════════════════════════════════════════════════════════════════════════
    //  CONSULTAR USUARIOS (Ambos Estilos)
    // ═════════════════════════════════════════════════════════════════════════

    /** PascalCase */
    public List<Usuario> ObtenerUsuarios() {
        String sql = "SELECT id_usuario, nombre_usuario, peso, altura, fecha_nacimiento, genero FROM usuarios ORDER BY nombre_usuario;";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setPeso(rs.getDouble("peso"));
                u.setAltura(rs.getDouble("altura"));
                u.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                u.setGenero(rs.getString("genero"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    /** camelCase (alias) */
    public List<Usuario> obtenerUsuarios() {
        return ObtenerUsuarios();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CONSULTAR RUTINAS DE UN USUARIO (Ambos Estilos)
    // ═════════════════════════════════════════════════════════════════════════

    /** PascalCase */
    public List<Rutina> ObtenerRutinasDeUsuario(int idUsuario) {
        String sql = "SELECT * FROM rutinas_guardadas WHERE id_usuario = ? ORDER BY grupo_muscular, nivel;";
        List<Rutina> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rutina rutina = new Rutina(rs.getString("grupo_muscular"));
                    rutina.setNivel(rs.getString("nivel"));
                    rutina.setTipoPeso(rs.getString("tipo_peso"));
                    rutina.setSeries(rs.getInt("series"));
                    rutina.setTiempoDescanso(rs.getInt("tiempo_descanso"));
                    rutina.setPesoExtraKg(rs.getDouble("peso_extra_kg"));
                    rutina.setMapaRepeticiones(JsonAMapa(rs.getString("repeticiones_json")));
                    lista.add(rutina);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener rutinas del usuario " + idUsuario + ": " + e.getMessage());
        }
        return lista;
    }

    /** camelCase (alias) */
    public List<Rutina> obtenerRutinasDeUsuario(int idUsuario) {
        return ObtenerRutinasDeUsuario(idUsuario);
    }

    /** PascalCase */
    public Rutina ObtenerDetalleRutina(int idUsuario, String grupoMuscular, String nivel, String tipoPeso) {
        String sql = "SELECT * FROM rutinas_guardadas WHERE id_usuario = ? AND grupo_muscular = ? AND nivel = ? AND tipo_peso = ?;";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, grupoMuscular);
            ps.setString(3, nivel);
            ps.setString(4, tipoPeso);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rutina rutina = new Rutina(rs.getString("grupo_muscular"));
                    rutina.setNivel(rs.getString("nivel"));
                    rutina.setTipoPeso(rs.getString("tipo_peso"));
                    rutina.setSeries(rs.getInt("series"));
                    rutina.setTiempoDescanso(rs.getInt("tiempo_descanso"));
                    rutina.setPesoExtraKg(rs.getDouble("peso_extra_kg"));
                    rutina.setMapaRepeticiones(JsonAMapa(rs.getString("repeticiones_json")));
                    return rutina;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalle de rutina: " + e.getMessage());
        }
        return null;
    }

    /** camelCase (alias) */
    public Rutina obtenerDetalleRutina(int idUsuario, String grupoMuscular, String nivel, String tipoPeso) {
        return ObtenerDetalleRutina(idUsuario, grupoMuscular, nivel, tipoPeso);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ACTUALIZAR RUTINA (PROGRESIÓN) (Ambos Estilos)
    // ═════════════════════════════════════════════════════════════════════════

    /** PascalCase */
    public boolean ActualizarRutinaProgresion(int idUsuario, Rutina rutina) {
        String sql = "UPDATE rutinas_guardadas SET "
                + "series = ?, tiempo_descanso = ?, peso_extra_kg = ?, repeticiones_json = ? "
                + "WHERE id_usuario = ? AND grupo_muscular = ? AND nivel = ? AND tipo_peso = ?;";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rutina.getSeries());
            ps.setInt(2, rutina.getTiempoDescanso());
            ps.setDouble(3, rutina.getPesoExtraKg());
            ps.setString(4, MapaAJson(rutina.getMapaRepeticiones()));
            ps.setInt(5, idUsuario);
            ps.setString(6, rutina.getGrupoMuscular());
            ps.setString(7, rutina.getNivel());
            ps.setString(8, rutina.getTipoPeso());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Rutina actualizada para progresión del usuario " + idUsuario);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar rutina para progresión: " + e.getMessage());
        }
        return false;
    }

    /** camelCase (alias) */
    public boolean actualizarRutinaProgresion(int idUsuario, Rutina rutina) {
        return ActualizarRutinaProgresion(idUsuario, rutina);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  UTILIDADES JSON (compatibles con RutinaGuardadaDAO existente)
    // ═════════════════════════════════════════════════════════════════════════

    private String MapaAJson(Map<String, Integer> mapa) {
        if (mapa == null || mapa.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, Integer> entry : mapa.entrySet()) {
            String nombre = entry.getKey().replace("\"", "\\\"");
            sb.append("\"").append(nombre).append("\":").append(entry.getValue()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    private Map<String, Integer> JsonAMapa(String json) {
        Map<String, Integer> mapa = new HashMap<>();
        if (json == null || json.equals("{}")) return mapa;
        String contenido = json.trim().substring(1, json.trim().length() - 1);
        String[] pares = contenido.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String par : pares) {
            String[] kv = par.split(":", 2);
            if (kv.length == 2) {
                String nombre = kv[0].trim().replace("\"", "");
                int reps = Integer.parseInt(kv[1].trim());
                mapa.put(nombre, reps);
            }
        }
        return mapa;
    }
}
