# BibliotecaEscolarEFT — Sistema de Gestión de Biblioteca Escolar

## Descripción del proyecto

Aplicación de escritorio desarrollada en Java para gestionar una biblioteca escolar, como parte de la Evaluación Final Transversal de Desarrollo Orientado a Objetos II (PRY2203) de Duoc UC.

El sistema permite administrar libros, categorías, estudiantes, préstamos y devoluciones, utilizando una interfaz gráfica Java Swing y una base de datos MySQL.

El desarrollo aplica principios de programación orientada a objetos, patrones de diseño y mecanismos de concurrencia para mantener la consistencia de los datos.

## Tecnologías utilizadas

- Java y Java Swing (JFrame).
- MySQL y JDBC.
- MySQL Connector/J.
- IntelliJ IDEA.
- Git y GitHub.

## Funcionalidades principales

**Autenticación y roles**

- Inicio de sesión mediante usuarios registrados en MySQL.
- Diferenciación entre bibliotecarios y estudiantes.
- Restricción de funcionalidades según el rol.

**Gestión de biblioteca**

- Administración de libros y categorías.
- Registro, consulta, modificación y eliminación de datos.
- Gestión de estudiantes y cuentas de usuario.
- Control de disponibilidad y stock de libros.

**Préstamos y devoluciones**

- Registro de préstamos y cálculo de vencimiento.
- Actualización del stock disponible.
- Registro de devoluciones y fecha real de devolución.
- Identificación de préstamos atrasados.
- Procesamiento en segundo plano para evitar bloquear la interfaz.

**Reportes**

- Libros más prestados.
- Préstamos activos.
- Historial de préstamos por estudiante.

## Arquitectura y conceptos implementados

- **POO:** encapsulación, herencia, polimorfismo, clases abstractas e interfaces.
- **MVC:** separación entre modelos, vistas y controladores.
- **DAO:** acceso a datos mediante clases especializadas.
- **Singleton:** instancia única del administrador de conexiones.
- **Concurrencia:** uso de hilos, SwingWorker, sincronización y transacciones SQL para proteger las operaciones de préstamo.

## Requisitos para ejecutar la aplicación

- Windows con Java 23 o superior instalado.
- Servidor MySQL en funcionamiento.
- Una base de datos denominada `biblioteca`, creada mediante los scripts incluidos.
- Acceso a una cuenta MySQL con permisos sobre dicha base de datos.

El conector JDBC está incorporado al ejecutable JAR. El proyecto también incluye una copia del conector en la carpeta `lib`.

## Instalación de la base de datos

Abre MySQL Workbench o un cliente compatible y ejecuta los scripts de la carpeta `sql` en este orden:

1. `PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql`
2. `PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql`
3. `Script_actualizacion_biblioteca.sql`

El primer script crea la base de datos y las tablas. El segundo incorpora registros de ejemplo. El tercero añade la columna `fecha_devolucion_real`, utilizada para registrar las devoluciones.

Estos scripts están pensados para instalar la base de datos desde cero. No es necesario volver a ejecutarlos si las tablas ya están creadas y actualizadas.

## Configuración de MySQL

En la carpeta del ejecutable encontrarás `db.properties.example`.

Crea una copia de ese archivo y renómbrala como `db.properties`.

Configura los valores correspondientes a tu instalación:

- `db.url`: dirección, puerto y nombre de la base de datos.
- `db.user`: usuario de MySQL.
- `db.password`: contraseña del usuario de MySQL.

El ejemplo utiliza `127.0.0.1`, puerto `3306` y usuario `root`. Estos valores pueden modificarse según el entorno.

El archivo `db.properties` debe ubicarse en la misma carpeta desde la cual se inicia el programa mediante el archivo por lotes.

Por seguridad, las credenciales personales no se incluyen en el código fuente ni en el repositorio público.

## Ejecución del programa

Una vez preparada la base de datos y configurado `db.properties`:

1. Verifica que el servicio MySQL esté iniciado.
2. Abre la carpeta de entrega.
3. Ejecuta `IniciarBiblioteca.bat` mediante doble clic.
4. Se abrirá la ventana de inicio de sesión de Biblioteca Escolar.

También puedes iniciar la aplicación desde una terminal situada en la carpeta del ejecutable con el comando `java -jar BibliotecaEscolarEFT.jar`.

## Acceso de demostración

El script de poblamiento incorpora usuarios de ejemplo.

**Bibliotecario**

- RUT: `12345678-9`
- Contraseña: `clave123`

**Estudiante**

- RUT: `98765432-1`
- Contraseña: `clave123`

Estas credenciales corresponden exclusivamente a los datos de prueba académicos.

## Estructura del proyecto

| Carpeta o archivo | Descripción |
|---|---|
| `src/app` | Punto de entrada de la aplicación |
| `src/conexion` | Configuración de conexión JDBC |
| `src/model` | Entidades y elementos de POO |
| `src/dao` | Acceso a la base de datos |
| `src/controlador` | Controladores MVC |
| `src/vista` | Interfaces gráficas Swing |
| `src/servicio` | Lógica de negocio y préstamos |
| `lib` | Conector JDBC de MySQL |
| `sql` | Scripts de creación, datos y actualización |
| `db.properties.example` | Plantilla de conexión a MySQL |
| `BibliotecaEscolarEFT.jar` | Ejecutable incluido en la entrega comprimida |
| `IniciarBiblioteca.bat` | Inicio de la aplicación en Windows |

## Repositorio

https://github.com/enunezj/BibliotecaEscolarEFT

## Contexto académico

Proyecto desarrollado para la Evaluación Final Transversal de Desarrollo Orientado a Objetos II, Duoc UC, semana 9, año 2026.