# 🏗️ Diseño Conceptual de Alto Nivel — Módulo Coach (PSP1)

**Proyecto:** FitGuide  
**Módulo:** Coach — Gestión de Ejercicios y Rutinas  
**Fecha:** 23 de octubre de 2026  

---

## 1. Arquitectura General del Sistema

```mermaid
flowchart TB
    subgraph Vista["🖥️ Vista (FXML + CSS)"]
        V1["PantallaCoachMenu.fxml"]
        V2["PantallaCoachCatalogoEjercicios.fxml"]
        V3["PantallaCoachGestionRutinas.fxml"]
    end

    subgraph Controlador["⚙️ Controladores (JavaFX Controllers)"]
        C1["PantallaCoachMenuController"]
        C2["PantallaCoachCatalogoEjerciciosController"]
        C3["PantallaCoachGestionRutinasController"]
    end

    subgraph Modelo["📦 Modelo (POJOs)"]
        M1["EjercicioCoach"]
        M2["Rutina (existente)"]
        M3["Usuario (existente)"]
    end

    subgraph DAO["🗄️ Acceso a Datos (DAO)"]
        D1["EjercicioCatalogoDao"]
        D2["CoachRutinaDao"]
        D3["UsuarioDAO (existente)"]
    end

    subgraph BD["💾 Base de Datos"]
        DB1["fitguide.db (SQLite)"]
    end

    V1 <--> C1
    V2 <--> C2
    V3 <--> C3

    C2 --> D1
    C3 --> D2
    C3 --> D3
    C2 --> M1
    C3 --> M2
    C3 --> M3

    D1 --> DB1
    D2 --> DB1
    D3 --> DB1
```

---

## 2. Diagrama de Clases del Módulo Coach

```mermaid
classDiagram
    class EjercicioCoach {
        -int IdEjercicio
        -String Nombre
        -String ZonaCorporal
        -String Nivel
        -String TipoPeso
        -int Repeticiones
        -int Series
        -int Duracion
        -int Descanso
        -String Descripcion
        -String UrlImagen
        -String UrlVideo
        +EjercicioCoach()
        +EjercicioCoach(all params)
        +getters/setters PascalCase properties
    }

    class EjercicioCatalogoDao {
        +ObtenerTodos() List~EjercicioCoach~
        +ObtenerFiltrados(zona, nivel, tipoPeso) List~EjercicioCoach~
        +BuscarPorNombre(nombre) List~EjercicioCoach~
        +Agregar(ejercicio) boolean
        +Actualizar(ejercicio) boolean
        +Eliminar(idEjercicio) boolean
    }

    class CoachRutinaDao {
        +ObtenerUsuarios() List~Usuario~
        +ObtenerRutinasDeUsuario(idUsuario) List~Rutina~
        +ObtenerDetalleRutina(idUsuario, llave) Rutina
        +ActualizarRutinaProgresion(idUsuario, llave, rutina) boolean
    }

    class PantallaCoachMenuController {
        +IrACatalogoEjercicios()
        +IrAGestionRutinas()
        +CerrarSesion()
    }

    class PantallaCoachCatalogoEjerciciosController {
        -TableView~EjercicioCoach~ TablaEjercicios
        -ComboBox FiltroZona
        -ComboBox FiltroNivel
        -ComboBox FiltroTipoPeso
        -TextField CampoBusqueda
        +initialize()
        +CargarEjercicios()
        +AplicarFiltros()
        +AgregarEjercicio()
        +EditarEjercicio()
        +EliminarEjercicio()
        +BuscarEjercicio()
        +RegresarMenu()
    }

    class PantallaCoachGestionRutinasController {
        -TableView~Usuario~ TablaUsuarios
        -TableView~Rutina~ TablaRutinas
        -TableView~Ejercicio~ TablaDetalleEjercicios
        +initialize()
        +CargarUsuarios()
        +CargarRutinasDeUsuario()
        +EditarRutinaProgresion()
        +RegresarMenu()
    }

    EjercicioCatalogoDao --> EjercicioCoach
    CoachRutinaDao --> Rutina
    CoachRutinaDao --> Usuario
    PantallaCoachCatalogoEjerciciosController --> EjercicioCatalogoDao
    PantallaCoachGestionRutinasController --> CoachRutinaDao
    EjercicioCatalogoDao --> ConexionDB
    CoachRutinaDao --> ConexionDB
```

---

## 3. Diagrama Entidad-Relación (Extendido)

```mermaid
erDiagram
    USUARIOS {
        int id_usuario PK
        text nombre_usuario UK
        text contrasena
        real peso
        real altura
        text fecha_nacimiento
        text genero
        text rol
    }

    EJERCICIOS_CATALOGO {
        int id_ejercicio PK
        text nombre
        text zona_corporal
        text nivel
        text tipo_peso
        int repeticiones
        int series
        int duracion
        int descanso
        text descripcion
        text url_imagen
        text url_video
    }

    RUTINAS_GUARDADAS {
        int id PK
        int id_usuario FK
        text grupo_muscular
        text nivel
        text tipo_peso
        int series
        int tiempo_descanso
        text repeticiones_json
        real peso_extra_kg
    }

    HISTORIAL_RUTINAS {
        int id_historial PK
        int id_usuario FK
        text fecha
        text grupo_muscular
        text nivel
        int duracion_segundos
    }

    HISTORIAL_EJERCICIOS {
        int id_detalle PK
        int id_historial FK
        text nombre_ejercicio
        int reps_objetivo
        int reps_reales
    }

    USUARIOS ||--o{ RUTINAS_GUARDADAS : "tiene"
    USUARIOS ||--o{ HISTORIAL_RUTINAS : "registra"
    HISTORIAL_RUTINAS ||--o{ HISTORIAL_EJERCICIOS : "contiene"
```

> [!NOTE]
> La tabla `ejercicios_catalogo` es **nueva** y reemplaza al catálogo hardcoded de `CatalogoEjercicios.java`. La tabla `usuarios` se extiende con un campo `rol` (TEXT) que distingue entre "usuario" y "coach".

---

## 4. Flujo de Navegación

```mermaid
flowchart LR
    A["Login"] -->|Coach| B["PantallaCoachMenu"]
    B --> C["Catálogo de Ejercicios"]
    B --> D["Gestión de Rutinas"]
    C -->|Agregar| E["Formulario Nuevo Ejercicio"]
    C -->|Editar| F["Formulario Editar Ejercicio"]
    C -->|Eliminar| G["Confirmación Eliminar"]
    D -->|Seleccionar Usuario| H["Ver Rutinas"]
    H -->|Editar Rutina| I["Formulario Progresión"]
    E --> C
    F --> C
    G --> C
    I --> H
    C --> B
    D --> B
```

---

## 5. Estructura de Archivos del Módulo

```
src/main/java/omr/uacm/sistemafitguide/
├── EjercicioCoach.java                              ← Modelo (POJO)
├── EjercicioCatalogoDao.java                        ← DAO para CRUD de ejercicios
├── CoachRutinaDao.java                              ← DAO para gestión de rutinas
├── PantallaCoachMenuController.java                 ← Controller menú Coach
├── PantallaCoachCatalogoEjerciciosController.java   ← Controller catálogo
├── PantallaCoachGestionRutinasController.java       ← Controller rutinas

src/main/resources/omr/uacm/sistemafitguide/
├── PantallaCoachMenu.fxml                           ← Vista menú
├── PantallaCoachCatalogoEjercicios.fxml             ← Vista catálogo
├── PantallaCoachGestionRutinas.fxml                 ← Vista rutinas
```

---

## 6. Patrones de Diseño Utilizados

| Patrón | Uso en el Módulo |
|---|---|
| **MVC** (Model-View-Controller) | Separación de FXML (Vista), Controllers (Controlador), y POJOs+DAOs (Modelo) |
| **DAO** (Data Access Object) | `EjercicioCatalogoDao` y `CoachRutinaDao` encapsulan el acceso a SQLite |
| **Singleton** | `ConexionDB` y `Sesion` (ya existentes) |
| **Observer** | JavaFX `ObservableList` para binding reactivo de tablas |

---

## 7. Convenciones de Estilo de Programación

### Estilo Principal: PascalCase

| Elemento | Convención | Ejemplo |
|---|---|---|
| Clases | PascalCase | `EjercicioCoach`, `CoachRutinaDao` |
| Métodos públicos | PascalCase | `ObtenerTodos()`, `AgregarEjercicio()` |
| Variables locales | camelCase | `listaEjercicios`, `ejercicioSeleccionado` |
| Campos privados | camelCase | `idEjercicio`, `zonaCorporal` |
| Constantes | UPPER_SNAKE_CASE | `ESTILO_TAB_ACTIVO` |
| FXML IDs | camelCase | `tablaEjercicios`, `btnAgregar` |
| Archivos FXML | PascalCase | `PantallaCoachMenu.fxml` |

> [!TIP]
> ### Segundo Estilo Recomendado: **camelCase Consistente (Java Standard)**
>
> Además de PascalCase para clases, les recomiendo combinar con **camelCase estricto** para todo lo demás (métodos, variables, parámetros). Este es el **estándar oficial de Java** (Java Code Conventions de Oracle/Sun).
>
> **¿Por qué?**
> - Es el estándar que toda la industria Java usa y espera.
> - JavaFX ya usa camelCase para sus propios métodos (`setOnAction`, `getChildren`).
> - Los IDEs (IntelliJ, NetBeans) generan código en camelCase por defecto.
> - Facilita la lectura cuando trabajas con las APIs existentes de Java.
>
> | Elemento | PascalCase (Clase) + camelCase (resto) |
> |---|---|
> | Clase | `EjercicioCoach` |
> | Método | `obtenerTodos()` |
> | Variable | `ejercicioSeleccionado` |
> | Constante | `ESTILO_ACTIVO` |
>
> Este combo (PascalCase clases + camelCase métodos) es lo que ya usan en la mayoría de su código existente, así que es el camino natural. 👍
