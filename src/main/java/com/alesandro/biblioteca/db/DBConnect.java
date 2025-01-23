package com.alesandro.biblioteca.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Clase de conexión a la base de datos
 */
public class DBConnect {
    /**
     * Logger a usar
     */
    private static final Logger logger = LoggerFactory.getLogger(DBConnect.class.getName());

    private final Connection connection;

    /**
     * Es el constructor que se llama al crear un objeto de esta clase, lanzado la conexión
     *
     * @throws SQLException Hay que controlar errores de SQL
     */
    public DBConnect() throws SQLException {
        Properties configuracion = getConfiguration();
        if (configuracion != null) {
            // los parametros de la conexion
            Properties connConfig = new Properties();
            connConfig.setProperty("user", configuracion.getProperty("user"));
            connConfig.setProperty("password", configuracion.getProperty("password"));
            //la conexion en sí
            connection = DriverManager.getConnection("jdbc:mariadb://" + configuracion.getProperty("address") + ":" + configuracion.getProperty("port") + "/" + configuracion.getProperty("database") + "?serverTimezone=Europe/Madrid", connConfig);
            connection.setAutoCommit(true);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            //debug
            logger.debug("--- Datos de conexión ------------------------------------------");
            logger.debug("Base de datos: {}", databaseMetaData.getDatabaseProductName());
            logger.debug("Versión: {}", databaseMetaData.getDatabaseProductVersion());
            logger.debug("Driver: {}", databaseMetaData.getDriverName());
            logger.debug("Versión driver: {}", databaseMetaData.getDriverVersion());
            logger.debug("----------------------------------------------------------------");
            connection.setAutoCommit(true);
        } else {
            this.connection = null;
        }
    }

    /**
     * Función que testea la conexión a una base de datos
     *
     * @param address
     * @param port
     * @param user
     * @param password
     * @param database
     * @return 1/error
     */
    public static String testConnection(String address, int port, String user, String password, String database) {
        try {
            Properties authentication = new Properties();
            authentication.setProperty("user", user);
            authentication.setProperty("password", password);
            Connection connection1 = DriverManager.getConnection("jdbc:mariadb://" + address + ":" + port + "/" + database + "?serverTimezone=Europe/Madrid", authentication);
            if (connection1.isValid(10)) {
                connection1.close();
                return "1";
            }
            connection1.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    /**
     * Función que obtiene la configuración para la conexión a la base de datos
     *
     * @return objeto Properties con los datos de conexión a la base de datos
     */
    public static Properties getConfiguration() {
        HashMap<String,String> map = new HashMap<String,String>();
        File f = new File("configuration.properties");
        Properties properties;
        try {
            FileInputStream configFileReader=new FileInputStream(f);
            properties = new Properties();
            try {
                properties.load(configFileReader);
                configFileReader.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            //throw new RuntimeException("configuration.properties not found at config file path " + f.getPath());
            return null;
        }
        return properties;
    }

    /**
     * Función que crea el fichero configuration.properties si este no existe con valores predeterminados
     *
     * @param db propiedades de la base de datos
     */
    public static void createConfiguration(Properties db) {
        File f = new File("configuration.properties");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            db.store(fos, "");
            fos.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Esta clase devuelve la conexión creada
     *
     * @return una conexión a la BBDD
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Metodo de cerrar la conexion con la base de datos
     *
     * @return La conexión cerrada.
     * @throws SQLException Se lanza en caso de errores de SQL al cerrar la conexión.
     */
    public Connection closeConnection() throws SQLException{
        connection.close();
        return connection;
    }

}
