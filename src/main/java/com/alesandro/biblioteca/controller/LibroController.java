package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.dao.DaoLibro;
import com.alesandro.biblioteca.model.Libro;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Clase controladora de la ventana libro
 */
public class LibroController implements Initializable {
    /**
     * Parámetro libro
     */
    private Libro libro;

    /**
     * Parámetro portada
     */
    private Blob portada;

    @FXML // fx:id="btnFotoBorrar"
    private Button btnFotoBorrar; // Value injected by FXMLLoader

    @FXML // fx:id="cbBaja"
    private CheckBox cbBaja; // Value injected by FXMLLoader

    @FXML // fx:id="cbEstado"
    private ComboBox<String> cbEstado; // Value injected by FXMLLoader

    @FXML // fx:id="foto"
    private ImageView foto; // Value injected by FXMLLoader

    @FXML // fx:id="txtAutor"
    private TextField txtAutor; // Value injected by FXMLLoader

    @FXML // fx:id="lblCodigo"
    private Label lblCodigo; // Value injected by FXMLLoader

    @FXML // fx:id="txtEditorial"
    private TextField txtEditorial; // Value injected by FXMLLoader

    @FXML // fx:id="txtTitulo"
    private TextField txtTitulo; // Value injected by FXMLLoader

    /**
     * Recursos de la aplicación (strings del multiidioma)
     */
    private ResourceBundle resources;

    /**
     * Constructor con parámetros para la consulta o edición de un libro
     *
     * @param libro a consultar o editar
     */
    public LibroController(Libro libro) {
        this.libro = libro;
    }

    /**
     * Constructor vacío para la creación de un libro
     */
    public LibroController() {
        this.libro = null;
    }

    /**
     * Función que se ejecuta cuando se inicia la ventana
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        cbEstado.getItems().addAll(resources.getString("book.status.available"),resources.getString("book.status.loaned"));
        cbEstado.getSelectionModel().select(0);
        if (libro != null) {
            lblCodigo.setText(libro.getCodigo() + "");
            txtTitulo.setText(libro.getTitulo());
            txtAutor.setText(libro.getAutor());
            txtEditorial.setText(libro.getEditorial());
            if (libro.getEstado().equals("disponible")) {
                cbEstado.getSelectionModel().select(resources.getString("book.status.available"));
            } else {
                cbEstado.getSelectionModel().select(resources.getString("book.status.loaned"));
            }
            if (libro.getBaja() == 1) {
                cbBaja.setSelected(true);
            }
            if (libro.getPortada() != null) {
                portada = libro.getPortada();
                try {
                    InputStream imagen = libro.getPortada().getBinaryStream();
                    foto.setImage(new Image(imagen));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                btnFotoBorrar.setDisable(false);
            }
        } else {
            lblCodigo.setText("-");
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Borrar Fóto". Elimina la portada del libro
     *
     * @param event evento del usuario
     */
    @FXML
    void borrarFoto(ActionEvent event) {
        portada = null;
        foto.setImage(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/libro.png")));
        btnFotoBorrar.setDisable(true);
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Cancelar". Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage)btnFotoBorrar.getScene().getWindow();
        stage.close();
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Guardar". Valida y guarda los datos del libro
     *
     * @param event evento del usuario
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            mostrarAlerta(error);
        } else {
            if (libro == null) {
                // Nuevo
                Libro libro1 = new Libro();
                libro1.setTitulo(txtTitulo.getText());
                libro1.setAutor(txtAutor.getText());
                libro1.setEditorial(txtEditorial.getText());
                if (cbEstado.getSelectionModel().getSelectedItem().equals(resources.getString("book.status.available"))) {
                    libro1.setEstado("disponible");
                } else {
                    libro1.setEstado("prestado");
                }
                libro1.setBaja(cbBaja.isSelected() ? 1 : 0);
                libro1.setPortada(portada);
                if (DaoLibro.insertar(libro1) != -1) {
                    mostrarConfirmacion(resources.getString("save.book"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("save.fail"));
                }
            } else {
                // Actualizar
                Libro libro1 = new Libro();
                libro1.setCodigo(libro.getCodigo());
                libro1.setTitulo(txtTitulo.getText());
                libro1.setAutor(txtAutor.getText());
                libro1.setEditorial(txtEditorial.getText());
                if (cbEstado.getSelectionModel().getSelectedItem().equals(resources.getString("book.status.available"))) {
                    libro1.setEstado("disponible");
                } else {
                    libro1.setEstado("prestado");
                }
                libro1.setBaja(cbBaja.isSelected() ? 1 : 0);
                libro1.setPortada(portada);
                if (DaoLibro.modificar(libro1)) {
                    mostrarConfirmacion(resources.getString("update.book"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("save.fail"));
                }
            }
        }
    }

    /**
     * Función que valida el formulario y devuelve los posibles errores
     *
     * @return string con posibles errores
     */
    private String validar() {
        String error = "";
        if (txtTitulo.getText().isEmpty()) {
            error += resources.getString("validate.book.title") + "\n";
        }
        if (txtAutor.getText().isEmpty()) {
            error += resources.getString("validate.book.author") + "\n";
        }
        if (txtEditorial.getText().isEmpty()) {
            error += resources.getString("validate.book.publisher") + "\n";
        }
        return error;
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Seleccionar Imagen". Abre un menú para seleccionar la portado del libro
     *
     * @param event evento del usuario
     */
    @FXML
    void seleccionImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("book.cover.chooser"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.jpg", "*.jpeg","*.png"));
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(null);
        try {
            InputStream imagen = new FileInputStream(file);
            Blob blob = DaoLibro.convertFileToBlob(file);
            this.portada = blob;
            foto.setImage(new Image(imagen));
            btnFotoBorrar.setDisable(false);
        } catch (IOException|NullPointerException e) {
            // e.printStackTrace();
            mostrarAlerta(resources.getString("book.cover.chooser.fail"));
        }
    }

    /**
     * Función que muestra un mensaje de confirmación al usuario
     *
     * @param mensaje de confirmación a mostrar
     */
    public void mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle(resources.getString("window.confirm"));
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Función que muestra un mensaje de alerta al usuario
     *
     * @param mensaje de error a mostrar
     */
    public void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("Error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
