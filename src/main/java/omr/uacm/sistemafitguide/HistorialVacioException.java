/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;

/**
 * Se lanza al exportar el historial de un usuario que aún no tiene
 * entrenamientos registrados.
 *
 * @author migue
 */
public class HistorialVacioException extends Exception {
    public HistorialVacioException() {
        super("Aún no tienes entrenamientos registrados. Completa una rutina para poder exportar tu historial.");
    }
}