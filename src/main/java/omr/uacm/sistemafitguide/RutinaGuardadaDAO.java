/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author migue
 */
public class RutinaGuardadaDAO {

    // guarda o actualiza una rutina configurada por el usuario
    public void guardarOActualizar(int idUsuario, String llave, Rutina r) {
        // convertimos el mapa de repeticiones a JSON simple: 
        String repJson = mapaAJson(r.getMapaRepeticiones());

        String sql = "INSERT INTO rutinas_guardadas "
            + "(id_usuario, grupo_muscular, nivel, tipo_peso, series, tiempo_descanso, repeticiones_json, peso_extra_kg) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
            + "ON CONFLICT(id_usuario, grupo_muscular, nivel, tipo_peso) DO UPDATE SET "
            + "series = excluded.series, "
            + "tiempo_descanso = excluded.tiempo_descanso, "
            + "repeticiones_json = excluded.repeticiones_json, "
            + "peso_extra_kg = excluded.peso_extra_kg;";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, r.getGrupoMuscular());
            ps.setString(3, r.getNivel());
            ps.setString(4, r.getTipoPeso());
            ps.setInt(5, r.getSeries());
            ps.setInt(6, r.getTiempoDescanso());
            ps.setString(7, repJson);
            ps.setDouble(8, r.getPesoExtraKg());
            ps.executeUpdate();
            System.out.println("Rutina guardada en BD: " + llave);
        } catch (SQLException e) {
            System.err.println("Error al guardar rutina: " + e.getMessage());
        }
    }

    // carga todas las rutinas del usuario y las mete en el mapaRutinas
    public void cargarRutinasUsuario(int idUsuario) {
        // se limpia el mapa para no mezclar rutinas de otro usuario
        PantallaConfiguracionRutinaController.mapaRutinas.clear();
        PantallaBienvenidoUsuarioController.rutinaConfigurada = false;

        String sql = "SELECT * FROM rutinas_guardadas WHERE id_usuario = ?;";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String grupo    = rs.getString("grupo_muscular");
                    String nivel    = rs.getString("nivel");
                    String tipoPeso = rs.getString("tipo_peso");
                    int series      = rs.getInt("series");
                    int descanso    = rs.getInt("tiempo_descanso");
                    double pesoKg   = rs.getDouble("peso_extra_kg");
                    String repJson  = rs.getString("repeticiones_json");

                    Rutina r = new Rutina(grupo);
                    r.setNivel(nivel);
                    r.setTipoPeso(tipoPeso);
                    r.setSeries(series);
                    r.setTiempoDescanso(descanso);
                    r.setPesoExtraKg(pesoKg);
                    r.setMapaRepeticiones(jsonAMapa(repJson));

                    String llave = grupo + "_" + nivel + "_" + tipoPeso;
                    PantallaConfiguracionRutinaController.mapaRutinas.put(llave, r);
                    System.out.println("Rutina cargada desde BD: " + llave);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar rutinas: " + e.getMessage());
        }
        // solo se puede entrenar si este usuario ya tiene rutinas guardadas
        PantallaBienvenidoUsuarioController.rutinaConfigurada =
            !PantallaConfiguracionRutinaController.mapaRutinas.isEmpty();
    }

    //  utilidades JSON manual

    private String mapaAJson(Map<String, Integer> mapa) {
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

    private Map<String, Integer> jsonAMapa(String json) {
        Map<String, Integer> mapa = new HashMap<>();
        if (json == null || json.equals("{}")) return mapa;
        // quitamos
        String contenido = json.trim().substring(1, json.trim().length() - 1);
        // dividimos por comas que no esten dentro de comillas
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