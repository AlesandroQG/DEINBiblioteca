# Proyecto 2 - Biblioteca
## DM2 DEIN 2024-2025
### Alesandro Quirós Gobbato

Esta es una aplicación para la gestión de una biblioteca hecha con JavaFX y JasperReports.

### Tecnologías usadas

Estas son las technologías usadas para este proyecto:
- SDK: Oracle OpenJDK 23.0.1
- Base de datos: MariaDB
- JasperReport: 7.0.1 (ejecución funciona en VSCode)

#### Estructura

La estructura del proyecto es la siguiente:
- `configuration.properties`: Fichero con las propiedades para la conexión a la base de datos
- `lang.properties`: Fichero con la propiedad de idioma
- `src > main`:
    - `java > com.alesandro.biblioteca`:
        - `BibliotecaApplicacion.java`: Clase que lanza la aplicación
        - `controller`:
          - `MainController.java`: Clase que controla los eventos de la ventana principal de la aplicación
          - `LibroController.java`: Clase que controla los eventos de la ventana libro
          - `AlumnoController.java`: Clase que controla los eventos de la ventana alumno
          - `PrestamoController.java`: Clase que controla los eventos de la ventana préstamo
          - `HistorialPrestamoController.java`: Clase que controla los eventos de la ventana historial préstamo
          - `AyudaHTMLController.java`: Clase que controla los eventos de la ventana ayuda HTML
        - `dao`:
          - `DaoAlumno.java`: Clase que realiza las operaciones con la base de datos del modelo Producto
          - `DaoHistorialPrestamo.java`: Clase que realiza las operaciones con la base de datos del modelo Historial Préstamo
          - `DaoLibro.java`: Clase que realiza las operaciones con la base de datos del modelo Libro
          - `DaoPrestamo.java`: Clase que realiza las operaciones con la base de datos del modelo Préstamo
        - `db`:
          - `DBConnect.java`: Clase que se conecta a la base de datos
        - `language`:
          - `LanguageManager.java`: Clase que maneja los idiomas de la aplicación
          - `LanguageSwitcher.java`: Clase que permite cambiar entre los idiomas de la aplicación
        - `model`:
          - `Alumno.java`: Clase que define el objeto Alumno
          - `HistorialPrestamo.java`: Clase que define el objeto Historia Préstamo
          - `Libro.java`: Clase que define el objeto Libro
          - `Prestamo.java`: Clase que define el objeto Préstamo
    - `resources > com.alesandro.biblioteca`:
        - `fonts`: Carpeta que contiene las fuentes de la aplicación
        - `fxml`:
          - `Main.fxml`: Ventana principal de la aplicación
          - `Libro.fxml`: Ventana para consulta y edición de libros
          - `Alumno.fxml`: Ventana para consulta y edición de libros
          - `Prestamo.fxml`: Ventana para consulta y edición de libros
          - `HistorialPrestao.fxml`: Ventana para consulta y edición del historial de préstamos
          - `AyudaHTML.fxml`: Ventana para consulta de la guía de usuario en formato HTML
        - `help`:
          - `html`: Directorio con los ficheros html para la ayuda de la aplicación
          - `pdf`: Directorio con los ficheros pdf para la ayuda de la aplicación
        - `images`: Carpeta que contiene las imágenes de la aplicación
        - `languages`: Carpeta que contiene los idiomas de la aplicación
        - `reports`:
          - `InformeLibros.jasper`: Fichero del informe de JasperReport de libros
          - `InformeLibros.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de libros
          - `InformeAlumnos.jasper`: Fichero del informe de JasperReport de alumnos
          - `InformeAlumnos.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de alumnos
          - `InformeGraficos.jasper`: Fichero del informe de JasperReport de gráficos
          - `InformeGraficos.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de gráficos
          - `InformeAltaPrestamo.jasper`: Fichero del informe de JasperReport de alta de préstamo
          - `InformeAltaPrestamo.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de alta de préstamo
        - `sql`:
          - `libros_con_imagen.sql`: Fichero para la creación de la base de datos
        - `style`: Carpeta que contiene los estilos de la aplicación
