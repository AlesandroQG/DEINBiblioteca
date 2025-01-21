# Proyecto 2 - Biblioteca
## DM2 DEIN 2024-2025
### Alesandro Quirós Gobbato

Esta es una aplicación para la gestión de una biblioteca hecha con JavaFX y JasperReports.

### Tecnologías usadas

Estas son las tecnologías usadas para este proyecto:
- SDK: Oracle OpenJDK 23.0.1
- Base de datos: MariaDB
- Informes: JasperReport 7.0.1 (ejecución funciona en VSCode)

### Configuración

Para la conexión a la base de datos, hay que editar el fichero `configuration.properties` localizado en la ruta base.
La plantilla es la siguiente:
```
address=
port=
user=
password=
database=
```

### Compilación y ejecución

Para compilar la aplicación, en la terminal hay que ejecutar este comando dentro de la carpeta base:
```bash
mvn clean package
```

Esto generará un fichero .jar, y este se puede ejecutar usando:
```bash
java -jar ./target/Biblioteca-1.0-jar-with-dependencies.jar
```

### Diagrama de Clases

Este es el diagrama de clases de la aplicación:
![Diagrama de Clases](https://github.com/AlesandroQG/DEINBiblioteca/blob/main/src/main/resources/com/alesandro/biblioteca/docs/DiagramaClases.png?raw=true)

### Diagrama de Casos

Este es el diagrama de casos de la aplicación:
![Diagrama de Casos](https://github.com/AlesandroQG/DEINBiblioteca/blob/main/src/main/resources/com/alesandro/biblioteca/docs/DiagramaCasos.png?raw=true)

### Estructura de la aplicación

La estructura del proyecto es la siguiente:
- `configuration.properties`: Fichero con las propiedades para la conexión a la base de datos (haga clic [aquí](#Configuración) para ver la configuración de este archivo)
- `lang.properties`: Fichero con la propiedad de idioma (creada al lanzar la aplicación)
- `src > main`:
    - `java > com.alesandro.biblioteca`: Código fuente de la aplicación
        - `BibliotecaApplicacion.java`: Clase que lanza la aplicación
        - `controller`: Controladores de las ventanas
          - `MainController.java`: Clase que controla los eventos de la ventana principal de la aplicación
          - `LibroController.java`: Clase que controla los eventos de la ventana libro
          - `AlumnoController.java`: Clase que controla los eventos de la ventana alumno
          - `PrestamoController.java`: Clase que controla los eventos de la ventana préstamo
          - `NuevoHistorialPrestamoController.java`: Clase que controla los eventos de la ventana para añadir un historial préstamo
          - `EditarHistorialPrestamoController.java`: Clase que controla los eventos de la ventana para editar un historial préstamo
          - `AyudaHTMLController.java`: Clase que controla los eventos de la ventana ayuda HTML
          - `FirstLaunchController.java`: Clase que controla los eventos de la ventana de 
        - `dao`: Clases que los modelos usan para las consultas con la base de datos
          - `DaoAlumno.java`: Clase que realiza las operaciones con la base de datos del modelo Producto
          - `DaoHistorialPrestamo.java`: Clase que realiza las operaciones con la base de datos del modelo Historial Préstamo
          - `DaoLibro.java`: Clase que realiza las operaciones con la base de datos del modelo Libro
          - `DaoPrestamo.java`: Clase que realiza las operaciones con la base de datos del modelo Préstamo
        - `db`: Paquete conteniendo la clase para la conexión a la base de datos
          - `DBConnect.java`: Clase que se conecta a la base de datos
        - `language`: Clases que manejan el multiidioma de la aplicación
          - `LanguageManager.java`: Clase que maneja los idiomas de la aplicación
          - `LanguageSwitcher.java`: Clase que permite cambiar entre los idiomas de la aplicación
        - `model`: Modelos de la aplicación
          - `Alumno.java`: Clase que define el objeto Alumno
          - `HistorialPrestamo.java`: Clase que define el objeto Historia Préstamo
          - `Libro.java`: Clase que define el objeto Libro
          - `Prestamo.java`: Clase que define el objeto Préstamo
          - `Help.java`: Clase para uso en la ayuda HTML
        - `utils`: Paquete con utilidades de la aplicación
          - `FechaFormatter.java`: Clase para formatear fechas
    - `resources > com.alesandro.biblioteca`: Recursos de la aplicación
        - `docs`: Carpeta con la documentación del proyecto
        - `fonts`: Carpeta que contiene las fuentes de la aplicación
        - `fxml`: Ficheros de las vistas
          - `com.alesandro.biblioteca.Main.fxml`: Ventana principal de la aplicación
          - `Libro.fxml`: Ventana para consulta y edición de libros
          - `Alumno.fxml`: Ventana para consulta y edición de libros
          - `Prestamo.fxml`: Ventana para consulta y edición de libros
          - `NuevoHistorialPrestamo.fxml`: Ventana para añadir un historial de préstamos
          - `EditarHistorialPrestamo.fxml`: Ventana para consulta y edición del historial de préstamos
          - `AyudaHTML.fxml`: Ventana para consulta de la guía de usuario en formato HTML
          - `FirstLaunch.fxml`: Ventana para la configuración inicial de la aplicación
        - `help`: Directorio conteniendo la ayuda de la aplicación
          - `html`: Directorio con los ficheros html para la ayuda de la aplicación
          - `pdf`: Directorio con los ficheros pdf para la ayuda de la aplicación
        - `images`: Carpeta que contiene las imágenes de la aplicación
        - `languages`: Carpeta que contiene los idiomas de la aplicación
        - `reports`: Directorio con los informes de la aplicación
          - `InformeLibros.jasper`: Fichero del informe de JasperReport de libros
          - `InformeLibros.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de libros
          - `InformeAlumnos.jasper`: Fichero del informe de JasperReport de alumnos
          - `InformeAlumnos.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de alumnos
          - `InformeGraficos.jasper`: Fichero del informe de JasperReport de gráficos
          - `InformeGraficos.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de gráficos
          - `InformeAltaPrestamo.jasper`: Fichero del informe de JasperReport de alta de préstamo
          - `InformeAltaPrestamo.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de alta de préstamo
        - `sql`: Directorio conteniendo los archivos sql para la creación local de la base de datos
          - `libros_con_imagen.sql`: Fichero para la creación de la base de datos
        - `style`: Carpeta que contiene los estilos de la aplicación
          - `guia.css`: Fichero de estilo para la guía de usuario de la aplicación
          - `style.css`: Fichero de estilo de la aplicación
