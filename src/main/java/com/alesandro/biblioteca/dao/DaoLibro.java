package com.alesandro.biblioteca.dao;

import com.alesandro.biblioteca.db.DBConnect;
import com.alesandro.biblioteca.model.Libro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase donde se ejecuta las consultas para la tabla Libro
 */
public class DaoLibro {
    /**
     * Metodo que busca un libro por medio de su id
     *
     * @param codigo codigo del libro a buscar
     * @return libro o null
     */
    public static Libro getLibro(int codigo) {
        DBConnect connection;
        Libro libro = null;
        try {
            connection = new DBConnect();
            String consulta = "SELECT codigo,titulo,autor,editorial,estado,baja,portada FROM Libro WHERE codigo = ?";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ps.setInt(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int codigo_db = rs.getInt("codigo");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String editorial = rs.getString("editorial");
                String estado = rs.getString("estado");
                int baja = rs.getInt("baja");
                Blob portada = rs.getBlob("portada");
                libro = new Libro(codigo_db, titulo, autor, editorial, estado, baja, portada);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return libro;
    }

    /**
     * Metodo que carga los datos de la tabla Libros y los devuelve para usarlos en un listado de libros
     *
     * @return listado de libros para cargar en un tableview
     */
    public static ObservableList<Libro> cargarListado() {
        DBConnect connection;
        ObservableList<Libro> libros = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT codigo,titulo,autor,editorial,estado,baja,portada FROM Libro";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int codigo_db = rs.getInt("codigo");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String editorial = rs.getString("editorial");
                String estado = rs.getString("estado");
                int baja = rs.getInt("baja");
                Blob portada = rs.getBlob("portada");
                Libro libro = new Libro(codigo_db, titulo, autor, editorial, estado, baja, portada);
                libros.add(libro);
            }
            rs.close();
            connection.closeConnection();
        }catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return libros;
    }

    /**
     * Función que convierto un objeto File a Blob
     *
     * @param file fichero imagen
     * @return blob
     */
    public static Blob convertFileToBlob(File file) {
        DBConnect connection;
        try {
            connection = new DBConnect();
            try (Connection conn = connection.getConnection();
                 FileInputStream inputStream = new FileInputStream(file)) {
                // Create Blob
                Blob blob = conn.createBlob();
                // Write the file's bytes to the Blob
                byte[] buffer = new byte[1024];
                int bytesRead;
                try (var outputStream = blob.setBinaryStream(1)) {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                return blob;
            }
        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Metodo que busca un libro y mira a ver si se puede eliminar
     *
     * @param libro libro a buscar
     * @return true/false
     */
    public static boolean esEliminable(Libro libro) {
        DBConnect connection;
        try {
            connection = new DBConnect();
            // Prestamos
            String consulta = "SELECT count(*) as cont FROM Prestamo WHERE codigo_libro = ?";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ps.setInt(1, libro.getCodigo());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int cont = rs.getInt("cont");
                if (cont != 0) {
                    rs.close();
                    connection.closeConnection();
                    return false;
                }
            }
            rs.close();
            ps.close();
            // Historial_prestamos
            consulta = "SELECT count(*) as cont FROM Historico_prestamo WHERE codigo_libro = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setInt(1, libro.getCodigo());
            rs = ps.executeQuery();
            if (rs.next()) {
                int cont = rs.getInt("cont");
                rs.close();
                connection.closeConnection();
                return (cont == 0);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    /**
     * Metodo que modifica los datos de un libro en la BD
     *
     * @param libro		Instancia del libro con datos a actualizar
     * @return			true/false
     */
    public static boolean modificar(Libro libro) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "UPDATE Libro SET titulo = ?,autor = ?,editorial = ?,estado = ?,baja = ?,portada = ? WHERE codigo = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setString(4, libro.getEstado());
            ps.setInt(5, libro.getBaja());
            ps.setBlob(6, libro.getPortada());
            ps.setInt(7, libro.getCodigo());
            int filasAfectadas = ps.executeUpdate();
            ps.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Metodo que CREA un nuevo libro en la BD
     *
     * @param libro		Instancia del modelo libro con datos nuevos
     * @return			id/-1
     */
    public  static int insertar(Libro libro) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "INSERT INTO Libro (titulo,autor,editorial,estado,baja,portada) VALUES (?,?,?,?,?,?) ";
            ps = connection.getConnection().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setString(4, libro.getEstado());
            ps.setInt(5, libro.getBaja());
            ps.setBlob(6, libro.getPortada());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    ps.close();
                    connection.closeConnection();
                    return id;
                }
            }
            ps.close();
            connection.closeConnection();
            return -1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /**
     * Elimina un libro en función del modelo Libro que le hayamos pasado
     *
     * @param libro Libro a eliminar
     * @return a boolean
     */
    public static boolean eliminar(Libro libro) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "DELETE FROM Libro WHERE codigo = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setInt(1, libro.getCodigo());
            int filasAfectadas = ps.executeUpdate();
            ps.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
