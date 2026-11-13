/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    public static final String FORMATO_CSV = "csv";
    public static final String FORMATO_TXT = "txt";

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

    // HU-01: exporta el historial completo (rutinas + ejercicios) de un usuario
    // a un archivo CSV o TXT. Las excepciones se propagan para que la pantalla
    // le muestre un mensaje al usuario.
    public void exportarHistorial(int idUsuario, String nombreUsuario, File archivo, String formato)
            throws SQLException, IOException, HistorialVacioException {
        String sql = "SELECT r.id_historial, r.fecha, r.grupo_muscular, r.nivel, r.duracion_segundos, "
            + "e.nombre_ejercicio, e.reps_objetivo, e.reps_reales "
            + "FROM historial_rutinas r "
            + "LEFT JOIN historial_ejercicios e ON e.id_historial = r.id_historial "
            + "WHERE r.id_usuario = ? "
            + "ORDER BY r.fecha, r.id_historial, e.id_detalle;";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                // si no hay filas se avisa antes de crear el archivo
                if (!rs.next()) {
                    throw new HistorialVacioException();
                }
                try (Writer escritor = Files.newBufferedWriter(archivo.toPath(), StandardCharsets.UTF_8)) {
                    if (FORMATO_TXT.equalsIgnoreCase(formato)) {
                        escribirTxt(rs, escritor, nombreUsuario);
                    } else {
                        escribirCsv(rs, escritor);
                    }
                }
            }
        }
    }

    // el ResultSet llega posicionado en la primera fila
    void escribirCsv(ResultSet rs, Writer escritor) throws SQLException, IOException {
        escritor.write('\uFEFF'); // BOM para que Excel respete los acentos
        escritor.write("Sesión,Fecha,Grupo muscular,Nivel,Duración (seg),Ejercicio,"
            + "Reps objetivo (por serie),Reps reales (total)\r\n");
        do {
            escritor.write(rs.getInt("id_historial") + ","
                + campoCsv(rs.getString("fecha")) + ","
                + campoCsv(rs.getString("grupo_muscular")) + ","
                + campoCsv(rs.getString("nivel")) + ","
                + rs.getInt("duracion_segundos") + ","
                + campoCsv(rs.getString("nombre_ejercicio")) + ","
                + enteroComoTexto(rs, "reps_objetivo") + ","
                + enteroComoTexto(rs, "reps_reales") + "\r\n");
        } while (rs.next());
    }

    // el ResultSet llega posicionado en la primera fila
    void escribirTxt(ResultSet rs, Writer escritor, String nombreUsuario) throws SQLException, IOException {
        String salto = System.lineSeparator();
        escritor.write("FITGUIDE - Historial de entrenamientos" + salto);
        escritor.write("Usuario: " + nombreUsuario + salto);
        escritor.write("Exportado el: " + LocalDate.now() + salto);

        int sesionActual = -1;
        do {
            int idHistorial = rs.getInt("id_historial");
            if (idHistorial != sesionActual) {
                sesionActual = idHistorial;
                int segundos = rs.getInt("duracion_segundos");
                escritor.write(salto + "Sesión " + idHistorial + " | " + rs.getString("fecha")
                    + " | " + rs.getString("grupo_muscular") + " | " + rs.getString("nivel")
                    + " | " + String.format("%02d:%02d min", segundos / 60, segundos % 60) + salto);
            }
            String ejercicio = rs.getString("nombre_ejercicio");
            if (ejercicio != null) {
                escritor.write("   - " + ejercicio + ": objetivo " + rs.getInt("reps_objetivo")
                    + " (por serie), reales " + rs.getInt("reps_reales") + " (total)" + salto);
            }
        } while (rs.next());
    }

    // entrecomilla los campos con coma, comillas o saltos de línea
    // y neutraliza fórmulas (=, +, -, @) al abrir el CSV en Excel
    static String campoCsv(String valor) {
        if (valor == null) return "";
        String texto = valor;
        if (!texto.isEmpty() && "=+-@".indexOf(texto.charAt(0)) >= 0) {
            texto = "'" + texto;
        }
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n") || texto.contains("\r")) {
            texto = "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    // una sesión sin ejercicios registrados deja la celda vacía (no 0)
    private String enteroComoTexto(ResultSet rs, String columna) throws SQLException {
        int valor = rs.getInt(columna);
        return rs.wasNull() ? "" : String.valueOf(valor);
    }
}