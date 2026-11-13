# 📄 Documento de Requisitos — Módulo Coach (PSP1)

**Proyecto:** FitGuide — Sistema de Entrenamiento Progresivo  
**Módulo:** Gestión de Ejercicios y Rutinas por parte del Coach  
**Versión:** 1.0  
**Fecha:** 23 de octubre de 2026  

---

## 1. Requisitos Funcionales

### RF-01: Visualizar Catálogo de Ejercicios
| Atributo | Valor |
|---|---|
| **ID** | RF-01 |
| **Prioridad** | Alta |
| **Descripción** | El Coach debe poder visualizar todos los ejercicios registrados en la base de datos en una tabla con columnas: ID, Nombre, Zona Corporal, Nivel, Tipo de Peso, Repeticiones, Series, Duración, Descanso y Descripción. |
| **Entrada** | Ninguna (carga automática) o filtros opcionales (zona, nivel, tipo peso). |
| **Salida** | Lista de ejercicios mostrada en un `TableView`. |
| **Criterio de Aceptación** | Al entrar a la pantalla de catálogo, se muestran todos los ejercicios. Al aplicar filtros, la tabla se actualiza en tiempo real. |

---

### RF-02: Agregar Ejercicio al Catálogo
| Atributo | Valor |
|---|---|
| **ID** | RF-02 |
| **Prioridad** | Alta |
| **Descripción** | El Coach debe poder agregar un nuevo ejercicio proporcionando: nombre, zona corporal, nivel, tipo de peso, repeticiones sugeridas, series sugeridas, duración, descanso, descripción, ruta de imagen y ruta de video. |
| **Entrada** | Datos del formulario de creación. |
| **Salida** | Registro insertado en la tabla `ejercicios_catalogo`. Mensaje de confirmación. |
| **Validaciones** | Nombre no vacío, repeticiones > 0, series > 0, zona corporal válida ("Brazo", "Abdomen", "Pierna"), nivel válido ("Básico", "Intermedio", "Avanzado"). |
| **Criterio de Aceptación** | Tras agregar, el ejercicio aparece inmediatamente en la tabla del catálogo. |

---

### RF-03: Editar Ejercicio del Catálogo
| Atributo | Valor |
|---|---|
| **ID** | RF-03 |
| **Prioridad** | Alta |
| **Descripción** | El Coach debe poder seleccionar un ejercicio de la tabla y modificar cualquiera de sus atributos. |
| **Entrada** | Ejercicio seleccionado + datos modificados en formulario. |
| **Salida** | Registro actualizado en `ejercicios_catalogo`. Mensaje de confirmación. |
| **Validaciones** | Las mismas que RF-02. |
| **Criterio de Aceptación** | Tras editar, la tabla refleja los cambios inmediatamente. |

---

### RF-04: Eliminar Ejercicio del Catálogo
| Atributo | Valor |
|---|---|
| **ID** | RF-04 |
| **Prioridad** | Alta |
| **Descripción** | El Coach debe poder eliminar un ejercicio seleccionado, con confirmación previa. |
| **Entrada** | Ejercicio seleccionado en la tabla. |
| **Salida** | Registro eliminado de `ejercicios_catalogo`. Mensaje de confirmación. |
| **Validaciones** | Confirmación obligatoria antes de eliminar. Verificar que el ejercicio no esté referenciado en rutinas activas (advertencia). |
| **Criterio de Aceptación** | Tras confirmar eliminación, el ejercicio desaparece de la tabla. |

---

### RF-05: Visualizar Rutinas de Usuarios
| Atributo | Valor |
|---|---|
| **ID** | RF-05 |
| **Prioridad** | Alta |
| **Descripción** | El Coach debe poder ver la lista de usuarios registrados y, al seleccionar uno, ver sus rutinas guardadas con sus detalles (grupo muscular, nivel, series, descanso, peso extra, repeticiones por ejercicio). |
| **Entrada** | Selección de usuario en la tabla. |
| **Salida** | Lista de rutinas del usuario seleccionado. |
| **Criterio de Aceptación** | Al hacer clic en un usuario, sus rutinas se muestran en la tabla de rutinas. |

---

### RF-06: Editar Rutina de un Usuario (Progresión)
| Atributo | Valor |
|---|---|
| **ID** | RF-06 |
| **Prioridad** | Alta |
| **Descripción** | El Coach debe poder modificar los parámetros de una rutina guardada de un usuario para implementar entrenamiento progresivo: incrementar series, modificar repeticiones por ejercicio, ajustar tiempo de descanso y modificar peso extra. |
| **Entrada** | Rutina seleccionada + nuevos valores de progresión. |
| **Salida** | Rutina actualizada en `rutinas_guardadas`. |
| **Validaciones** | Series entre 1 y 10, descanso entre 10 y 300 segundos, peso ≥ 0, repeticiones > 0. |
| **Criterio de Aceptación** | Tras guardar, los nuevos valores se reflejan en la tabla y están disponibles para el usuario en su siguiente sesión. |

---

### RF-07: Filtrar Ejercicios en el Catálogo
| Atributo | Valor |
|---|---|
| **ID** | RF-07 |
| **Prioridad** | Media |
| **Descripción** | El Coach debe poder filtrar el catálogo de ejercicios por zona corporal, nivel y tipo de peso usando ComboBoxes. |
| **Entrada** | Selección en los ComboBox de filtros. |
| **Salida** | Tabla filtrada dinámicamente. |
| **Criterio de Aceptación** | La tabla se actualiza al cambiar cualquier filtro. El filtro "Todos" muestra todos los registros. |

---

### RF-08: Buscar Ejercicio por Nombre
| Atributo | Valor |
|---|---|
| **ID** | RF-08 |
| **Prioridad** | Media |
| **Descripción** | El Coach debe poder buscar ejercicios por nombre usando un campo de texto que filtra en tiempo real. |
| **Entrada** | Texto escrito en el campo de búsqueda. |
| **Salida** | Tabla filtrada por coincidencia parcial de nombre. |
| **Criterio de Aceptación** | Al escribir, la tabla muestra solo los ejercicios cuyo nombre contiene el texto ingresado (case-insensitive). |

---

## 2. Requisitos No Funcionales

### RNF-01: Estilo de Programación
| Atributo | Valor |
|---|---|
| **ID** | RNF-01 |
| **Descripción** | Todo el código debe seguir la convención **PascalCase** para nombres de clases y **camelCase** para métodos, variables y parámetros. |

### RNF-02: Arquitectura
| Atributo | Valor |
|---|---|
| **ID** | RNF-02 |
| **Descripción** | El módulo debe seguir el patrón **MVC** (Model-View-Controller) usando JavaFX/FXML para las vistas y el patrón **DAO** para el acceso a datos. |

### RNF-03: Compatibilidad con la Base de Datos
| Atributo | Valor |
|---|---|
| **ID** | RNF-03 |
| **Descripción** | El módulo debe usar la misma base de datos SQLite (`fitguide.db`) existente y la clase `ConexionDB` para las conexiones. |

### RNF-04: Independencia del Módulo
| Atributo | Valor |
|---|---|
| **ID** | RNF-04 |
| **Descripción** | El módulo Coach debe ser autocontenido. Los compañeros del equipo se encargarán de integrarlo con el sistema de login y la navegación general. |

### RNF-05: Rendimiento
| Atributo | Valor |
|---|---|
| **ID** | RNF-05 |
| **Descripción** | Las operaciones CRUD sobre ejercicios y la carga de rutinas deben ejecutarse en menos de 2 segundos. |

### RNF-06: Usabilidad
| Atributo | Valor |
|---|---|
| **ID** | RNF-06 |
| **Descripción** | Las pantallas del Coach deben tener un diseño visual coherente con el resto de la aplicación (colores verdes, estilo de cards, tipografía consistente). |

---

## 3. Matriz de Trazabilidad

| Requisito | Clase(s) Involucrada(s) | Pantalla FXML |
|---|---|---|
| RF-01 | `EjercicioCatalogoDao`, `EjercicioCoach` | `PantallaCoachCatalogoEjercicios.fxml` |
| RF-02 | `EjercicioCatalogoDao`, `EjercicioCoach` | `PantallaCoachCatalogoEjercicios.fxml` (diálogo) |
| RF-03 | `EjercicioCatalogoDao`, `EjercicioCoach` | `PantallaCoachCatalogoEjercicios.fxml` (diálogo) |
| RF-04 | `EjercicioCatalogoDao` | `PantallaCoachCatalogoEjercicios.fxml` (confirmación) |
| RF-05 | `CoachRutinaDao`, `UsuarioDAO` | `PantallaCoachGestionRutinas.fxml` |
| RF-06 | `CoachRutinaDao` | `PantallaCoachGestionRutinas.fxml` (diálogo) |
| RF-07 | `EjercicioCatalogoDao` | `PantallaCoachCatalogoEjercicios.fxml` |
| RF-08 | `EjercicioCatalogoDao` | `PantallaCoachCatalogoEjercicios.fxml` |
