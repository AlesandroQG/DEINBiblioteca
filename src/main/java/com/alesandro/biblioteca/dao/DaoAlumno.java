package com.alesandro.biblioteca.dao;

import com.alesandro.biblioteca.db.DBConnect;
import com.alesandro.biblioteca.model.Alumno;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase donde se ejecuta las consultas para la tabla Alumno
 */
public class DaoAlumno {
    /**
     * Metodo que busca un alumno por medio de su id
     *
     * @param dni dni del alumno a buscar
     * @return alumno o null
     */
    public static Alumno getAlumno(String dni) {
        DBConnect connection;
        Alumno alumno = null;
        try {
            connection = new DBConnect();
            String consulta = "SELECT dni,nombre,apellido1,apellido2 FROM Alumno WHERE dni = ?";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dni_db = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                alumno = new Alumno(dni_db, nombre, apellido1, apellido2);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return alumno;
    }

    /**
     * Metodo que carga los datos de la tabla Alumnos y los devuelve para usarlos en un listado de alumnos
     *
     * @return listado de alumnos para cargar en un tableview
     */
    public static ObservableList<Alumno> cargarListado() {
        DBConnect connection;
        ObservableList<Alumno> alumnos = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT dni,nombre,apellido1,apellido2 FROM Alumno";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                Alumno alumno = new Alumno(dni, nombre, apellido1, apellido2);
                alumnos.add(alumno);
            }
            rs.close();
            connection.closeConnection();
        }catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return alumnos;
    }

    /**
     * Metodo que busca un alumno y mira a ver si se puede eliminar
     *
     * @param alumno alumno a buscar
     * @return true/false
     */
    public static boolean esEliminable(Alumno alumno) {
        DBConnect connection;
        try {
            connection = new DBConnect();
            // Prestamos
            String consulta = "SELECT count(*) as cont FROM Prestamos WHERE dni_alumno = ?";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getDni());
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
            consulta = "SELECT count(*) as cont FROM Historial_prestamos WHERE dni_alumno = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getDni());
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
     * Metodo que modifica los datos de un alumno en la BD
     *
     * @param alumno		Instancia del alumno con datos a actualizar
     * @return			true/false
     */
    public static boolean modificar(Alumno alumno) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "UPDATE Alumno SET nombre = ?,apellido1 = ?,apellido2 = ? WHERE dni = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido1());
            ps.setString(3, alumno.getApellido2());
            ps.setString(4, alumno.getDni());
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Actualizado alumno");
            ps.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Metodo que CREA un nuevo alumno en la BD
     *
     * @param alumno		Instancia del modelo alumno con datos nuevos
     * @return			true/false
     */
    public  static boolean insertar(Alumno alumno) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "INSERT INTO Alumno (dni,nombre,apellido1,apellido2) VALUES (?,?,?,?) ";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getDni());
            ps.setString(2, alumno.getNombre());
            ps.setString(3, alumno.getApellido1());
            ps.setString(4, alumno.getApellido2());
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Nueva entrada en alumno");
            connection.closeConnection();
            return (filasAfectadas > 0);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un alumno en función del modelo Alumno que le hayamos pasado
     *
     * @param alumno Alumno a eliminar
     * @return a boolean
     */
    public static boolean eliminar(Alumno alumno) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "DELETE FROM Alumno WHERE dni = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getDni());
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
