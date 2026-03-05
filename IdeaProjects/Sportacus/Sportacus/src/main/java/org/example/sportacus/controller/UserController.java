package org.example.sportacus.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.sportacus.SportacusApp;
import org.example.sportacus.model.Deporte;
import org.example.sportacus.model.Pista;
import org.example.sportacus.model.Reserva;
import org.example.sportacus.model.Usuario;
import org.example.sportacus.service.DeporteService;
import org.example.sportacus.service.PistaService;
import org.example.sportacus.service.ReservaService;
import org.example.sportacus.util.SessionManager;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    // Top
    @FXML private Label lblBienvenida;
    @FXML private Label lblHeroTitle;

    // Nav buttons
    @FXML private javafx.scene.control.Button navInicio;
    @FXML private javafx.scene.control.Button navReservar;
    @FXML private javafx.scene.control.Button navMisReservas;
    @FXML private javafx.scene.control.Button navPerfil;

    // Paneles
    @FXML private VBox panelInicio;
    @FXML private VBox panelReservar;
    @FXML private VBox panelMisReservas;

    // Tabla proximas (inicio)
    @FXML private TableView<Reserva> tablaProximas;
    @FXML private TableColumn<Reserva, String> colProxPista;
    @FXML private TableColumn<Reserva, String> colProxDeporte;
    @FXML private TableColumn<Reserva, String> colProxInicio;
    @FXML private TableColumn<Reserva, String> colProxFin;
    @FXML private TableColumn<Reserva, String> colProxPrecio;

    // Reservar
    @FXML private ComboBox<Deporte> cboDeporte;
    @FXML private DatePicker dateFecha;
    @FXML private ComboBox<String> cboHoraInicio;
    @FXML private ComboBox<String> cboDuracion;
    @FXML private ListView<Pista> listPistasDisponibles;
    @FXML private Label lblPrecioEstimado;
    @FXML private javafx.scene.control.Button btnConfirmarReserva;

    // Mis reservas
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colPista;
    @FXML private TableColumn<Reserva, String> colDeporte;
    @FXML private TableColumn<Reserva, String> colInicio;
    @FXML private TableColumn<Reserva, String> colFin;
    @FXML private TableColumn<Reserva, String> colEstado;
    @FXML private TableColumn<Reserva, String> colPrecio;
    @FXML private javafx.scene.layout.HBox hboxFiltrosDeporteUser;
    private final java.util.Set<String> filtrosDeporteUser = new java.util.HashSet<>();
    private java.util.List<Reserva> todasReservasUser = new java.util.ArrayList<>();

    // Perfil
    @FXML private VBox panelPerfil;
    @FXML private TextField txtPerfilNombre;
    @FXML private TextField txtPerfilApellidos;
    @FXML private TextField txtPerfilEmail;
    @FXML private TextField txtPerfilTelefono;
    @FXML private javafx.scene.control.PasswordField txtPerfilPassword;
    @FXML private Label lblPerfilTotalReservas;
    @FXML private Label lblPerfilProximas;
    @FXML private Label lblPerfilGastado;
    @FXML private TableView<Reserva> tablaPerfilReservas;
    @FXML private TableColumn<Reserva, String> colPerfilPista;
    @FXML private TableColumn<Reserva, String> colPerfilDeporte;
    @FXML private TableColumn<Reserva, String> colPerfilInicio;
    @FXML private TableColumn<Reserva, String> colPerfilEstado;
    @FXML private TableColumn<Reserva, String> colPerfilPrecio;

    private final ReservaService reservaService = new ReservaService();
    private final DeporteService deporteService = new DeporteService();

    // Paginación mis reservas
    private static final int PAGE_SIZE = 20;
    private int paginaReservas = 0;
    @FXML private javafx.scene.control.Button btnResPrev;
    @FXML private javafx.scene.control.Button btnResNext;
    @FXML private Label lblResPagina;
    private final PistaService pistaService = new PistaService();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario u = SessionManager.getUsuarioActual();
        if (u != null) {
            lblBienvenida.setText("Hola, " + u.getNombre());
            lblHeroTitle.setText("¡Hola, " + u.getNombre() + "!");
        }

        setNavActivo(navInicio);
        colProxPista.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPistaNombre()));
        colProxDeporte.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeporteNombre()));
        colProxInicio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInicio() != null ? c.getValue().getInicio().format(FMT) : ""));
        colProxFin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFin() != null ? c.getValue().getFin().format(FMT) : ""));
        colProxPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioTotal() + " EUR"));

        // Inicializar tabla mis reservas
        colPista.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPistaNombre()));
        colDeporte.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeporteNombre()));
        colInicio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInicio() != null ? c.getValue().getInicio().format(FMT) : ""));
        colFin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFin() != null ? c.getValue().getFin().format(FMT) : ""));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(traducirEstado(c.getValue().getEstado())));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioTotal() + " EUR"));

        // Inicializar combo deportes
        cboDeporte.setItems(FXCollections.observableArrayList(deporteService.getAll()));

        // Horas
        for (int h = 8; h <= 21; h++) cboHoraInicio.getItems().add(String.format("%02d:00", h));

        // Duraciones
        cboDuracion.getItems().addAll("1 hora", "1.5 horas", "2 horas", "2.5 horas", "3 horas");
        cboDuracion.getSelectionModel().selectFirst();

        dateFecha.setValue(LocalDate.now());

        // Bloquear fechas pasadas en el DatePicker
        dateFecha.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // Habilitar botón Confirmar solo cuando hay pista seleccionada
        listPistasDisponibles.getSelectionModel().selectedItemProperty().addListener((obs, old, pista) -> {
            btnConfirmarReserva.setDisable(pista == null);
            if (pista != null && cboDuracion.getValue() != null) {
                long min = parseDuracion(cboDuracion.getValue());
                double precio = pista.getPrecioHora().doubleValue() * (min / 60.0);
                lblPrecioEstimado.setText(String.format("Precio estimado: %.2f EUR", precio));
            }
        });

        // Celda personalizada con detalles de la pista
        listPistasDisponibles.setCellFactory(lv -> new javafx.scene.control.ListCell<Pista>() {
            @Override
            protected void updateItem(Pista pista, boolean empty) {
                super.updateItem(pista, empty);
                if (empty || pista == null) {
                    setGraphic(null);
                    return;
                }
                javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(3);
                box.setPadding(new javafx.geometry.Insets(8, 10, 8, 10));

                javafx.scene.control.Label nombre = new javafx.scene.control.Label(
                        "🏟  " + pista.getNombre());
                nombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

                // Detalles: superficie, precio, cubierta, iluminación, capacidad
                StringBuilder detalles = new StringBuilder();
                if (pista.getTipoSuperficie() != null && !pista.getTipoSuperficie().isBlank())
                    detalles.append(pista.getTipoSuperficie());
                detalles.append("  •  ").append(String.format("%.2f €/h", pista.getPrecioHora().doubleValue()));
                if (pista.isCubierta())    detalles.append("  •  🏠 Cubierta");
                if (pista.isIluminacion()) detalles.append("  •  💡 Iluminación");
                if (pista.getCapacidad() != null && pista.getCapacidad() > 0)
                    detalles.append("  •  👥 ").append(pista.getCapacidad()).append(" personas");

                javafx.scene.control.Label info = new javafx.scene.control.Label(detalles.toString());
                info.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.60);");

                box.getChildren().addAll(nombre, info);
                setGraphic(box);
                setText(null);
            }
        });

        cargarProximas();
    }

    private void cargarProximas() {
        Usuario u = SessionManager.getUsuarioActual();
        if (u == null) return;
        try {
            List<Reserva> proximas = reservaService.getProximas(u.getId());
            tablaProximas.setItems(FXCollections.observableArrayList(proximas));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setNavActivo(javafx.scene.control.Button activo) {
        for (javafx.scene.control.Button b : new javafx.scene.control.Button[]{navInicio, navReservar, navMisReservas, navPerfil}) {
            if (b == null) continue;
            b.getStyleClass().remove("nav-btn-active");
        }
        if (activo != null && !activo.getStyleClass().contains("nav-btn-active"))
            activo.getStyleClass().add("nav-btn-active");
    }

    // Navegación entre paneles
    private void mostrarPanel(VBox panel) {
        panelInicio.setVisible(false); panelInicio.setManaged(false);
        panelReservar.setVisible(false); panelReservar.setManaged(false);
        panelMisReservas.setVisible(false); panelMisReservas.setManaged(false);
        panelPerfil.setVisible(false); panelPerfil.setManaged(false);
        panel.setVisible(true); panel.setManaged(true);
    }

    @FXML private void mostrarInicio() {
        mostrarPanel(panelInicio);
        setNavActivo(navInicio);
        cargarProximas();
    }

    @FXML private void mostrarReservar() {
        irAReservarConDeporte(null);
    }

    @FXML private void reservarBaloncesto() { irAReservarConDeporte("Baloncesto"); }
    @FXML private void reservarTenis()      { irAReservarConDeporte("Tenis"); }
    @FXML private void reservarPadel()      { irAReservarConDeporte("Pádel"); }
    @FXML private void reservarFutbol()     { irAReservarConDeporte("Fútbol"); }

    private void irAReservarConDeporte(String nombreDeporte) {
        mostrarPanel(panelReservar);
        setNavActivo(navReservar);
        listPistasDisponibles.getItems().clear();
        lblPrecioEstimado.setText("Selecciona deporte, fecha y hora para buscar.");
        btnConfirmarReserva.setDisable(true);

        if (nombreDeporte != null) {
            cboDeporte.getItems().stream()
                    .filter(d -> d.getNombre().equalsIgnoreCase(nombreDeporte))
                    .findFirst()
                    .ifPresent(d -> {
                        cboDeporte.setValue(d);
                        if (dateFecha.getValue() != null && cboHoraInicio.getValue() != null) {
                            buscarPistas();
                        }
                    });
        }
    }

    @FXML private void mostrarMisReservas() {
        mostrarPanel(panelMisReservas);
        setNavActivo(navMisReservas);
        paginaReservas = 0;
        Usuario u = SessionManager.getUsuarioActual();
        if (u == null) return;
        try {
            todasReservasUser = reservaService.getMisReservas(u.getId());
            hboxFiltrosDeporteUser.getChildren().subList(1, hboxFiltrosDeporteUser.getChildren().size()).clear();
            filtrosDeporteUser.clear();
            for (Deporte d : deporteService.getAll()) {
                CheckBox cb = new CheckBox(d.getNombre());
                cb.setSelected(true);
                cb.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 13px;");
                filtrosDeporteUser.add(d.getNombre());
                cb.selectedProperty().addListener((obs, o, selected) -> {
                    if (selected) filtrosDeporteUser.add(d.getNombre());
                    else filtrosDeporteUser.remove(d.getNombre());
                    paginaReservas = 0;
                    aplicarFiltroReservasUser();
                });
                hboxFiltrosDeporteUser.getChildren().add(cb);
            }
            aplicarFiltroReservasUser();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void aplicarFiltroReservasUser() {
        List<Reserva> filtradas = todasReservasUser.stream()
                .filter(r -> filtrosDeporteUser.contains(r.getDeporteNombre()))
                .collect(java.util.stream.Collectors.toList());
        int total = filtradas.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        paginaReservas = Math.min(paginaReservas, totalPaginas - 1);
        int desde = paginaReservas * PAGE_SIZE;
        tablaReservas.setItems(FXCollections.observableArrayList(
                filtradas.subList(desde, Math.min(desde + PAGE_SIZE, total))));
        lblResPagina.setText("Página " + (paginaReservas + 1) + " de " + totalPaginas);
        btnResPrev.setDisable(paginaReservas == 0);
        btnResNext.setDisable(paginaReservas >= totalPaginas - 1);
    }

    @FXML private void reservasPrevPage() { paginaReservas--; aplicarFiltroReservasUser(); }
    @FXML private void reservasNextPage() { paginaReservas++; aplicarFiltroReservasUser(); }

    @FXML private void mostrarPerfil() {
        mostrarPanel(panelPerfil);
        setNavActivo(navPerfil);
        Usuario u = SessionManager.getUsuarioActual();
        if (u == null) return;

        // Rellenar campos
        txtPerfilNombre.setText(u.getNombre());
        txtPerfilApellidos.setText(u.getApellidos());
        txtPerfilEmail.setText(u.getEmail());
        txtPerfilTelefono.setText(u.getTelefono() != null ? u.getTelefono() : "");
        txtPerfilPassword.clear();

        // Configurar tabla perfil (sólo si aún no tiene columnas configuradas)
        if (colPerfilPista.getCellValueFactory() == null) {
            colPerfilPista.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPistaNombre()));
            colPerfilDeporte.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeporteNombre()));
            colPerfilInicio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInicio() != null ? c.getValue().getInicio().format(FMT) : ""));
            colPerfilEstado.setCellValueFactory(c -> new SimpleStringProperty(traducirEstado(c.getValue().getEstado())));
            colPerfilPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioTotal() + " EUR"));
        }

        try {
            List<Reserva> todas = reservaService.getMisReservas(u.getId());
            List<Reserva> proximas = reservaService.getProximas(u.getId());
            java.math.BigDecimal gastado = todas.stream()
                    .filter(r -> !"CANCELADA".equals(r.getEstado()))
                    .map(Reserva::getPrecioTotal)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            lblPerfilTotalReservas.setText(String.valueOf(todas.size()));
            lblPerfilProximas.setText(String.valueOf(proximas.size()));
            lblPerfilGastado.setText(String.format("%.2f €", gastado.doubleValue()));
            tablaPerfilReservas.setItems(FXCollections.observableArrayList(todas));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void guardarPerfil() {
        Usuario u = SessionManager.getUsuarioActual();
        if (u == null) return;

        String nombre = txtPerfilNombre.getText().trim();
        String apellidos = txtPerfilApellidos.getText().trim();
        String telefono = txtPerfilTelefono.getText().trim();
        String password = txtPerfilPassword.getText();

        if (nombre.isBlank() || apellidos.isBlank()) {
            mostrarAlerta("Nombre y apellidos son obligatorios.");
            return;
        }
        try {
            u.setNombre(nombre);
            u.setApellidos(apellidos);
            u.setTelefono(telefono.isBlank() ? null : telefono);
            if (!password.isBlank()) {
                if (password.length() < 4) { mostrarAlerta("La contraseña debe tener al menos 4 caracteres."); return; }
                u.setPasswordHash(password);
            }
            new org.example.sportacus.service.UsuarioService().actualizar(u);
            SessionManager.setUsuarioActual(u);
            lblBienvenida.setText("Hola, " + u.getNombre());
            mostrarInfo("Perfil actualizado correctamente.");
            txtPerfilPassword.clear();
        } catch (Exception e) { mostrarAlerta("Error al guardar: " + e.getMessage()); }
    }

    @FXML
    private void buscarPistas() {
        Deporte deporte = cboDeporte.getValue();
        LocalDate fecha = dateFecha.getValue();
        String horaStr = cboHoraInicio.getValue();
        String durStr = cboDuracion.getValue();

        if (deporte == null || fecha == null || horaStr == null || durStr == null) {
            mostrarAlerta("Rellena todos los campos antes de buscar.");
            return;
        }
        LocalDateTime inicio = LocalDateTime.of(fecha, LocalTime.parse(horaStr));
        LocalDateTime fin = inicio.plusMinutes(parseDuracion(durStr));
        try {
            List<Pista> disponibles = pistaService.getDisponibles(deporte.getId(), inicio, fin);
            listPistasDisponibles.setItems(FXCollections.observableArrayList(disponibles));
            btnConfirmarReserva.setDisable(true);
            lblPrecioEstimado.setText(disponibles.isEmpty() ? "No hay pistas disponibles para ese horario." : "Selecciona una pista para ver el precio.");
        } catch (Exception e) {
            mostrarAlerta("Error al buscar: " + e.getMessage());
        }
    }

    @FXML
    private void confirmarReserva() {
        Pista pista = listPistasDisponibles.getSelectionModel().getSelectedItem();
        if (pista == null) { mostrarAlerta("Selecciona una pista disponible."); return; }

        LocalDateTime inicio = LocalDateTime.of(dateFecha.getValue(), LocalTime.parse(cboHoraInicio.getValue()));
        LocalDateTime fin = inicio.plusMinutes(parseDuracion(cboDuracion.getValue()));

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        estilizarAlert(confirm);
        confirm.setTitle("Confirmar reserva");
        confirm.setContentText("Reservar " + pista.getNombre() + "\n" +
                inicio.format(FMT) + " - " + fin.format(DateTimeFormatter.ofPattern("HH:mm")) +
                "\n" + lblPrecioEstimado.getText());
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    Reserva r = reservaService.crear(SessionManager.getUsuarioActual(), pista, inicio, fin);
                    mostrarInfo("Reserva confirmada! Total: " + r.getPrecioTotal() + " EUR");
                    mostrarMisReservas();
                } catch (Exception e) {
                    mostrarAlerta("Error: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void cancelarReserva() {
        Reserva sel = tablaReservas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona una reserva."); return; }
        if ("CANCELADA".equals(sel.getEstado())) { mostrarAlerta("Esta reserva ya está cancelada."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        estilizarAlert(confirm);
        confirm.setTitle("Cancelar reserva");
        confirm.setContentText("¿Cancelar la reserva en " + sel.getPistaNombre() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    reservaService.cancelar(sel.getId(), "Cancelada por el usuario");
                    mostrarMisReservas();
                } catch (Exception e) { mostrarAlerta("Error: " + e.getMessage()); }
            }
        });
    }

    @FXML
    private void eliminarReserva() {
        Reserva sel = tablaReservas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona una reserva."); return; }
        if (!"CANCELADA".equals(sel.getEstado())) { mostrarAlerta("Solo se pueden eliminar reservas canceladas."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        estilizarAlert(confirm);
        confirm.setTitle("Eliminar reserva");
        confirm.setContentText("¿Eliminar definitivamente esta reserva? Esta acción no se puede deshacer.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    reservaService.eliminar(sel.getId());
                    mostrarMisReservas();
                } catch (Exception e) { mostrarAlerta("Error: " + e.getMessage()); }
            }
        });
    }

    @FXML
    private void cerrarSesion() {
        SessionManager.cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(SportacusApp.class.getResource("fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private long parseDuracion(String d) {
        return switch (d) {
            case "1 hora" -> 60; case "1.5 horas" -> 90; case "2 horas" -> 120;
            case "2.5 horas" -> 150; case "3 horas" -> 180; default -> 60;
        };
    }

    private String traducirEstado(String estado) {
        if (estado == null) return "";
        return switch (estado) {
            case "CONFIRMADA" -> "Reservado";
            case "CANCELADA"  -> "Cancelada";
            case "PENDIENTE"  -> "Pendiente";
            case "COMPLETADA" -> "Completada";
            default -> estado;
        };
    }

    private void estilizarAlert(Alert a) {
        a.getDialogPane().getStylesheets().add(
            getClass().getResource("/org/example/sportacus/css/user.css").toExternalForm());
        a.getDialogPane().getStyleClass().add("dialog-pane");
        a.setGraphic(null);
        // Quitar icono del header
        a.getDialogPane().setGraphic(null);
    }
    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null); a.setContentText(msg);
        estilizarAlert(a); a.showAndWait();
    }
    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg);
        estilizarAlert(a); a.showAndWait();
    }
}
