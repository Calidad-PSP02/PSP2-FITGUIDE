/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author migue
 */
public class ConexionDB {

    private static final String URL = "jdbc:sqlite:fitguide.db";

    //conexion activa
    public static Connection conectar() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        // soporte fisico para las llaves foraneas
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    // incersion de las datos
    public static void inicializarBaseDatos() {
        String tablaUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre_usuario TEXT UNIQUE NOT NULL, "
                + "contrasena TEXT NOT NULL, "
                + "peso REAL NOT NULL, "
                + "altura REAL NOT NULL, "
                + "fecha_nacimiento TEXT NOT NULL, "
                + "genero TEXT NOT NULL"
                + ");";

        String tablaHistorialRutinas = "CREATE TABLE IF NOT EXISTS historial_rutinas ("
                + "id_historial INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "id_usuario INTEGER NOT NULL, "
                + "fecha TEXT NOT NULL, "
                + "grupo_muscular TEXT NOT NULL, "
                + "nivel TEXT NOT NULL, "
                + "duracion_segundos INTEGER NOT NULL DEFAULT 0, "  // ← línea nueva
                + "FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON DELETE CASCADE"
                + ");";

        String tablaHistorialEjercicios = "CREATE TABLE IF NOT EXISTS historial_ejercicios ("
                + "id_detalle INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "id_historial INTEGER NOT NULL, "
                + "nombre_ejercicio TEXT NOT NULL, "
                + "reps_objetivo INTEGER NOT NULL, "
                + "reps_reales INTEGER NOT NULL, "
                + "FOREIGN KEY (id_historial) REFERENCES historial_rutinas (id_historial) ON DELETE CASCADE"
                + ");";

        String tablaRutinasGuardadas = "CREATE TABLE IF NOT EXISTS rutinas_guardadas ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "id_usuario INTEGER NOT NULL, "
                + "grupo_muscular TEXT NOT NULL, "
                + "nivel TEXT NOT NULL, "
                + "tipo_peso TEXT NOT NULL, "
                + "series INTEGER NOT NULL, "
                + "tiempo_descanso INTEGER NOT NULL, "
                + "repeticiones_json TEXT NOT NULL, "
                + "peso_extra_kg REAL NOT NULL DEFAULT 0, "
                + "UNIQUE(id_usuario, grupo_muscular, nivel, tipo_peso), "
                + "FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON DELETE CASCADE"
                + ");";

        String tablaCoaches = "CREATE TABLE IF NOT EXISTS coaches ("
                + "id_coach INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre_coach TEXT UNIQUE NOT NULL, "
                + "contrasena TEXT NOT NULL, "
                + "especialidad TEXT NOT NULL"
                + ");";

        String tablaEjerciciosCatalogo = "CREATE TABLE IF NOT EXISTS ejercicios_catalogo ("
                + "id_ejercicio INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL, "
                + "zona_corporal TEXT NOT NULL, "
                + "nivel TEXT NOT NULL, "
                + "tipo_peso TEXT NOT NULL, "
                + "repeticiones INTEGER NOT NULL DEFAULT 12, "
                + "series INTEGER NOT NULL DEFAULT 3, "
                + "duracion INTEGER NOT NULL DEFAULT 0, "
                + "descanso INTEGER NOT NULL DEFAULT 30, "
                + "descripcion TEXT, "
                + "url_imagen TEXT, "
                + "url_video TEXT"
                + ");";

        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(tablaUsuarios);
            stmt.execute(tablaHistorialRutinas);
            stmt.execute(tablaHistorialEjercicios);
            stmt.execute(tablaRutinasGuardadas);
            stmt.execute(tablaCoaches);
            stmt.execute(tablaEjerciciosCatalogo);
            System.out.println("Base de datos SQLite verificada e inicializada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error crítico al inicializar la base de datos local: " + e.getMessage());
        }
    }
}