package org.example.sportacus.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.sportacus.SportacusApp;
import org.example.sportacus.model.Deporte;
import org.example.sportacus.model.Pista;
import org.example.sportacus.model.Reserva;
import org.example.sportacus.service.DeporteService;
import org.example.sportacus.service.PistaService;
import org.example.sportacus.service.ReservaService;
import org.example.sportacus.util.SessionManager;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class ReservarController implements Initializable {

    @FXML private ComboBox<Deporte> cboDeporte;
    @FXML private DatePicker dateFecha;
    @FXML private ComboBox<String> cboHoraInicio;
    @FXML private ComboBox<String> cboDuracion;
    @FXML private ListView<Pista> listPistasDisponibles;
    @FXML private Label lblPrecioEstimado;
    @FXML private Button btnBuscar;
    @FXML private Button btnReservar;

    private final DeporteService deporteService = new DeporteService();
    private final PistaService pistaService = new PistaService();
    private final ReservaService reservaService = new ReservaService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cargar deportes
        List<Deporte> deportes = deporteService.getAll();
        cboDeporte.setItems(FXCollections.observableArrayList(deportes));

        // Horas disponibles de 8:00 a 22:00 cada hora
        for (int h = 8; h <= 21; h++) {
            cboHoraInicio.getItems().add(String.format("%02d:00", h));
        }

        // Duraciones
        cboDuracion.getItems().addAll("1 hora", "1.5 horas", "2 horas", "2.5 horas", "3 horas");
        cboDuracion.getSelectionModel().selectFirst();

        // Fecha mínima = hoy
        dateFecha.setValue(LocalDate.now());

        // Listener para actualizar precio estimado
        listPistasDisponibles.getSelectionModel().selectedItemProperty().addListener((obs, old, pista) -> {
            if (pista != null) {
                actualizarPrecioEstimado(pista);
            }
        });
    }

    @FXML
    private void buscarPistas() {
        Deporte deporte = cboDeporte.getValue();
        LocalDate fecha = dateFecha.getValue();
        String horaStr = cboHoraInicio.getValue();
        String durStr = cboDuracion.getValue();

        if (deporte == null || fecha == null || horaStr == null || durStr == null) {
            mostrarAlerta("Por favor rellena todos los campos.");
            return;
        }

        LocalDateTime inicio = LocalDateTime.of(fecha, LocalTime.parse(horaStr));
        LocalDateTime fin = inicio.plusMinutes(parseDuracion(durStr));

        try {
            List<Pista> disponibles = pistaService.getDisponibles(deporte.getId(), inicio, fin);
            listPistasDisponibles.setItems(FXCollections.observableArrayList(disponibles));

            if (disponibles.isEmpty()) {
                lblPrecioEstimado.setText("No hay pistas disponibles para ese horario.");
            } else {
                lblPrecioEstimado.setText("Selecciona una pista para ver el precio.");
            }
        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Error al buscar pistas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void confirmarReserva() {
        Pista pista = listPistasDisponibles.getSelectionModel().getSelectedItem();
        if (pista == null) {
            mostrarAlerta("Selecciona una pista disponible.");
            return;
        }

        LocalDateTime inicio = getInicio();
        LocalDateTime fin = inicio.plusMinutes(parseDuracion(cboDuracion.getValue()));

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar reserva");
        confirm.setContentText("¿Confirmar reserva en " + pista.getNombre() + "?\n" +
                inicio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                " - " + fin.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) +
                "\nPrecio: " + lblPrecioEstimado.getText());

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    Reserva r = reservaService.crear(SessionManager.getUsuarioActual(), pista, inicio, fin);
                    mostrarInfo("¡Reserva confirmada! Precio total: " + r.getPrecioTotal() + " €");
                    volverDashboard();
                } catch (IllegalStateException e) {
                    mostrarAlerta("La pista ya fue reservada. Busca de nuevo.");
                    buscarPistas();
                } catch (Exception e) {
                    mostrarAlerta("Error al crear la reserva: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void actualizarPrecioEstimado(Pista pista) {
        if (cboDuracion.getValue() == null) return;
        long minutos = parseDuracion(cboDuracion.getValue());
        double horas = minutos / 60.0;
        double precio = pista.getPrecioHora().doubleValue() * horas;
        lblPrecioEstimado.setText(String.format("Precio estimado: %.2f €", precio));
    }

    private LocalDateTime getInicio() {
        LocalDate fecha = dateFecha.getValue();
        String horaStr = cboHoraInicio.getValue();
        return LocalDateTime.of(fecha, LocalTime.parse(horaStr));
    }

    private long parseDuracion(String duracion) {
        return switch (duracion) {
            case "1 hora" -> 60;
            case "1.5 horas" -> 90;
            case "2 horas" -> 120;
            case "2.5 horas" -> 150;
            case "3 horas" -> 180;
            default -> 60;
        };
    }

    @FXML
    private void volverDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(SportacusApp.class.getResource("fxml/user-dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = (Stage) cboDeporte.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
