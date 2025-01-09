# Proyecto 2 - Biblioteca
## DM2 DEIN 2024-2025
### Alesandro Quirós Gobbato

Esta es una aplicación para la gestión de una biblioteca hecha con JavaFX y JasperReports.

JasperReport está compilado en 7.0.1. La ejecución funciona en VSCode.

#### Estructura

La estructura del proyecto es la siguiente:
- `src > main`:
    - `java > com.alesandro.biblioteca`:
        - `BibliotecaApplicacion.java`: Clase que lanza la aplicación
        - `controller`:
          - `MainController.java`: Clase que controla los eventos de la ventana principal de la aplicación
        - `dao`:
          - `DaoProducto.java`: Clase que realiza las operaciones con la base de datos del modelo Producto
          - `DaoSeccion.java`: Clase que realiza las operaciones con la base de datos del modelo Sección
        - `db`:
          - `DBConnect.java`: Clase que se conecta a la base de datos
        - `model`:
          - `Producto.java`: Clase que define el objeto Producto
          - `Sección.java`: Clase que define el objeto Sección
    - `resources > com.alesandro.biblioteca`:
        - `fxml`:
          - `Main.fxml`: Ventana principal de la aplicación
        - `images`: Carpeta que contiene las imágenes de la aplicación
        - `reports`:
          - `InformeProductos.jasper`: Fichero del informe de JasperReport de productos
          - `InformeProductos.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de productos
          - `InformeSecciones.jasper`: Fichero del informe de JasperReport de las secciones
          - `InformeSecciones.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de las secciones
          - `InformeTablaProductos.jasper`: Fichero del informe de JasperReport de tabla productos
          - `InformeTablaProductos.jrxml`: Fichero para la edición del informe de JasperReport en JasperStudio de tabla productos
        - `sql`:
          - `biblioteca.sql`: Fichero para la creación de la base de datos
