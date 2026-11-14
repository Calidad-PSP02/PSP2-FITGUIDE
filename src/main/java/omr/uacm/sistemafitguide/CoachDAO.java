/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author migue
 */
public class CoachDAO {

    public boolean registrarCoach(Coach coach) {
        String sql = "INSERT INTO coaches (nombre_coach, contrasena, especialidad) VALUES (?, ?, ?);";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, coach.getNombreCoach());
            pstmt.setString(2, coach.getContrasena());
            pstmt.setString(3, coach.getEspecialidad());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar coach en BD: " + e.getMessage());
            return false;
        }
    }

    public int verificarLoginCoach(String nombreCoach, String contrasena) {
        String sql = "SELECT id_coach FROM coaches WHERE nombre_coach = ? AND contrasena = ?;";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreCoach);
            pstmt.setString(2, contrasena);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_coach");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar login de coach: " + e.getMessage());
        }
        return -1;
    }
}

