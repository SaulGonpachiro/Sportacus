package org.example.sportacus.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.sportacus.SportacusApp;
import org.example.sportacus.model.Usuario;
import org.example.sportacus.service.UsuarioService;
import org.example.sportacus.util.SessionManager;

import java.util.Optional;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String pass = passwordField.getText();

        try {
            Optional<Usuario> resultado = usuarioService.login(email, pass);

            if (resultado.isPresent()) {
                Usuario u = resultado.get();
                SessionManager.setUsuarioActual(u);
                usuarioService.updateUltimoLogin(u.getId());
                cargarDashboard(u);
            } else {
                mostrarError("Credenciales incorrectas o usuario inactivo.");
            }
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SportacusApp.class.getResource("fxml/registro.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            mostrarError("No se pudo abrir el registro.");
            e.printStackTrace();
        }
    }

    private void cargarDashboard(Usuario usuario) {
        try {
            String fxml = "ADMIN".equals(usuario.getRol()) ? "fxml/admin-dashboard.fxml" : "fxml/user-dashboard.fxml";
            FXMLLoader loader = new FXMLLoader(SportacusApp.class.getResource(fxml));
            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            mostrarError("Error al cargar el dashboard.");
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
