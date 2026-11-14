/*
         * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
         * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
         */
package omr.uacm.sistemafitguide;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author MiguelDiaz
 */
public class PantallaInicioController implements Initializable {

    @FXML private VBox vboxSeleccionRol;

    @FXML private VBox vboxLogin;
    @FXML private Label lblTituloLogin;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField pfContrasenaLogin;
    @FXML private Button btnIngresaSesion;
    @FXML private Hyperlink linkOlvidoContrasena;

    @FXML private VBox vboxRegistro;
    @FXML private TextField txtUsuarioRegistro;
    @FXML private TextField txtPesoRegistro;
    @FXML private TextField txtAlturaRegistro;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private PasswordField pfContrasenaRegistro;
    @FXML private Button btnRegistrarmeEnBD;
    private String generoSeleccionado = "";
    @FXML private Button btnHombre;
    @FXML private Button btnMujer;

    @FXML private VBox vboxRegistroCoach;
    @FXML private TextField txtNombreCoachRegistro;
    @FXML private TextField txtEspecialidadRegistro;
    @FXML private PasswordField pfContrasenaCoachRegistro;
    @FXML private Button btnRegistrarCoach;

    //se instandia lo del usuario DAO
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final CoachDAO coachDAO = new CoachDAO();
    private boolean modoRegistro = false;
    private boolean esCoachSeleccionado = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mostrarPanel(vboxSeleccionRol);
    }

    @FXML
    private void mostrarPantallaLogin(ActionEvent event) {
        modoRegistro = false;
        mostrarPanel(vboxSeleccionRol);
    }

    @FXML
    private void mostrarPantallaRegistro(ActionEvent event) {
        modoRegistro = true;
        mostrarPanel(vboxSeleccionRol);
    }

    @FXML
    private void seleccionarRolUsuario(ActionEvent event) {
        esCoachSeleccionado = false;
        abrirFormularioSegunModo();
    }

    @FXML
    private void seleccionarRolCoach(ActionEvent event) {
        esCoachSeleccionado = true;
        abrirFormularioSegunModo();
    }

    private void abrirFormularioSegunModo() {
        if (modoRegistro) {
            mostrarPanel(esCoachSeleccionado ? vboxRegistroCoach : vboxRegistro);
        } else {
            lblTituloLogin.setText(esCoachSeleccionado ? "Iniciar Sesión (Coach)" : "Iniciar Sesión (Usuario)");
            mostrarPanel(vboxLogin);
        }
    }

    private void mostrarPanel(VBox panelAMostrar) {
        vboxSeleccionRol.setVisible(false);
        vboxLogin.setVisible(false);
        vboxRegistro.setVisible(false);
        vboxRegistroCoach.setVisible(false);
        panelAMostrar.setVisible(true);
    }

    // INICIAR SESION
    @FXML
    private void accionIniciarSesion(ActionEvent event) {
        try {
            String usuario = txtUsuario.getText();
            String contrasena = pfContrasenaLogin.getText();

            // validamos campos vacios
            if (usuario == null || usuario.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty()) {
                throw new CuentaDatosIncompletosException("Faltan datos. Por favor, ingresa tanto tu usuario como tu contraseña.");
            }

            // si eligio coach se valida con CoachDAO
            if (esCoachSeleccionado) {
                iniciarSesionCoach(usuario, contrasena);
                return;
            }

            //consultamos la base de datos 
            int idUsuario = usuarioDAO.verificarLogin(usuario, contrasena);

            // si nos regresa -1 las credenciales  estal mal
            if (idUsuario == -1) {
                throw new CredencialesInvalidasException("Usuario o contraseña incorrectos. Inténtalo de nuevo.");
            }

            // guardamos loa datos del usuario
            Sesion.idUsuarioActivo = idUsuario;
            // cargar las rutinas guardadas de este usuario
            new RutinaGuardadaDAO().cargarRutinasUsuario(idUsuario);
            // si tiene al menos una rutina lo deja pasar
            PantallaBienvenidoUsuarioController.rutinaConfigurada =
                    !PantallaConfiguracionRutinaController.mapaRutinas.isEmpty();
            Sesion.nombreUsuarioActivo = usuario;

            System.out.println("Sesión iniciada. Bienvenido, " + usuario + " (ID: " + idUsuario + ")");

            // Pasa a la siguiente pantalla
            App.setRoot("PantallaBienvenidoUsuario");

        } catch (CuentaDatosIncompletosException e) {
            mostrarAlertaError("Datos Incompletos", e.getMessage());
        } catch (CredencialesInvalidasException e) {
            mostrarAlertaError("Acceso Denegado", e.getMessage());
        } catch (IOException e) {
            mostrarAlertaError("Error de Pantalla", "No se pudo cargar la siguiente pantalla.");
        }
    }

    @FXML
    private void recuperarContraseña(ActionEvent event) {
    }

    // REGISTRO
    @FXML
    private void accionRegistrarmeBaseDatos(ActionEvent event) {
        try {
            String usuario = txtUsuarioRegistro.getText();
            String pesoStr = txtPesoRegistro.getText();
            String alturaStr = txtAlturaRegistro.getText();
            String contrasena = pfContrasenaRegistro.getText();
            LocalDate fechaNacimiento = dpFechaNacimiento.getValue();

            // validmaos campos vacios
            if (usuario == null || usuario.trim().isEmpty() ||
                    pesoStr == null || pesoStr.trim().isEmpty() ||
                    alturaStr == null || alturaStr.trim().isEmpty() ||
                    contrasena == null || contrasena.trim().isEmpty() ||
                    fechaNacimiento == null ||
                    generoSeleccionado.isEmpty()) {
                throw new CuentaDatosIncompletosException("Faltan datos. Por favor, llena todos los campos y selecciona tu género.");
            }

            // Validacion de rango de peso
            double peso = Double.parseDouble(pesoStr);
            if (peso < 20 || peso > 200) {
                throw new PesoFueraDeRangoException("El peso es inválido. Tienes que pesar mínimo 20kg.");
            }

            // Validacion de rango de estatura
            double altura = Double.parseDouble(alturaStr);
            if (altura <= 1.0 || altura > 3.0) {
                throw new EstaturaFueraDeRangoException("La estatura es inválida. Tienes que medir mínimo 1 metro.");
            }

            // Validacion de rango de edad
            LocalDate hoy = LocalDate.now();
            int edad = Period.between(fechaNacimiento, hoy).getYears();

            if (edad < 15 || edad > 100) {
                throw new EdadFueraDeRangoException("Para usar FitGuide debes tener mínimo 15 años.");
            }

            // insertamos los datos para la abse de datos
            //contruimos el usuario con su informacion valida
            Usuario nuevoUsuario = new Usuario(usuario, contrasena, peso, altura, fechaNacimiento.toString(), generoSeleccionado);

            // Intentamos registrarlo a través del DAO
            boolean registradoExitosamente = usuarioDAO.registrarUsuario(nuevoUsuario);

            if (!registradoExitosamente) {
                throw new Exception("El nombre de usuario '" + usuario + "' ya existe o hubo un problema con el registro.");
            }

            System.out.println("Usuario registrado exitosamente en la BD. Iniciando sesión automática...");

            //iniciamos secion automatica para el nuevo usuario
            int idUsuarioNuevo = usuarioDAO.verificarLogin(usuario, contrasena);
            Sesion.idUsuarioActivo = idUsuarioNuevo;
            Sesion.nombreUsuarioActivo = usuario;
            new RutinaGuardadaDAO().cargarRutinasUsuario(idUsuarioNuevo);

            // si todo slae vien pasamos a la siguiente pantalla
            App.setRoot("PantallaBienvenidoUsuario");

        } catch (CuentaDatosIncompletosException e) {
            mostrarAlertaError("Datos de Registro Incompletos", e.getMessage());
        } catch (PesoFueraDeRangoException e) {
            mostrarAlertaError("Error en Peso", e.getMessage());
        } catch (EstaturaFueraDeRangoException e) {
            mostrarAlertaError("Error en Estatura", e.getMessage());
        } catch (EdadFueraDeRangoException e) {
            mostrarAlertaError("Error en Edad", e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlertaError("Formato Incorrecto", "El peso y la altura deben ser valores numéricos (ej. 70.5).");
        } catch (Exception e) {
            mostrarAlertaError("Error de Registro", e.getMessage());
            e.printStackTrace();
        }
    }

    // REGISTRO COACH
    @FXML
    private void accionRegistrarCoach(ActionEvent event) {
        try {
            String nombreCoach = txtNombreCoachRegistro.getText();
            String especialidad = txtEspecialidadRegistro.getText();
            String contrasena = pfContrasenaCoachRegistro.getText();

            if (estaVacio(nombreCoach) || estaVacio(especialidad) || estaVacio(contrasena)) {
                throw new CuentaDatosIncompletosException("Faltan datos. Por favor, llena tu nombre, tu especialidad y tu contraseña.");
            }

            Coach nuevoCoach = new Coach(nombreCoach, contrasena, especialidad);

            if (!coachDAO.registrarCoach(nuevoCoach)) {
                throw new Exception("El nombre de coach '" + nombreCoach + "' ya existe o hubo un problema con el registro.");
            }

            System.out.println("Coach registrado exitosamente en la BD. Iniciando sesión automática...");
            iniciarSesionCoach(nombreCoach, contrasena);

        } catch (CuentaDatosIncompletosException e) {
            mostrarAlertaError("Datos de Registro Incompletos", e.getMessage());
        } catch (Exception e) {
            mostrarAlertaError("Error de Registro", e.getMessage());
        }
    }

    private void iniciarSesionCoach(String nombreCoach, String contrasena) throws CredencialesInvalidasException, IOException {
        int idCoach = coachDAO.verificarLoginCoach(nombreCoach, contrasena);

        if (idCoach == -1) {
            throw new CredencialesInvalidasException("Coach o contraseña incorrectos. Inténtalo de nuevo.");
        }

        Sesion.idCoachActivo = idCoach;
        Sesion.nombreCoachActivo = nombreCoach;
        CoachPanelController.NombreCoach = nombreCoach;
        CoachPanelController.IdCoachActual = idCoach;
        System.out.println("Sesión de coach iniciada. Bienvenido, " + nombreCoach + " (ID: " + idCoach + ")");
        irAPantallaCoach();
    }

    private void irAPantallaCoach() throws IOException {
        if (App.class.getResource("CoachPanel.fxml") == null) {
            mostrarAlertaError("Pantalla no disponible", "La pantalla del coach aún no está disponible.");
            return;
        }
        App.setRoot("CoachPanel");
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    @FXML
    private void seleccionarHombre() {
        generoSeleccionado = "Hombre";
        btnHombre.setStyle("-fx-background-color: #1a5276; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: white; -fx-border-radius: 8; -fx-border-width: 2;");
        btnMujer.setStyle("-fx-background-color: #e91e63; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    @FXML
    private void seleccionarMujer() {
        generoSeleccionado = "Mujer";
        btnMujer.setStyle("-fx-background-color: #78281f; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: white; -fx-border-radius: 8; -fx-border-width: 2;");
        btnHombre.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
