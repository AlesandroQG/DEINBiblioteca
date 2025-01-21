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
        /*System.out.println();
        System.out.println("--- Datos de conexión ------------------------------------------");
        System.out.printf("Base de datos: %s%n", databaseMetaData.getDatabaseProductName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDatabaseProductVersion());
        System.out.printf("Driver: %s%n", databaseMetaData.getDriverName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDriverVersion());
        System.out.println("----------------------------------------------------------------");
        System.out.println();*/
            connection.setAutoCommit(true);
        } else {
            this.connection = null;
        }
    }

    /**
     * Función que obtiene la configuración para la conexión a la base de datos
     *
     * @return objeto Properties con los datos de conexión a la base de datos
     */
    public static Properties getConfiguration() {
        HashMap<String,String> map = new HashMap<String,String>();
        File f = new File("configuration.properties");
        if (!f.exists()) {
            createConfiguration();
        }
        Properties properties;
        try {
            FileInputStream configFileReader=new FileInputStream(f);
            properties = new Properties();
            try {
                properties.load(configFileReader);
                configFileReader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            //throw new RuntimeException("configuration.properties not found at config file path " + f.getPath());
            return null;
        }
        return properties;
    }

    /**
     * Función que crea el fichero configuration.properties si este no existe con valores predeterminados
     */
    public static void createConfiguration() {
        File f = new File("configuration.properties");
        Properties properties = new Properties();
        properties.setProperty("address", "127.0.0.1");
        properties.setProperty("port", "33066");
        properties.setProperty("user", "admin");
        properties.setProperty("password", "mypass");
        properties.setProperty("database", "admin");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            properties.store(fos, "");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
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
