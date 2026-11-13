# Checklist de Revisión de Diseño (PSP 2.0)
**Proyecto:** Sistema FitGuide — Módulo del Coach & HU-03  
**Proceso Personal de Software:** Nivel PSP 2.0 (Design Verification & Quality Gate)  
**Autor:** Antigravity AI & Equipo de Desarrollo  
**Fecha:** Noviembre 2026  
**Estado:** Aprobado / Verificado  

---

## 1. Propósito del Checklist
En el marco del **Personal Software Process (PSP 2.0)**, la revisión estructurada de diseño tiene como objetivo detectar y corregir defectos conceptuales, inconsistencias arquitectónicas y fallas en la especificación de interfaces antes de la fase de codificación o durante la integración de nuevos incrementos ([HU-03]).

---

## 2. Criterios de Calidad y Verificación de Diseño

### 2.1 Trazabilidad de Requisitos de Usuario
| Ítem | Criterio de Verificación | Estado | Evidencia / Justificación |
| :--- | :--- | :---: | :--- |
| **REQ-01** | Cobertura de HU-01 (Catálogo de ejercicios con CRUD completo). | **CUMPLE** | Pantallas `PantallaCoachCatalogoEjercicios` y `PantallaCoachFormularioEjercicio`. Métodos `Agregar`, `Editar`, `Eliminar` en DAO. |
| **REQ-02** | Cobertura de HU-02 (Ajuste de sobrecarga progresiva en rutinas de atletas). | **CUMPLE** | Pantallas `PantallaCoachGestionRutinas` y `PantallaCoachEditarRutina` con manipulación de series, descansos, repeticiones y peso. |
| **REQ-03** | Cobertura de HU-03 (Filtrado en tiempo real por grupo muscular, descanso y ordenación por duración). | **CUMPLE** | Implementación reactiva con `FilteredList` y `SortedList` vinculadas a `txtBusqueda`, `cmbFiltroZona`, `txtFiltroDescanso` y `cmbOrdenarDuracion`. |
| **REQ-04** | Persistencia inmediata del cambio de repeticiones de un ejercicio. | **CUMPLE** | Evento de persistencia en `ejercicios_catalogo` con recarga y vinculación bidireccional de propiedades JavaFX (`repeticionesProperty()`). |

---

### 2.2 Arquitectura y Patrones de Diseño (MVC)
| Ítem | Criterio de Verificación | Estado | Evidencia / Justificación |
| :--- | :--- | :---: | :--- |
| **ARC-01** | Estricta separación Modelo - Vista - Controlador. | **CUMPLE** | FXML define presentación, POJOs (`EjercicioCoach`) encapsulan datos, DAOs (`EjercicioCatalogoDao`, `CoachRutinaDao`) aíslan SQL. |
| **ARC-02** | Desacoplamiento entre Vistas (Navegación de Pantallas Completas). | **CUMPLE** | Se abandonaron los diálogos modales; se utiliza el patrón `App.setRoot(fxml)` para transiciones limpias y fluidas. |
| **ARC-03** | Inyección y enlace de propiedades reactivas JavaFX. | **CUMPLE** | Modelos implementan `StringProperty`, `IntegerProperty` y `DoubleProperty`, permitiendo bindings directos en `TableView`. |
| **ARC-04** | Estilo Arquitectónico Dual (PascalCase + camelCase). | **CUMPLE** | Todas las firmas de métodos y propiedades proveen variantes en PascalCase y camelCase para compatibilidad multiplataforma y legado. |

---

### 2.3 Diseño de Interfaz de Usuario y Estilo Visual
| Ítem | Criterio de Verificación | Estado | Evidencia / Justificación |
| :--- | :--- | :---: | :--- |
| **UI-01** | Consistencia visual con la pantalla de Consejos ("Motivación Diaria"). | **CUMPLE** | Paleta verde fuerte `#2D5A27` y `#1B4D1B`, bordes acentuados `#A5D6A7`, fondos de tarjetas `#FFFFFF` con esquinas redondeadas (25px) y drop-shadows suaves. |
| **UI-02** | Ergonomía y legibilidad de controles de filtrado. | **CUMPLE** | Barra horizontal compacta estilizada en `#E4F2E7` con bordes `#A5D6A7` que agrupa búsqueda, filtros y badge de totalización. |
| **UI-03** | Notación Húngara en identificadores FXML (`fx:id`). | **CUMPLE** | Nombres canónicos: `btnGuardar`, `txtNombre`, `cmbZonaCorporal`, `tablaEjercicios`, `colDescanso`, `lblTotalRegistros`. |
| **UI-04** | Retroalimentación al usuario (Feedback & Alerts). | **CUMPLE** | Mensajes de confirmación para borrado, alertas de validación para campos obligatorios y diálogo informativo de éxito. |

---

### 2.4 Diseño de Datos y Persistencia Relacional
| Ítem | Criterio de Verificación | Estado | Evidencia / Justificación |
| :--- | :--- | :---: | :--- |
| **DAT-01** | Normalización de la tabla `ejercicios_catalogo`. | **CUMPLE** | Llave primaria `id_ejercicio INTEGER PRIMARY KEY AUTOINCREMENT`, campos estructurados para duracion, descanso, zona y URLs multimedia. |
| **DAT-02** | Robustez en la inicialización DDL (Idempotencia). | **CUMPLE** | Sentencias con cláusula `CREATE TABLE IF NOT EXISTS` y sembrado automático de datos iniciales en el primer arranque. |
| **DAT-03** | Prevención de Inyección SQL. | **CUMPLE** | 100% de consultas parametrizadas mediante `PreparedStatement` tanto en lecturas como escrituras. |
| **DAT-04** | Gestión de Recursos y Fugas de Conexiones. | **CUMPLE** | Patrón `try-with-resources` implementado en todas las operaciones JDBC con cierre automático de `Connection`, `PreparedStatement` y `ResultSet`. |

---

## 3. Registro de Defectos de Diseño (PSP 2.0 Defect Log)
Durante las sesiones de inspección y diseño previo se identificaron y remediaron los siguientes defectos:

| ID Defecto | Tipo PSP | Descripción del Defecto | Causa Raíz | Acción Correctiva |
| :---: | :---: | :--- | :--- | :--- |
| **DEF-DES-01** | Interface | Formulario de edición se mostraba en un `Dialog` modal estrecho donde los textos se cortaban. | Diseño preliminar no escalable para resoluciones bajas. | Rediseño a pantalla completa (`PantallaCoachFormularioEjercicio.fxml`) con `ScrollPane`. |
| **DEF-DES-02** | Logic | El filtrado requería consulta a la base de datos por cada carácter tecleado en la búsqueda. | Dependencia de consultas SQL síncronas de alto costo. | Implementación de `FilteredList` y `SortedList` en memoria reactiva con `setPredicate()`. |
| **DEF-DES-03** | Interface | Disparidad de estilos: pantallas de Coach tenían tonalidades azuladas/grises ajenas a la identidad verde de FitGuide. | Falta de aplicación de guía de estilos centralizada. | Estandarización a la paleta "Consejos": `#2D5A27`, `#1B4D1B`, `#A5D6A7` y degradados `#E4F2E7`. |
| **DEF-DES-04** | Data | Al editar repeticiones, la lista de catálogo no reflejaba el cambio inmediatamente sin reiniciar. | Desvinculación de la colección observable en el retorno. | Vinculación con `SimpleIntegerProperty` y recarga estructurada de la colección base al volver de la edición. |

---

## 4. Dictamen del Quality Gate PSP 2.0
El diseño del módulo del Coach y la Historia de Usuario HU-03 cumple satisfactoriamente con la totalidad de métricas de cobertura funcional, ergonomía de usuario, robustez arquitectónica y directrices de codificación dual.

**Aprobado para despliegue y validación final:**  
- Fase: **PSP 2.0 Design Review Complete**  
- Tasa de Defectos Residuales de Diseño: **0% críticos**
