package org.example.sportacus.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.sportacus.SportacusApp;
import org.example.sportacus.service.UsuarioService;

public class RegistroController {

    @FXML private TextField nameField;
    @FXML private TextField apellidosField;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void handleRegistro() {
        try {
            usuarioService.registrar(
                    nameField.getText(),
                    apellidosField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    telefonoField.getText()
            );
            mostrarInfo("¡Registro completado! Ya puedes iniciar sesión.");
            volverLogin();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al registrar. Inténtalo de nuevo.");
            e.printStackTrace();
        }
    }

    @FXML
    private void volverLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(SportacusApp.class.getResource("fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro exitoso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
