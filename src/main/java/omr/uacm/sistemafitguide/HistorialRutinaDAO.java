/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author migue
 */
public class HistorialRutinaDAO {

   //recuerda las fechas donde el usuario termino su rutina
    public Set<LocalDate> obtenerFechasEntrenamiento(int idUsuario) {
        Set<LocalDate> fechas = new HashSet<>();
        String sql = "SELECT fecha FROM historial_rutinas WHERE id_usuario = ?;";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String fechaStr = rs.getString("fecha");
                    if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                        fechas.add(LocalDate.parse(fechaStr));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener fechas de entrenamiento: " + e.getMessage());
        }
        return fechas;
    }

    public int insertarHistorialRutina(HistorialRutina rutina) {
    String sql = "INSERT INTO historial_rutinas "
        + "(id_usuario, fecha, grupo_muscular, nivel, duracion_segundos) "
        + "VALUES (?, ?, ?, ?, ?);";

    try (Connection conn = ConexionDB.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql,
             java.sql.Statement.RETURN_GENERATED_KEYS)) {

        GestionRutina g = GestionRutina.getInstance();

        pstmt.setInt(1, rutina.getIdUsuario());
        pstmt.setString(2, rutina.getFecha());
        pstmt.setString(3, g.getMusculoSeleccionado());  
        pstmt.setString(4, g.getNivelSeleccionado());    
        pstmt.setInt(5, rutina.getDuracionSegundos());

        int filasAfectadas = pstmt.executeUpdate();

        if (filasAfectadas > 0) {
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al insertar en historial_rutinas: " + e.getMessage());
    }
    return -1;
}
    
   //calcula cuantos dias lleva de racha
    public int calcularRachaActual(int idUsuario) {
        Set<LocalDate> fechas = obtenerFechasEntrenamiento(idUsuario);
        if (fechas.isEmpty()) return 0;

        int racha = 0;
        LocalDate diaAVerificar = LocalDate.now();

        //si hoy no tuvo racha vemos su dia anterior para ver si termino
        if (!fechas.contains(diaAVerificar)) {
            diaAVerificar = diaAVerificar.minusDays(1);
        }

        // cuenta hacia atrjas de forma consecutina. 
        while (fechas.contains(diaAVerificar)) {
            racha++;
            diaAVerificar = diaAVerificar.minusDays(1);
        }

        return racha;
    }
}