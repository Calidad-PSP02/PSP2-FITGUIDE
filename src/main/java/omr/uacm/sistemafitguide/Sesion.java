/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;

/**
 *
 * @author migue
 */
public class Sesion {
    // variables estaticas para guardar los usuario
    public static int idUsuarioActivo = -1;
    public static String nombreUsuarioActivo = "";
    public static int idCoachActivo = -1;
    public static String nombreCoachActivo = "";

    public static void cerrarSesion() {
        idUsuarioActivo = -1;
        nombreUsuarioActivo = "";
        idCoachActivo = -1;
        nombreCoachActivo = "";
    }
}
