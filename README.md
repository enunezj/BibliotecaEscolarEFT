```
# BibliotecaEscolarEFT - Sistema de Gestión de Biblioteca Escolar

## Autor del proyecto

- **Nombre completo:** Emilio Nuñez Jara
- **Sección:** PRY2203
- **Carrera:** Analista Programador Computacional
- **Sede:** Campus Virtual
- **Asignatura:** Desarrollo Orientado a Objetos II
- **Evaluación:** Evaluación Final Transversal - Semana 9
- **Año:** 2026

---

## Descripción general del sistema

**BibliotecaEscolarEFT** es una aplicación de escritorio desarrollada en Java para administrar los procesos de una biblioteca escolar.

El sistema permite gestionar libros, categorías, estudiantes, cuentas de usuario, préstamos, devoluciones y reportes, manteniendo la información almacenada en una base de datos MySQL.

La aplicación incorpora una interfaz gráfica desarrollada con **Java Swing**, utilizando ventanas independientes basadas en `JFrame`.

Su arquitectura aplica principios de programación orientada a objetos, patrones de diseño y concurrencia, conforme a los requerimientos de la Evaluación Final Transversal de Desarrollo Orientado a Objetos II.

Entre los conceptos implementados se encuentran:

- Encapsulamiento.
- Herencia.
- Polimorfismo.
- Clases abstractas.
- Interfaces.
- Sobrescritura de métodos.
- Arquitectura MVC.
- Patrón DAO.
- Patrón Singleton.
- Operaciones CRUD.
- Persistencia mediante JDBC.
- Consultas parametrizadas.
- Transacciones SQL.
- Procesamiento en segundo plano con `SwingWorker`.
- Sincronización mediante `synchronized`.
- Control de consistencia del stock.
- Manejo de excepciones y validación de datos.

---

## Funcionalidades del sistema

### Autenticación y control de acceso

La aplicación permite iniciar sesión con usuarios almacenados en la base de datos MySQL.

Se diferencian dos roles:

**Bibliotecario**

Puede acceder a las funcionalidades administrativas de la biblioteca, incluyendo gestión de libros, categorías, estudiantes, préstamos, devoluciones y reportes.

**Estudiante**

Accede a las funcionalidades habilitadas para su rol, como consulta de libros, préstamos, devoluciones e historial personal.

El sistema verifica las credenciales y utiliza el rol del usuario para determinar las opciones disponibles en la ventana principal.

### Gestión de categorías

El módulo de categorías permite:

- Registrar nuevas categorías.
- Consultar las categorías existentes.
- Modificar los nombres registrados.
- Eliminar categorías cuando las relaciones de la base de datos lo permitan.
- Actualizar la información presentada en la tabla.

Las categorías permiten clasificar los libros registrados.

### Gestión de libros

El módulo permite administrar los siguientes datos:

- Identificador.
- Título.
- Autor.
- ISBN.
- Editorial.
- Stock disponible.
- Categoría asociada.

Se implementan operaciones CRUD:

- **Create:** registrar libros.
- **Read:** consultar libros almacenados.
- **Update:** modificar información existente.
- **Delete:** eliminar registros permitidos por las restricciones de integridad.

Los datos se recuperan de MySQL y se presentan mediante componentes de Java Swing.

### Gestión de estudiantes

El sistema permite registrar y administrar estudiantes utilizando información como:

- Identificador.
- Nombre.
- RUT.
- Curso.
- Correo electrónico.

La gestión de estudiantes se relaciona con sus cuentas de acceso mediante las tablas `estudiantes` y `usuarios`.

La clase `EstudianteCuentaDAO` coordina operaciones entre ambas tablas, utilizando transacciones para mantener la consistencia de los registros.

### Préstamos de libros

El módulo permite registrar préstamos y asociarlos con un estudiante y un libro.

Durante el registro se comprueba la disponibilidad del libro y se actualiza el stock correspondiente.

El préstamo almacena:

- Identificador.
- Estudiante asociado.
- Libro asociado.
- Fecha de préstamo.
- Fecha de vencimiento.
- Estado de devolución.

El registro utiliza mecanismos de sincronización y control transaccional para evitar inconsistencias cuando existen operaciones simultáneas.

### Devoluciones

El sistema permite registrar la devolución de un libro previamente prestado.

Al completar una devolución:

- Se actualiza el estado del préstamo.
- Se registra la fecha real de devolución.
- Se restituye la unidad correspondiente al stock.
- Se conserva el registro para consultar el historial.

La fecha real de devolución se almacena mediante el campo adicional `fecha_devolucion_real`.

Este campo permite distinguir la fecha límite de devolución de la fecha en que efectivamente fue devuelto el libro.

### Control de atrasos

El sistema considera la fecha de vencimiento de los préstamos y permite identificar registros atrasados.

En los reportes se pueden distinguir los préstamos activos, los devueltos y aquellos cuya fecha real de devolución supera el vencimiento.

Los registros históricos que no poseen fecha real de devolución se identifican separadamente.

### Reportes de biblioteca

La aplicación dispone de tres reportes principales:

**Libros más prestados**

Presenta información sobre los libros con mayor cantidad de préstamos registrados.

**Préstamos activos**

Permite consultar los préstamos que todavía no han sido devueltos.

**Historial por estudiante**

Presenta los préstamos vinculados con un estudiante, incluyendo fechas, vencimientos, devoluciones y estado.

---

## Estructura general del proyecto

El código fuente se encuentra organizado mediante paquetes con responsabilidades diferenciadas.

| Paquete | Responsabilidad |
|---|---|
| `app` | Punto de entrada de la aplicación |
| `conexion` | Configuración y obtención de conexiones JDBC |
| `model` | Entidades, clase abstracta e interfaz |
| `dao` | Operaciones de acceso a MySQL |
| `controlador` | Coordinación entre interfaz y operaciones |
| `servicio` | Lógica de negocio y sincronización de préstamos |
| `vista` | Ventanas y componentes de Java Swing |

### Archivos principales

**Punto de entrada**

- `app/Main.java`

**Conexión**

- `conexion/DatabaseConnection.java`

**Modelos**

- `model/Persona.java`
- `model/Describible.java`
- `model/Usuario.java`
- `model/Estudiante.java`
- `model/Libro.java`
- `model/Categoria.java`
- `model/Prestamo.java`

**Acceso a datos**

- `dao/UsuarioDAO.java`
- `dao/EstudianteDAO.java`
- `dao/EstudianteCuentaDAO.java`
- `dao/LibroDAO.java`
- `dao/CategoriaDAO.java`
- `dao/PrestamoDAO.java`
- `dao/ReporteDAO.java`

**Controladores**

- `controlador/ControladorLogin.java`
- `controlador/ControladorCategoria.java`
- `controlador/ControladorLibro.java`
- `controlador/ControladorEstudiante.java`
- `controlador/ControladorPrestamo.java`
- `controlador/ControladorReporte.java`

**Servicio**

- `servicio/ServicioPrestamos.java`

**Interfaz gráfica**

- `vista/VentanaLogin.java`
- `vista/VentanaPrincipal.java`
- `vista/VentanaCategorias.java`
- `vista/VentanaLibros.java`
- `vista/VentanaEstudiantes.java`
- `vista/VentanaPrestamos.java`
- `vista/VentanaReportes.java`

---

## Programación orientada a objetos

### Encapsulamiento

Los modelos utilizan atributos privados para controlar el acceso a la información.

El acceso a los datos se realiza mediante constructores y métodos públicos definidos según las necesidades de cada entidad.

### Herencia

La clase abstracta `Persona` representa características comunes de personas registradas en el sistema.

Las clases `Usuario` y `Estudiante` heredan de `Persona` mediante `extends`.

Esto permite reutilizar atributos y comportamientos comunes.

### Abstracción

`Persona` es una clase abstracta y no se utiliza para crear objetos directamente.

Define una estructura común para sus subclases.

### Interfaces

La interfaz `Describible` establece el comportamiento `obtenerDescripcion()`.

La clase `Persona` implementa esta interfaz y sus subclases proporcionan las implementaciones correspondientes.

### Polimorfismo

El sistema utiliza referencias del tipo `Persona` para trabajar con objetos de sus subclases.

Un ejemplo se encuentra en `VentanaPrincipal`, donde el método `crearBienvenida(Persona persona)` recibe una referencia de la clase base e invoca `obtenerDescripcion()`.

La implementación ejecutada corresponde al tipo concreto del objeto recibido.

---

## Patrones de diseño implementados

### Patrón MVC

La arquitectura separa las responsabilidades en tres componentes:

**Modelo**

Representa las entidades y estructuras de datos del sistema.

**Vista**

Implementa las ventanas, formularios, botones, tablas y demás componentes de Java Swing.

**Controlador**

Coordina las acciones realizadas desde la interfaz y comunica las operaciones correspondientes a los componentes del sistema.

La distribución en distintas clases y ventanas evita concentrar todas las funcionalidades dentro de una única ventana principal.

### Patrón DAO

Las clases DAO centralizan el acceso a la base de datos MySQL.

Permiten ejecutar operaciones de creación, consulta, actualización y eliminación sin incorporar directamente las sentencias SQL en las ventanas de la interfaz.

Para estas operaciones se emplean componentes JDBC como:

- `Connection`
- `PreparedStatement`
- `ResultSet`
- `SQLException`

La separación facilita el mantenimiento y la organización del proyecto.

### Patrón Singleton

La clase `DatabaseConnection` utiliza el patrón Singleton.

Dispone de:

- Constructor privado.
- Instancia estática de la clase.
- Método público `getInstance()`.

Esto garantiza una única instancia del administrador de conexiones.

El método `getConnection()` entrega conexiones JDBC para realizar las operaciones necesarias. Las operaciones pueden utilizar conexiones independientes, lo que permite gestionar correctamente sus respectivas transacciones.

---

## Concurrencia y sincronización

La aplicación implementa mecanismos de concurrencia para evitar bloquear la interfaz gráfica durante las operaciones de préstamo.

### Procesamiento en segundo plano

El módulo de préstamos utiliza `SwingWorker` para ejecutar operaciones que involucran acceso a la base de datos sin mantener ocupado el hilo principal de eventos de Java Swing.

Esto permite separar las tareas de procesamiento de la interacción gráfica.

### Sincronización

`ServicioPrestamos` utiliza métodos sincronizados mediante `synchronized` para coordinar operaciones críticas de préstamos y devoluciones.

La sincronización evita que múltiples hilos de una misma instancia del servicio interfieran simultáneamente en las operaciones protegidas.

### Transacciones y consistencia de datos

Las operaciones de préstamo y devolución utilizan transacciones SQL para mantener relacionados los cambios realizados.

Esto permite coordinar el registro del préstamo con la actualización del stock y evitar estados parcialmente guardados en caso de error.

El control de consistencia combina sincronización en Java con operaciones transaccionales en MySQL.

### Prueba de concurrencia

Durante el desarrollo se realizó una prueba con dos solicitudes simultáneas para un libro cuyo stock inicial era de una unidad.

El resultado observado fue:

- Préstamos exitosos: 1.
- Stock final: 0.
- Préstamos registrados: 1.
- Resultado de la prueba: superada.

Esta comprobación permitió verificar que no se registraran dos préstamos exitosos sobre la misma unidad disponible.

---

## Persistencia y base de datos

El sistema utiliza MySQL mediante JDBC.

La base de datos se denomina:

**`biblioteca`**

### Tablas utilizadas

| Tabla | Descripción |
|---|---|
| `usuarios` | Cuentas, credenciales y roles |
| `estudiantes` | Datos personales y académicos |
| `categorias` | Clasificación de libros |
| `libros` | Catálogo e inventario |
| `prestamos` | Registro de préstamos y devoluciones |

Las tablas utilizan claves primarias y relaciones mediante claves foráneas.

Estas relaciones permiten mantener la integridad referencial entre estudiantes, libros, categorías y préstamos.

### Actualización del esquema

Además de los scripts originales proporcionados para la evaluación, se incorpora el archivo `Script_actualizacion_biblioteca.sql`.

Este script agrega a `prestamos` la columna `fecha_devolucion_real`, utilizada para conservar la fecha efectiva de devolución.

El campo es adicional a las tablas mínimas exigidas para la evaluación.

---

## Consultas SQL y gestión de recursos

Las clases DAO utilizan sentencias SQL parametrizadas mediante `PreparedStatement`.

Esto permite separar las instrucciones SQL de los valores proporcionados durante las operaciones de la aplicación.

Los resultados de las consultas se recuperan mediante `ResultSet`.

Las operaciones JDBC utilizan manejo de excepciones y cierre de recursos, según corresponda, para evitar dejar conexiones y consultas abiertas innecesariamente.

---

## Validaciones y manejo de errores

La aplicación incorpora validaciones en sus formularios y controladores para comprobar los datos antes de realizar operaciones sobre MySQL.

Las operaciones contemplan situaciones como:

- Campos obligatorios sin completar.
- Valores ingresados incorrectamente.
- Registros duplicados sujetos a restricciones de la base de datos.
- Falta de disponibilidad de libros.
- Operaciones no permitidas por las relaciones entre registros.
- Errores durante consultas o actualizaciones de MySQL.

La interfaz utiliza mensajes de Java Swing para comunicar resultados y problemas al usuario.

Las excepciones producidas durante las operaciones JDBC se manejan en las capas correspondientes.

---

## Interfaz gráfica

La interfaz está desarrollada utilizando Java Swing.

Entre los componentes utilizados se encuentran:

- `JFrame`
- `JPanel`
- `JLabel`
- `JTextField`
- `JPasswordField`
- `JButton`
- `JTable`
- `JComboBox`
- `JScrollPane`
- `JOptionPane`

La aplicación utiliza ventanas separadas por funcionalidad para facilitar la navegación y evitar concentrar todos los módulos en una única clase de interfaz.

El acceso a las opciones depende del rol del usuario autenticado.

---

## Compatibilidad con Java

El proyecto fue desarrollado inicialmente con Oracle OpenJDK 23.0.2.

Posteriormente se adaptó su código para utilizar el nivel de lenguaje **Java 21, sin funciones preliminares**.

Se reemplazaron las expresiones lambda que utilizaban `_` como parámetro por sintaxis compatible con Java 21.

La configuración del compilador utiliza el nivel de lenguaje Java 21 y la opción `--release` para generar bytecode compatible.

Durante la verificación se comprobó que las clases compiladas utilizaban **bytecode versión 65**, correspondiente a Java 21.

También se comprobó que el archivo JAR actualizado contenía bytecode versión 65 en su clase principal.

**Verificación de ejecución:** el programa fue ejecutado satisfactoriamente con Java 27. La ejecución directa utilizando un entorno Java 21 todavía no ha sido comprobada.

La aplicación no requiere activar funciones preliminares de Java.

---

## Requisitos para ejecutar el proyecto

- Java 21 o superior.
- Para compilar el código: JDK 21 o superior.
- MySQL Server instalado y en funcionamiento.
- Permisos de acceso a la base de datos.
- Scripts SQL proporcionados en la carpeta `sql`.
- IntelliJ IDEA, en caso de trabajar con el código fuente.

Para ejecutar el archivo JAR no es necesario abrir IntelliJ.

El controlador MySQL Connector/J está incluido dentro del ejecutable, y también se proporciona como dependencia en la carpeta `lib`.

---

## Instalación de la base de datos

Abre MySQL Workbench u otro cliente compatible.

Ejecuta los siguientes archivos en este orden:

**1. Creación de tablas**

`sql/PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql`

Este script crea la base de datos `biblioteca` y sus tablas principales.

**2. Poblamiento**

`sql/PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql`

Incorpora los datos de prueba entregados para la evaluación.

**3. Actualización del esquema**

`sql/Script_actualizacion_biblioteca.sql`

Agrega el campo `fecha_devolucion_real` a la tabla `prestamos`.

El tercer script debe ejecutarse después de crear las tablas y solamente si la columna todavía no existe.

Estos scripts están preparados para inicializar el sistema. No deben repetirse indiscriminadamente sobre una base de datos que ya contiene las tablas y registros correspondientes.

---

## Configuración de la conexión MySQL

La configuración de JDBC se administra mediante:

`src/conexion/DatabaseConnection.java`

La aplicación lee los valores desde un archivo externo llamado:

`db.properties`

Se proporciona una plantilla:

`db.properties.example`

### Procedimiento

1. Copia `db.properties.example`.
2. Renombra la copia como `db.properties`.
3. Configura la dirección de MySQL, el puerto, el usuario y la contraseña.
4. Guarda `db.properties` en la carpeta desde la que ejecutarás el programa.

La plantilla contiene:

| Propiedad | Valor de ejemplo |
|---|---|
| `db.url` | `jdbc:mysql://127.0.0.1:3306/biblioteca` |
| `db.user` | `root` |
| `db.password` | Vacío; debe configurarse según MySQL |

El puerto `3306` es un ejemplo habitual. Puede variar según la instalación.

La dirección `127.0.0.1` corresponde al computador donde se ejecuta la aplicación.

El archivo `db.properties` con credenciales personales está excluido de Git mediante `.gitignore`.

No es necesario modificar ni recompilar el código Java para cambiar la conexión.

---

## Ejecución del sistema desde Windows

En la carpeta de entrega se incluye:

`BibliotecaEscolarEFT.jar`

También se proporciona:

`IniciarBiblioteca.bat`

### Procedimiento

1. Verifica que Java esté instalado.
2. Inicia el servicio MySQL.
3. Ejecuta los scripts SQL si todavía no has instalado la base de datos.
4. Prepara `db.properties` con los datos de tu instalación.
5. Mantén `db.properties` junto al archivo JAR.
6. Ejecuta `IniciarBiblioteca.bat` mediante doble clic.

El archivo por lotes establece la carpeta de ejecución, comprueba que exista `db.properties` y ejecuta el programa.

También es posible iniciar la aplicación desde una terminal abierta en la carpeta del ejecutable mediante:

`java -jar BibliotecaEscolarEFT.jar`

---

## Apertura del proyecto en IntelliJ IDEA

### 1. Abrir el proyecto

Abre IntelliJ IDEA y selecciona la carpeta que contiene el código fuente del proyecto.

### 2. Configurar el JDK

En **File → Project Structure → Project**, selecciona un JDK compatible.

El proyecto utiliza el nivel de lenguaje Java 21 sin funciones preliminares.

### 3. Verificar dependencias

En **File → Project Structure → Modules → Dependencies**, comprueba que esté disponible:

`lib/mysql-connector-j-26.7.0.jar`

### 4. Configurar MySQL

Prepara el archivo `db.properties` en la raíz del proyecto, según los datos de tu instalación.

### 5. Ejecutar

Ejecuta la clase:

`app.Main`

### 6. Generar el ejecutable

La configuración de artefactos de IntelliJ permite reconstruir el archivo JAR mediante:

**Build → Build Artifacts → BibliotecaEscolarEFT:jar → Rebuild**

El JAR incluye las clases del proyecto y las dependencias extraídas correspondientes.

---

## Usuarios de demostración

El script de poblamiento incorpora cuentas de ejemplo para comprobar la autenticación y los permisos.

### Bibliotecario

- **Nombre:** Antonia Pérez
- **RUT:** `12345678-9`
- **Contraseña:** `clave123`

### Estudiante

- **Nombre:** Carlos Ruiz
- **RUT:** `98765432-1`
- **Contraseña:** `clave123`

Estas credenciales corresponden exclusivamente a datos de demostración académica.

La aplicación utiliza el esquema de autenticación solicitado para la actividad. No debe considerarse un sistema de autenticación preparado para producción sin mejoras adicionales de seguridad, como el almacenamiento de contraseñas mediante hashes seguros.

---

## Pruebas y verificaciones

Durante el desarrollo se realizaron comprobaciones de:

- Compilación y reconstrucción del proyecto.
- Inicio de sesión.
- Acceso diferenciado por roles.
- Registro y consulta de libros.
- Gestión de categorías.
- Gestión de estudiantes y cuentas.
- Registro de préstamos.
- Registro de devoluciones.
- Actualización de stock.
- Visualización de reportes.
- Registro de fecha real de devolución.
- Comportamiento frente a solicitudes concurrentes.
- Ejecución del JAR fuera de IntelliJ.
- Ejecución desde una carpeta independiente del proyecto.
- Ejecución desde una copia extraída de un archivo ZIP.
- Compilación para bytecode de Java 21.

Las pruebas del ejecutable se realizaron utilizando una instalación local de MySQL y una configuración externa de conexión.

---

## Organización de la entrega

La carpeta de entrega incluye los siguientes componentes:

| Archivo o carpeta | Descripción |
|---|---|
| `src/` | Código fuente Java completo |
| `lib/` | Conector JDBC de MySQL |
| `sql/` | Scripts de creación, poblamiento y actualización |
| `.idea/` | Configuración compartida del proyecto IntelliJ |
| `BibliotecaEscolarEFT.iml` | Configuración del módulo |
| `BibliotecaEscolarEFT.jar` | Ejecutable de la aplicación |
| `IniciarBiblioteca.bat` | Archivo de inicio para Windows |
| `db.properties.example` | Plantilla de conexión |
| `README.md` | Documentación del proyecto |

Las credenciales privadas de MySQL no se incluyen en el paquete de entrega.

---

## Tecnologías utilizadas

- Java 21 como nivel de lenguaje y versión de destino.
- Oracle OpenJDK.
- Java Swing.
- JDBC.
- MySQL.
- MySQL Connector/J 26.7.0.
- IntelliJ IDEA.
- Git.
- GitHub.
- Programación orientada a objetos.
- MVC.
- DAO.
- Singleton.
- Concurrencia y sincronización.

---

## Repositorio GitHub

Repositorio público:

https://github.com/enunezj/BibliotecaEscolarEFT

El repositorio contiene el código fuente, los scripts SQL y los archivos de configuración compartidos necesarios para trabajar con el proyecto.

---

## Contexto académico

Proyecto desarrollado para la Evaluación Final Transversal de Desarrollo Orientado a Objetos II, semana 9, Duoc UC.

**Fecha:** octubre de 2026.
```
