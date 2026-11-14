/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author migue
 */
public class HistorialEjercicioDAO {

    //guardmaos la lista completa de los ejercicios
    public void guardarListaEjercicios(List<HistorialEjercicio> lista) {
        String sql = "INSERT INTO historial_ejercicios (id_historial, nombre_ejercicio, reps_objetivo, reps_reales) VALUES (?, ?, ?, ?);";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (HistorialEjercicio ej : lista) {
                pstmt.setInt(1, ej.getIdHistorial());
                pstmt.setString(2, ej.getNombreEjercicio());
                pstmt.setInt(3, ej.getRepsObjetivo());
                pstmt.setInt(4, ej.getRepsReales());
                pstmt.addBatch();
            }

            pstmt.executeBatch(); // ejecutamos todo el lote de golpes
            conn.commit();        // guardamos los cambios de forma permanente
            System.out.println("Desglose de ejercicios guardado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al guardar el desglose de ejercicios: " + e.getMessage());
        }
    }
}