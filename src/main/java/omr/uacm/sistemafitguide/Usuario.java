
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package omr.uacm.sistemafitguide;

/**
 *
 * @author migue
 */
public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String contrasena;
    private double peso;
    private double altura;
    private String fechaNacimiento;
    private String genero;

    // constructor vacío
    public Usuario() {
    }

    // constructor para registrar un usuario
    public Usuario(String nombreUsuario, String contrasena, double peso, double altura, String fechaNacimiento, String genero) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.peso = peso;
        this.altura = altura;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
    }


    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
}

