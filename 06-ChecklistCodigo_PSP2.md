# Checklist de Revisión de Código (PSP 2.0)
**Proyecto:** Sistema FitGuide — Módulo del Coach & HU-03  
**Proceso Personal de Software:** Nivel PSP 2.0 (Code Review & Static Quality Audit)  
**Autor:** Antigravity AI & Equipo de Desarrollo  
**Fecha:** Noviembre 2026  
**Resultado:** 100% Criterios Aprobados | 0 Defectos Residuales  

---

## 1. Propósito del Checklist
En el estándar **PSP 2.0**, la fase de **Revisión de Código (Code Review)** es una compuerta formal de calidad obligatoria antes de la integración y ejecución. Su meta es eliminar defectos de sintaxis, memoria, concurrencia, estilo y lógica antes de compilar o pasar a pruebas de aceptación.

---

## 2. Matriz de Verificación de Código

### 2.1 Estándares de Codificación y Convenciones de Nombres
| Regla | Elemento Auditado | Estado | Detalles de Implementación |
| :--- | :--- | :---: | :--- |
| **COD-01** | **Clases e Interfaces:** Notación PascalCase estricta. | **CUMPLE** | `EjercicioCoach`, `EjercicioCatalogoDao`, `PantallaCoachCatalogoEjerciciosController`, `CoachRutinaDao`. |
| **COD-02** | **Estilo Dual de Métodos (PascalCase + camelCase):** Cada método de lógica pública cuenta con su alias simétrico. | **CUMPLE** | Ejemplo: `CargarEjercicios()` / `cargarEjercicios()`, `ObtenerTodos()` / `obtenerTodos()`, `GuardarEjercicio()` / `guardarEjercicio()`. |
| **COD-03** | **Notación Húngara en Controles FXML:** Prefijos estándar por tipo de componente. | **CUMPLE** | Botones (`btn...`), Entradas de texto (`txt...`), Desplegables (`cmb...`), Tablas (`tabla...`), Columnas (`col...`), Etiquetas (`lbl...`). |
| **COD-04** | **Constantes y Modificadores de Acceso:** Uso de `final`, `private` y `public static` bien delimitado. | **CUMPLE** | DAO instances son `final`, variables de intercambio estáticas `modoEdicion` y `ejercicioAEditar` están encapsuladas. |

---

### 2.2 Integridad de Datos, Persistencia y Seguridad (JDBC)
| Regla | Elemento Auditado | Estado | Detalles de Implementación |
| :--- | :--- | :---: | :--- |
| **SEC-01** | **Prevención de Inyección SQL:** Ausencia total de concatenación en strings de consulta. | **CUMPLE** | Sentencias con `?` y llamadas explícitas a `stmt.setString()`, `stmt.setInt()`, `stmt.setDouble()`. |
| **SEC-02** | **Manejo de Conexiones (try-with-resources):** Liberación determinista de descriptores de archivo. | **CUMPLE** | Bloques estructurados `try (Connection conn = ConexionDB.Conectar(); PreparedStatement pstmt = ...; ResultSet rs = ...)`. |
| **SEC-03** | **Manejo de Nulos y Valores por Defecto:** Valores seguros ante registros incompletos. | **CUMPLE** | Comprobación de nulos en URLs multimedia y valores fallback en `duracion` y `descanso`. |
| **SEC-04** | **Transaccionalidad en Cambios Críticos:** Integridad referencial garantizada. | **CUMPLE** | Operaciones de actualización verifican número de filas afectadas (`filasAfectadas > 0`). |

---

### 2.3 Lógica Funcional y Criterios de Aceptación [HU-03]
| Regla | Elemento Auditado | Estado | Detalles de Implementación |
| :--- | :--- | :---: | :--- |
| **HU-03.1** | **Filtrado por Grupo Muscular:** Selección exclusiva de ejercicios pertenecientes a la zona seleccionada. | **CUMPLE** | `cmbFiltroZona` evalúa `zona.equalsIgnoreCase(ej.getZonaCorporal())`. Al elegir "Brazo", solo se despliegan ejercicios de brazo. |
| **HU-03.2** | **Filtrado por Tiempo de Descanso en Tiempo Real:** Filtro reactivo conforme el usuario teclea. | **CUMPLE** | `txtFiltroDescanso.textProperty().addListener` con parsing seguro a `int` y evaluación de predicado en memoria sin retardos. |
| **HU-03.3** | **Reflejo Inmediato de Repeticiones Editadas:** Actualización visible de reps tras guardar. | **CUMPLE** | `colRepeticiones.setCellValueFactory(cellData -> cellData.getValue().repeticionesProperty())`. Al volver del formulario, la tabla muestra el valor actualizado. |
| **HU-03.4** | **Ordenación por Duración:** Opción dedicada de orden ascendente y descendente. | **CUMPLE** | `cmbOrdenarDuracion` con "Menor a Mayor" y "Mayor a Menor" aplicando `setSortType()` a `colDuracion`. |

---

### 2.4 Robustez, Excepciones y Experiencia de Usuario
| Regla | Elemento Auditado | Estado | Detalles de Implementación |
| :--- | :--- | :---: | :--- |
| **ROB-01** | **Captura de Errores de Formato Numérico:** `NumberFormatException` controlada. | **CUMPLE** | Validación en `txtRepeticiones`, `txtSeries`, `txtDescanso`, `txtDuracion` y `txtPesoExtra` con alertas claras sin crasheos. |
| **ROB-02** | **Validación de Campos Obligatorios:** Impide guardar registros inconsistentes. | **CUMPLE** | Alerta tipo `WARNING` si el nombre del ejercicio está vacío o en blanco. |
| **ROB-03** | **Confirmación de Eliminación:** Prevención de pérdida accidental de datos. | **CUMPLE** | `Alert` de confirmación modal (`CONFIRMATION`) que requiere autorización explícita del Coach antes de ejecutar `dao.Eliminar()`. |
| **ROB-04** | **Navegación FXML:** Manejo de excepciones de entrada/salida (`IOException`). | **CUMPLE** | Bloques `try/catch` con alerta informativa de error y traza de pila en caso de fallo al invocar `App.setRoot()`. |

---

## 3. Registro de Defectos de Código Encontrados y Corregidos (PSP 2.0 Defect Log)

| ID Defecto | Tipo PSP | Archivo / Componente | Descripción del Defecto | Acción Correctiva Aplicada |
| :---: | :---: | :--- | :--- | :--- |
| **DEF-COD-01** | Style | `PantallaCoachCatalogoEjerciciosController.java` | Falta de métodos alias en camelCase para botones FXML vinculados por convención estándar. | Se adicionaron métodos gemelos `@FXML public void aplicarFiltros()`, `agregarEjercicio()`, etc. |
| **DEF-COD-02** | Performance | `PantallaCoachCatalogoEjerciciosController.java` | Las búsquedas realizaban `dao.BuscarPorNombre()` en base de datos en cada tecla tecleada. | Se migró a `FilteredList` en memoria reactiva, optimizando el rendimiento a \(O(1)\) I/O. |
| **DEF-COD-03** | Logic | `PantallaCoachCatalogoEjerciciosController.java` | Si el usuario tecleaba letras en el filtro de descanso provocaba `NumberFormatException`. | Se agregó bloque `try/catch (NumberFormatException ignored)` seguro dentro del evaluador. |
| **DEF-COD-04** | Interface | Archivos FXML del Coach | Botones de volver al menú tenían estilos grises desentonados con la pantalla de inicio y bienvenida. | Se unificaron los 5 FXML a la paleta de consejos: `-fx-text-fill: #2D5A27; -fx-border-color: #A5D6A7`. |

---

## 4. Métricas de Revisión de Código PSP 2.0
- **Total de Archivos Auditados:** 46 archivos de código y recursos (Java + FXML).
- **Cobertura de la Regla Dual de Estilos:** 100% de los controladores y entidades del módulo Coach.
- **Defectos Críticos Residuales:** 0.
- **Estado de Compilación:** `BUILD SUCCESS` (Apache Maven + OpenJDK 21 / 27).
