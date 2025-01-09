package com.alesandro.biblioteca.dao;

import com.alesandro.biblioteca.db.DBConnect;
import com.alesandro.biblioteca.model.Alumno;
import com.alesandro.biblioteca.model.Libro;
import com.alesandro.biblioteca.model.HistorialPrestamo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Clase donde se ejecuta las consultas para la tabla HistorialPrestamo
 */
public class DaoHistorialPrestamo {
    /**
     * Metodo que busca un prestamo por medio de su id
     *
     * @param id_prestamo id_prestamo del prestamo a buscar
     * @return prestamo o null
     */
    public static HistorialPrestamo getHistorialPrestamo(String id_prestamo) {
        DBConnect connection;
        HistorialPrestamo prestamo = null;
        try {
            connection = new DBConnect();
            String consulta = "SELECT id_prestamo,dni_alumno,codigo_libro,fecha_prestamo,fecha_devolucion FROM Historial_prestamos WHERE id_prestamo = ?";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, id_prestamo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id_prestamo_db = rs.getInt("id_prestamo");
                String dni_alumno = rs.getString("dni_alumno");
                Alumno alumno = DaoAlumno.getAlumno(dni_alumno);
                int codigo_libro = rs.getInt("codigo_libro");
                Libro libro = DaoLibro.getLibro(codigo_libro);
                LocalDateTime fecha_prestamo = rs.getTimestamp("fecha_prestamo").toLocalDateTime();
                LocalDateTime fecha_devolucion = rs.getTimestamp("fecha_devolucion").toLocalDateTime();
                prestamo = new HistorialPrestamo(id_prestamo_db, alumno, libro, fecha_prestamo, fecha_devolucion);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return prestamo;
    }

    /**
     * Metodo que carga los datos de la tabla HistorialPrestamos y los devuelve para usarlos en un listado de prestamos
     *
     * @return listado de prestamos para cargar en un tableview
     */
    public static ObservableList<HistorialPrestamo> cargarListado() {
        DBConnect connection;
        ObservableList<HistorialPrestamo> prestamos = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT id_prestamo,dni_alumno,codigo_libro,fecha_prestamo,fecha_devolucion FROM Historial_prestamos";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id_prestamo_db = rs.getInt("id_prestamo");
                String dni_alumno = rs.getString("dni_alumno");
                Alumno alumno = DaoAlumno.getAlumno(dni_alumno);
                int codigo_libro = rs.getInt("codigo_libro");
                Libro libro = DaoLibro.getLibro(codigo_libro);
                LocalDateTime fecha_prestamo = rs.getTimestamp("fecha_prestamo").toLocalDateTime();
                LocalDateTime fecha_devolucion = rs.getTimestamp("fecha_devolucion").toLocalDateTime();
                HistorialPrestamo prestamo = new HistorialPrestamo(id_prestamo_db, alumno, libro, fecha_prestamo, fecha_devolucion);
                prestamos.add(prestamo);
            }
            rs.close();
            connection.closeConnection();
        }catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return prestamos;
    }

    /**
     * Metodo que modifica los datos de un prestamo en la BD
     *
     * @param prestamo		Instancia del prestamo con datos a actualizar
     * @return			true/false
     */
    public static boolean modificar(HistorialPrestamo prestamo) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "UPDATE Historial_prestamos SET dni_alumno = ?,codigo_libro = ?,fecha_prestamo = ?,fecha_devolucion = ? WHERE id_prestamo = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, prestamo.getAlumno().getDni());
            ps.setInt(2, prestamo.getLibro().getCodigo());
            ps.setTimestamp(3, Timestamp.valueOf(prestamo.getFecha_prestamo()));
            ps.setTimestamp(4, Timestamp.valueOf(prestamo.getFecha_devolucion()));
            ps.setInt(5, prestamo.getId_prestamo());
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Actualizado prestamo");
            ps.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Metodo que CREA un nuevo prestamo en la BD
     *
     * @param prestamo		Instancia del modelo historial prestamo con datos nuevos
     * @return			true/false
     */
    public  static boolean insertar(HistorialPrestamo prestamo) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "INSERT INTO Historial_prestamos (id_prestamo,dni_alumno,codigo_libro,fecha_prestamo,fecha_devolucion) VALUES (?,?,?,?,?) ";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setInt(1, prestamo.getId_prestamo());
            ps.setString(2, prestamo.getAlumno().getDni());
            ps.setInt(3, prestamo.getLibro().getCodigo());
            ps.setTimestamp(4, Timestamp.valueOf(prestamo.getFecha_prestamo()));
            ps.setTimestamp(5, Timestamp.valueOf(prestamo.getFecha_devolucion()));
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Nueva entrada en prestamo");
            ps.close();
            connection.closeConnection();
            return (filasAfectadas > 0);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un prestamo en función del modelo HistorialPrestamo que le hayamos pasado
     *
     * @param prestamo HistorialPrestamo a eliminar
     * @return a boolean
     */
    public static boolean eliminar(HistorialPrestamo prestamo) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "DELETE FROM Historial_prestamos WHERE id_prestamo = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setInt(1, prestamo.getId_prestamo());
            int filasAfectadas = ps.executeUpdate();
            ps.close();
            connection.closeConnection();
            System.out.println("Eliminado con éxito");
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
