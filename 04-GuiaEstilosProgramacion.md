# 📐 Guía de Estándares de Codificación — FitGuide (SQA / PSP1)

**Proyecto:** Sistema FitGuide  
**Materia/Metodología:** SQA / Personal Software Process (PSP1)  
**Fecha:** Octubre 2026  

---

## 1. Implementación de PascalCase en el Proyecto

**PascalCase** (también conocido como *UpperCamelCase*) es una convención de nomenclatura donde cada palabra de un identificador compuesto comienza con una letra mayúscula, sin espacios ni guiones bajos.

### 1.1 Elementos en PascalCase en FitGuide

| Elemento | Convención | Estado en FitGuide | Ejemplos del Proyecto |
|---|---|---|---|
| **Clases de Dominio / Modelo** | PascalCase | ✅ Implementado | `EjercicioCoach`, `Ejercicio`, `Rutina`, `Usuario`, `HistorialRutina` |
| **Controladores JavaFX** | PascalCase | ✅ Implementado | `PantallaCoachMenuController`, `PantallaCoachCatalogoEjerciciosController`, `PantallaCoachGestionRutinasController`, `PantallaInicioController` |
| **DAOs (Data Access Objects)** | PascalCase | ✅ Implementado | `EjercicioCatalogoDao`, `CoachRutinaDao`, `UsuarioDAO`, `RutinaGuardadaDAO` |
| **Excepciones Personalizadas** | PascalCase | ✅ Implementado | `CredencialesInvalidasException`, `DescansoFueraDeRangoException`, `SeleccionIncompletaException` |
| **Vistas FXML** | PascalCase | ✅ Implementado | `PantallaCoachMenu.fxml`, `PantallaCoachCatalogoEjercicios.fxml`, `PantallaCoachGestionRutinas.fxml` |
| **Métodos Públicos del Módulo Coach** | PascalCase (+ alias) | ✅ Implementado | `ObtenerTodos()`, `Agregar()`, `Actualizar()`, `Eliminar()`, `CargarEjercicios()`, `EditarRutinaProgresion()` |

---

## 2. Advertencia Técnica Crítica: PascalCase en Java y JavaFX

> [!WARNING]
> En Java y JavaFX, aplicar PascalCase de forma indiscriminada a **todos** los identificadores puede provocar fallas en tiempo de ejecución:
>
> 1. **Java Beans y JavaFX Properties:**
>    Los componentes de JavaFX como `TableView` y `PropertyValueFactory` dependen de la especificación Java Beans. Buscan métodos que comiencen con minúscula:
>    - `getNombre()` / `setNombre()`
>    - `nombreProperty()`
>    Si se renombran a `GetNombre()`, JavaFX no los encuentra por introspección y las tablas quedan vacías.
>
> 2. **Métodos de Interfaces y Clases Base del JDK:**
>    Métodos sobreescritos (`@Override`) de la biblioteca estándar de Java **no** pueden cambiarse a PascalCase porque romperían el contrato de la interfaz:
>    - `start(Stage stage)` (de `javafx.application.Application`)
>    - `initialize(URL url, ResourceBundle rb)` (de `javafx.fxml.Initializable`)
>    - `toString()`, `equals(Object o)` (de `java.lang.Object`)
>
> 3. **Solución Implementada en el Módulo Coach:**
>    Se implementaron los métodos de lógica de negocio y controladores en **PascalCase** (`ObtenerTodos()`, `BuscarPorNombre()`, `Agregar()`, `EditarRutinaProgresion()`), manteniendo además los getters/setters compatibles con JavaFX (`getIdEjercicio()`, `idEjercicioProperty()`) para garantizar que la interfaz gráfica funcione al 100%.

---

## 3. ¿Qué Otros Estilos de Programación Recomendamos?

### 🥇 Opción 1 (Altamente Recomendada): **Google Java Style / Oracle Standard**
Es el estándar por excelencia de la industria de software para Java:

```java
// Clase en PascalCase
public class CatalogoEjerciciosService {

    // Constante en UPPER_SNAKE_CASE
    private static final int MAX_REPETICIONES = 50;

    // Campo privado en camelCase
    private List<Ejercicio> listaEjercicios;

    // Método en camelCase
    public List<Ejercicio> obtenerEjerciciosPorNivel(String nivelSeleccionado) {
        int contadorEjercicios = 0; // Variable local en camelCase
        return listaEjercicios;
    }
}
```

* **Ventajas:**
  - 100% compatible con todos los frameworks (Spring, JavaFX, Hibernate, JUnit).
  - Todos los IDEs (IntelliJ IDEA, Eclipse, NetBeans) tienen formateadores automáticos para este estilo.
  - Es el estándar exigido en la industria laboral de Java.

---

### 🥈 Opción 2: **Notación Húngara para Componentes UI (JavaFX)**
Muy recomendada en proyectos universitarios y de SQA porque elimina ambigüedades al inspeccionar controladores FXML:

| Tipo de Control UI | Prefijo | Ejemplo en FitGuide |
|---|---|---|
| `Button` | `btn` | `btnGuardar`, `btnEditarProgresion`, `btnVolverMenu` |
| `TextField` | `txt` | `txtNombre`, `txtBusqueda`, `txtDescanso` |
| `Label` | `lbl` | `lblTitulo`, `lblUsuarioSeleccionado`, `lblTotal` |
| `ComboBox` | `cmb` | `cmbFiltroZona`, `cmbFiltroNivel` |
| `TableView` | `tabla` / `tbl` | `tablaEjercicios`, `tablaUsuarios`, `tablaRutinas` |
| `TableColumn` | `col` | `colId`, `colNombre`, `colSeries`, `colDescanso` |
| `Spinner` | `sp` | `spSeries`, `spDescanso` |

---

### 🥉 Opción 3: **Estilo Híbrido C# / .NET (Si su profesor exige PascalCase en métodos)**
Si el docente o la rúbrica de SQA exige explícitamente métodos en PascalCase:

- Clases: `PascalCase` (`EjercicioCoach`)
- Métodos públicos: `PascalCase` (`ObtenerTodos()`, `GuardarRutina()`)
- Variables privadas y parámetros: `camelCase` o `_camelCase` (`idUsuario`, `_conexion`)
- Constantes: `UPPER_SNAKE_CASE` (`URL_BASE_DATOS`)
- Base de Datos: `snake_case` (`id_usuario`, `fecha_nacimiento`, `ejercicios_catalogo`)

---

## 4. Tabla Comparativa de Convenciones

| Convención | Clases | Métodos | Variables | Constantes | BD (SQLite) |
|---|---|---|---|---|---|
| **PascalCase Estricto** | `MiClase` | `MiMetodo()` | `MiVariable` | `MiConstante` | `MiTabla` |
| **Estándar Java (Recomendado)** | `MiClase` | `miMetodo()` | `miVariable` | `MI_CONSTANTE` | `mi_tabla` |
| **Estilo C# / .NET** | `MiClase` | `MiMetodo()` | `miVariable` | `MI_CONSTANTE` | `mi_tabla` |
| **FitGuide Actual** | `MiClase` | `MiMetodo()` + alias | `miVariable` | `MI_CONSTANTE` | `mi_tabla` |
