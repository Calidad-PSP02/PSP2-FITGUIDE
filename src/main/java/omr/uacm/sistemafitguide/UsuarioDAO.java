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
public class UsuarioDAO {

    //nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_usuario, contrasena, peso, altura, fecha_nacimiento, genero) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setDouble(3, usuario.getPeso());
            pstmt.setDouble(4, usuario.getAltura());
            pstmt.setString(5, usuario.getFechaNacimiento());
            pstmt.setString(6, usuario.getGenero());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al intentar registrar usuario: " + e.getMessage());
            // si el usuario ya existe marca un error.
            return false;
        }
    }

    public int verificarLogin(String nombreUsuario, String contrasena) {
        String sql = "SELECT id_usuario FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?;";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, contrasena);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario"); //retorno el id del usuario
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar inicio de sesión: " + e.getMessage());
        }
        return -1; // credenciales invalidas
    }
}

