# Documentación Técnica del Sistema FitGuide
**Proyecto:** FitGuide — Módulo de Administración del Entrenador (Coach) & HU-03  
**Asignatura / Marco:** Software Quality Assurance (SQA) & PSP 1 / 2.0  
**Fecha de Publicación:** Noviembre 2026  

---

## 1. Código Fuente y Referencia en GitHub

El código fuente completo del proyecto, incluyendo el módulo del Coach, las pantallas FXML rediseñadas con el estilo visual de consejos, la implementación de [HU-03] y los artefactos de calidad PSP, se encuentra versionado y disponible en el siguiente repositorio oficial:

- **URL de Repositorio GitHub:** [https://github.com/DestroyedSquid/FitGuide.git](https://github.com/DestroyedSquid/FitGuide.git)
- **Acceso por SSH:** `git@github.com:DestroyedSquid/FitGuide.git`
- **Ruta del Proyecto en Workspace:** `SistemaFitGuide/SistemaFitGuide/`

---

## 2. Herramientas de Software Utilizadas y Versiones

La siguiente tabla detalla el inventario completo de herramientas, compiladores, bibliotecas y entornos utilizados para el desarrollo, compilación y pruebas del sistema:

| Herramienta / Tecnología | Versión Utilizada | Propósito en el Proyecto |
| :--- | :---: | :--- |
| **Java Development Kit (JDK)** | **OpenJDK 21 / 27-ea** | Compilador y máquina virtual de ejecución (`javac`, `java`). Compatibilidad de bytecode release 21. |
| **Apache Maven** | **3.8.5 / 3.9.x** | Gestor de ciclo de vida del proyecto, dependencias y empaquetado JAR (`pom.xml`). |
| **JavaFX (OpenJFX)** | **21.0.10** | Framework para el desarrollo de la interfaz gráfica de usuario (`javafx-controls`, `javafx-fxml`). |
| **SQLite JDBC Driver** | **3.45.1.0** | Conector JDBC embebido para persistencia de datos relacionales en archivo local (`fitguide.db`). |
| **SLF4J API** | **1.7.36** | Capa de abstracción de logging requerida por los controladores JDBC modernos. |
| **Maven Compiler Plugin** | **3.13.0** | Plugin de Maven configurado para compilar fuentes modernas en Java 21 con soporte modular (`module-info.java`). |
| **JavaFX Maven Plugin** | **0.0.8** | Plugin para ejecución directa del ciclo de vida de la aplicación JavaFX (`mvn javafx:run`). |
| **Git / GitHub** | **Git 2.40+** | Sistema de control de versiones distribuido y plataforma de alojamiento del repositorio. |
| **DB Browser for SQLite** | **3.12.2+** | Herramienta GUI para visualización, verificación de integridad y diseño de tablas relacionales. |
| **Entorno de Desarrollo (IDE)** | **Apache NetBeans 21+ / IntelliJ IDEA 2024** | Entornos de desarrollo integrados para desarrollo de software en JavaFX. |

---

## 3. Instrucciones para Correr el Programa

### 3.1 Requisitos Previos del Sistema
1. Disponer de **Java JDK 21** o superior instalado en el sistema operativo.
2. Disponer de **Apache Maven 3.8+** configurado en el `PATH` o utilizar el wrapper `mvnw`.
3. Conexión a Internet únicamente para la primera descarga de dependencias del repositorio central de Maven.

---

### 3.2 Clonación del Repositorio
Para obtener una copia limpia del código fuente, ejecuta en tu terminal:

```bash
git clone https://github.com/DestroyedSquid/FitGuide.git
cd FitGuide/SistemaFitGuide
```

---

### 3.3 Compilación del Proyecto
Para compilar los 46 archivos fuente y procesar los recursos FXML:

```bash
mvn clean compile
```

Si cuentas con múltiples versiones de Java instaladas en tu sistema Linux/macOS, asegúrate de apuntar la variable `JAVA_HOME` a la versión correcta:

```bash
JAVA_HOME=/usr/lib/jvm/java-latest-openjdk mvn clean compile
```

---

### 3.4 Ejecución de la Aplicación
Para lanzar la aplicación gráfica con JavaFX:

```bash
mvn javafx:run
```

O bien, mediante el empaquetado y ejecución directa:

```bash
mvn package
java --module-path target/lib -m omr.uacm.sistemafitguide/omr.uacm.sistemafitguide.App
```

---

### 3.5 Guía Rápida de Uso del Módulo del Coach

1. **Ingreso al Modo Coach:**
   - En la **Pantalla de Inicio** de FitGuide, haz clic en el botón verde con borde destacado **"Acceso Coach"** ubicado en el encabezado.
   
2. **Menú del Coach:**
   - Selecciona **"Catálogo de Ejercicios"** para gestionar el catálogo CRUD o **"Rutinas de Atletas"** para supervisar la progresión de usuarios.

3. **Filtrado en Tiempo Real y Ordenación [HU-03]:**
   - **Búsqueda por texto:** Teclea el nombre del ejercicio en el campo de texto; la lista se filtrará al instante sin presionar botones.
   - **Filtro de Zona Muscular:** Selecciona "Brazo", "Abdomen" o "Pierna" en el desplegable. Se visualizarán exclusivamente los ejercicios correspondientes a dicho grupo muscular.
   - **Filtro de Tiempo de Descanso:** Escribe los segundos máximos deseados en el campo `Máx Desc (s)` (por ejemplo: `30`); la tabla mostrará en tiempo real solo los circuitos aptos para ese descanso.
   - **Ordenación por Duración:** Selecciona en el combo `Duración` las opciones "Menor a Mayor" o "Mayor a Menor", o bien haz clic directamente en la cabecera de columna `Dur (s)`.

4. **Edición y Reflejo Inmediato de Repeticiones:**
   - Selecciona un ejercicio de la tabla y presiona **"✏️ Editar"**.
   - Se abrirá la pantalla completa `PantallaCoachFormularioEjercicio`.
   - Modifica el campo de **Repeticiones Sugeridas** (o series, descanso, duración, etc.).
   - Presiona **"💾 Guardar Ejercicio"**.
   - El sistema persistirá los cambios en `fitguide.db` y te regresará al catálogo, donde verás reflejadas las nuevas repeticiones de inmediato en la columna **Reps**.

5. **Ajuste de Sobrecarga Progresiva (Rutinas):**
   - En **"Rutinas de Atletas"**, selecciona un usuario de la tabla izquierda para consultar sus circuitos.
   - Haz clic en **"⚙️ Ajustar Progresión"** para acceder al editor dedicado.
   - Ajusta series globales, descanso y peso extra, o edita directamente las repeticiones por ejercicio haciendo doble clic sobre la celda correspondiente.
   - Guarda los cambios con **"💾 Guardar Progresión"**.
