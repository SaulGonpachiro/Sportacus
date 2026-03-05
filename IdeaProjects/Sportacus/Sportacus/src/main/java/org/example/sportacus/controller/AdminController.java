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
import org.example.sportacus.model.*;
import org.example.sportacus.service.*;
import org.example.sportacus.util.SessionManager;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private Label lblAdminNombre;

    // Paneles
    @FXML private VBox panelInicio;

    // Nav buttons
    @FXML private javafx.scene.control.Button navInicio;
    @FXML private javafx.scene.control.Button navReservas;
    @FXML private javafx.scene.control.Button navPistas;
    @FXML private javafx.scene.control.Button navUsuarios;
    @FXML private javafx.scene.control.Button navDeportes;
    @FXML private javafx.scene.control.Button navPerfil;
    @FXML private VBox panelReservas;
    @FXML private VBox panelPistas;
    @FXML private VBox panelUsuarios;
    @FXML private VBox panelDeportes;

    // Inicio stats
    @FXML private Label lblTotalReservas;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblTotalPistas;
    @FXML private TableView<Reserva> tablaUltimasReservas;
    @FXML private TableColumn<Reserva, String> colURUsuario;
    @FXML private TableColumn<Reserva, String> colURPista;
    @FXML private TableColumn<Reserva, String> colURInicio;
    @FXML private TableColumn<Reserva, String> colUREstado;
    @FXML private TableColumn<Reserva, String> colURPrecio;

    // Reservas
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colResId;
    @FXML private TableColumn<Reserva, String> colResUsuario;
    @FXML private TableColumn<Reserva, String> colResPista;
    @FXML private TableColumn<Reserva, String> colResInicio;
    @FXML private TableColumn<Reserva, String> colResFin;
    @FXML private TableColumn<Reserva, String> colResEstado;
    @FXML private javafx.scene.layout.HBox hboxFiltrosDeporteAdmin;
    private final java.util.Set<String> filtrosDeporteAdmin = new java.util.HashSet<>();
    private java.util.List<Reserva> todasReservasAdmin = new java.util.ArrayList<>();
    @FXML private TableColumn<Reserva, String> colResPrecio;

    // Pistas - campos del formulario
    @FXML private TableView<Pista> tablaPistas;
    @FXML private TableColumn<Pista, String> colPistaId;
    @FXML private TableColumn<Pista, String> colPistaNombre;
    @FXML private TableColumn<Pista, String> colPistaDeporte;
    @FXML private TableColumn<Pista, String> colPistaSuperficie;
    @FXML private TableColumn<Pista, String> colPistaPrecio;
    @FXML private TableColumn<Pista, String> colPistaActiva;
    @FXML private TextField txtPistaNombre;
    @FXML private ComboBox<Deporte> cboPistaDeporte;
    @FXML private TextField txtPistaSuperficie;
    @FXML private TextField txtPistaPrecio;
    @FXML private CheckBox chkPistaCubierta;
    @FXML private CheckBox chkPistaIluminacion;
    @FXML private javafx.scene.control.Label lblFormPistaTitulo;
    @FXML private javafx.scene.control.Button btnGuardarPista;
    @FXML private javafx.scene.control.Button btnCancelarEdicion;

    private Pista pistaEnEdicion = null;

    // Usuarios
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsuId;
    @FXML private TableColumn<Usuario, String> colUsuNombre;
    @FXML private TableColumn<Usuario, String> colUsuEmail;
    @FXML private TableColumn<Usuario, String> colUsuRol;
    @FXML private TableColumn<Usuario, String> colUsuActivo;

    // Deportes
    @FXML private TableView<Deporte> tablaDeportes;
    @FXML private TableColumn<Deporte, String> colDepId;
    @FXML private TableColumn<Deporte, String> colDepNombre;
    @FXML private TextField txtDeporteNombre;

    // Perfil
    @FXML private VBox panelPerfil;
    @FXML private TextField txtPerfilNombre;
    @FXML private TextField txtPerfilApellidos;
    @FXML private TextField txtPerfilEmail;
    @FXML private TextField txtPerfilTelefono;
    @FXML private javafx.scene.control.PasswordField txtPerfilPassword;
    @FXML private Label lblPerfilTotalUsuarios;
    @FXML private Label lblPerfilTotalReservas;
    @FXML private Label lblPerfilTotalPistas;
    @FXML private Label lblPerfilRol;
    @FXML private Label lblPerfilFechaRegistro;
    @FXML private Label lblPerfilUltimoLogin;

    private final ReservaService reservaService = new ReservaService();
    private final UsuarioService usuarioService = new UsuarioService();

    // Paginación reservas
    private static final int PAGE_SIZE = 20;
    private int paginaReservas = 0;
    private int paginaUsuarios = 0;
    @FXML private javafx.scene.control.Button btnResPrev;
    @FXML private javafx.scene.control.Button btnResNext;
    @FXML private Label lblResPagina;
    @FXML private javafx.scene.control.Button btnUsuPrev;
    @FXML private javafx.scene.control.Button btnUsuNext;
    @FXML private Label lblUsuPagina;
    private final PistaService pistaService = new PistaService();
    private final DeporteService deporteService = new DeporteService();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario u = SessionManager.getUsuarioActual();
        if (u != null) lblAdminNombre.setText(u.getNombreCompleto());

        setNavActivo(navInicio);

        configurarTablaUltimas();
        configurarTablaReservas();
        configurarTablaPistas();
        configurarTablaUsuarios();
        configurarTablaDeportes();

        cboPistaDeporte.setItems(FXCollections.observableArrayList(deporteService.getAll()));

        cargarDashboard();
    }

    private void setNavActivo(javafx.scene.control.Button activo) {
        for (javafx.scene.control.Button b : new javafx.scene.control.Button[]{navInicio, navReservas, navPistas, navUsuarios, navDeportes, navPerfil}) {
            if (b == null) continue;
            b.getStyleClass().remove("nav-btn-active");
        }
        if (activo != null && !activo.getStyleClass().contains("nav-btn-active"))
            activo.getStyleClass().add("nav-btn-active");
    }

    private void mostrarPanel(VBox panel) {
        panelInicio.setVisible(false);   panelInicio.setManaged(false);
        panelReservas.setVisible(false); panelReservas.setManaged(false);
        panelPistas.setVisible(false);   panelPistas.setManaged(false);
        panelUsuarios.setVisible(false); panelUsuarios.setManaged(false);
        panelDeportes.setVisible(false); panelDeportes.setManaged(false);
        panelPerfil.setVisible(false);   panelPerfil.setManaged(false);
        panel.setVisible(true); panel.setManaged(true);
    }

    @FXML private void mostrarInicio()    { mostrarPanel(panelInicio);    setNavActivo(navInicio);    cargarDashboard(); }
    @FXML private void mostrarReservas()  { mostrarPanel(panelReservas);  setNavActivo(navReservas);  cargarTablaReservas(); }
    @FXML private void mostrarPistas()    { mostrarPanel(panelPistas);    setNavActivo(navPistas);    cargarTablaPistas(); }
    @FXML private void mostrarUsuarios()  { mostrarPanel(panelUsuarios);  setNavActivo(navUsuarios);  cargarTablaUsuarios(); }
    @FXML private void mostrarDeportes()  { mostrarPanel(panelDeportes);  setNavActivo(navDeportes);  cargarTablaDeportes(); }

    @FXML private void mostrarPerfil() {
        mostrarPanel(panelPerfil);
        setNavActivo(navPerfil);
        Usuario u = SessionManager.getUsuarioActual();
        if (u == null) return;

        txtPerfilNombre.setText(u.getNombre());
        txtPerfilApellidos.setText(u.getApellidos());
        txtPerfilEmail.setText(u.getEmail());
        txtPerfilTelefono.setText(u.getTelefono() != null ? u.getTelefono() : "");
        txtPerfilPassword.clear();

        lblPerfilRol.setText("Rol: " + u.getRol());
        lblPerfilFechaRegistro.setText("Registrado: " + (u.getFechaRegistro() != null ? u.getFechaRegistro().format(FMT) : "-"));
        lblPerfilUltimoLogin.setText("Último acceso: " + (u.getUltimoLogin() != null ? u.getUltimoLogin().format(FMT) : "-"));

        try {
            lblPerfilTotalUsuarios.setText(String.valueOf(usuarioService.count()));
            lblPerfilTotalReservas.setText(String.valueOf(reservaService.count()));
            lblPerfilTotalPistas.setText(String.valueOf(pistaService.count(null)));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void guardarPerfil() {
        Usuario u = SessionManager.getUsuarioActual();
        if (u == null) return;
        String nombre = txtPerfilNombre.getText().trim();
        String apellidos = txtPerfilApellidos.getText().trim();
        String telefono = txtPerfilTelefono.getText().trim();
        String password = txtPerfilPassword.getText();
        if (nombre.isBlank() || apellidos.isBlank()) { mostrarAlerta("Nombre y apellidos son obligatorios."); return; }
        try {
            u.setNombre(nombre);
            u.setApellidos(apellidos);
            u.setTelefono(telefono.isBlank() ? null : telefono);
            if (!password.isBlank()) {
                if (password.length() < 4) { mostrarAlerta("La contraseña debe tener al menos 4 caracteres."); return; }
                u.setPasswordHash(password);
            }
            usuarioService.actualizar(u);
            SessionManager.setUsuarioActual(u);
            lblAdminNombre.setText(u.getNombreCompleto());
            mostrarInfo("Perfil actualizado correctamente.");
            txtPerfilPassword.clear();
        } catch (Exception e) { mostrarAlerta("Error al guardar: " + e.getMessage()); }
    }

    // ===== INICIO =====
    private void cargarDashboard() {
        try {
            lblTotalReservas.setText(String.valueOf(reservaService.count()));
            lblTotalUsuarios.setText(String.valueOf(usuarioService.count()));
            lblTotalPistas.setText(String.valueOf(pistaService.count(null)));
            List<Reserva> ultimas = reservaService.getPaginated(1, 10);
            tablaUltimasReservas.setItems(FXCollections.observableArrayList(ultimas));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void configurarTablaUltimas() {
        colURUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuarioNombre()));
        colURPista.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPistaNombre()));
        colURInicio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInicio() != null ? c.getValue().getInicio().format(FMT) : ""));
        colUREstado.setCellValueFactory(c -> new SimpleStringProperty(traducirEstado(c.getValue().getEstado())));
        colURPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioTotal() + " EUR"));
    }

    // ===== RESERVAS =====
    private void configurarTablaReservas() {
        colResId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colResUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuarioNombre()));
        colResPista.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPistaNombre()));
        colResInicio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInicio() != null ? c.getValue().getInicio().format(FMT) : ""));
        colResFin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFin() != null ? c.getValue().getFin().format(FMT) : ""));
        colResEstado.setCellValueFactory(c -> new SimpleStringProperty(traducirEstado(c.getValue().getEstado())));
        colResPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioTotal() + " EUR"));
    }

    private void cargarTablaReservas() {
        paginaReservas = 0;
        try {
            todasReservasAdmin = reservaService.getAll();
            hboxFiltrosDeporteAdmin.getChildren().subList(1, hboxFiltrosDeporteAdmin.getChildren().size()).clear();
            filtrosDeporteAdmin.clear();
            for (Deporte d : deporteService.getAll()) {
                CheckBox cb = new CheckBox(d.getNombre());
                cb.setSelected(true);
                cb.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 13px;");
                filtrosDeporteAdmin.add(d.getNombre());
                cb.selectedProperty().addListener((obs, o, selected) -> {
                    if (selected) filtrosDeporteAdmin.add(d.getNombre());
                    else filtrosDeporteAdmin.remove(d.getNombre());
                    paginaReservas = 0;
                    aplicarFiltroReservasAdmin();
                });
                hboxFiltrosDeporteAdmin.getChildren().add(cb);
            }
            aplicarFiltroReservasAdmin();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void aplicarFiltroReservasAdmin() {
        List<Reserva> filtradas = todasReservasAdmin.stream()
                .filter(r -> filtrosDeporteAdmin.contains(r.getDeporteNombre()))
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

    @FXML private void reservasPrevPage() { paginaReservas--; aplicarFiltroReservasAdmin(); }
    @FXML private void reservasNextPage() { paginaReservas++; aplicarFiltroReservasAdmin(); }

    @FXML
    private void cancelarReservaAdmin() {
        Reserva sel = tablaReservas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona una reserva."); return; }
        if ("CANCELADA".equals(sel.getEstado())) { mostrarAlerta("Esta reserva ya está cancelada."); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("¿Cancelar la reserva #" + sel.getId() + "?");
        c.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    reservaService.cancelar(sel.getId(), "Cancelada por administrador");
                    cargarTablaReservas();
                    mostrarInfo("Reserva cancelada.");
                } catch (Exception e) { mostrarAlerta("Error al cancelar: " + e.getMessage()); }
            }
        });
    }

    @FXML
    private void eliminarReservaAdmin() {
        Reserva sel = tablaReservas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona una reserva."); return; }
        if (!"CANCELADA".equals(sel.getEstado())) { mostrarAlerta("Solo se pueden eliminar reservas canceladas."); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("¿Eliminar definitivamente la reserva #" + sel.getId() + "? Esta acción no se puede deshacer.");
        c.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    reservaService.eliminar(sel.getId());
                    cargarTablaReservas();
                    mostrarInfo("Reserva eliminada.");
                } catch (Exception e) { mostrarAlerta("Error al eliminar: " + e.getMessage()); }
            }
        });
    }

    // ===== PISTAS =====
    private void configurarTablaPistas() {
        colPistaId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colPistaNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPistaDeporte.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeporte() != null ? c.getValue().getDeporte().getNombre() : ""));
        colPistaSuperficie.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoSuperficie() != null ? c.getValue().getTipoSuperficie() : ""));
        colPistaPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioHora() + " EUR"));
        colPistaActiva.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActiva() ? "Si" : "No"));
    }

    private void cargarTablaPistas() {
        try { tablaPistas.setItems(FXCollections.observableArrayList(pistaService.getAll())); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void editarPista() {
        Pista sel = tablaPistas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona una pista para editar."); return; }
        pistaEnEdicion = sel;
        lblFormPistaTitulo.setText("Editar Pista");
        txtPistaNombre.setText(sel.getNombre());
        cboPistaDeporte.setValue(sel.getDeporte());
        txtPistaSuperficie.setText(sel.getTipoSuperficie() != null ? sel.getTipoSuperficie() : "");
        txtPistaPrecio.setText(sel.getPrecioHora().toPlainString());
        chkPistaCubierta.setSelected(sel.isCubierta());
        chkPistaIluminacion.setSelected(sel.isIluminacion());
        btnCancelarEdicion.setVisible(true);
        btnCancelarEdicion.setManaged(true);
    }

    @FXML
    private void cancelarEdicionPista() {
        pistaEnEdicion = null;
        lblFormPistaTitulo.setText("Nueva Pista");
        txtPistaNombre.clear(); txtPistaSuperficie.clear(); txtPistaPrecio.clear();
        cboPistaDeporte.setValue(null);
        chkPistaCubierta.setSelected(false); chkPistaIluminacion.setSelected(false);
        btnCancelarEdicion.setVisible(false);
        btnCancelarEdicion.setManaged(false);
    }

    @FXML
    private void guardarPista() {
        String nombre = txtPistaNombre.getText();
        Deporte deporte = cboPistaDeporte.getValue();
        String superficie = txtPistaSuperficie.getText();
        String precioStr = txtPistaPrecio.getText();

        if (nombre.isBlank() || deporte == null || precioStr.isBlank()) {
            mostrarAlerta("Nombre, deporte y precio son obligatorios.");
            return;
        }
        try {
            BigDecimal precio = new BigDecimal(precioStr.trim().replace(",", "."));
            if (pistaEnEdicion != null) {
                // Modo edición
                pistaEnEdicion.setNombre(nombre.trim());
                pistaEnEdicion.setDeporte(deporte);
                pistaEnEdicion.setTipoSuperficie(superficie.trim());
                pistaEnEdicion.setPrecioHora(precio);
                pistaEnEdicion.setCubierta(chkPistaCubierta.isSelected());
                pistaEnEdicion.setIluminacion(chkPistaIluminacion.isSelected());
                pistaService.actualizar(pistaEnEdicion);
                mostrarInfo("Pista actualizada correctamente.");
                cancelarEdicionPista();
            } else {
                // Modo nueva
                Pista p = new Pista();
                p.setNombre(nombre.trim());
                p.setDeporte(deporte);
                p.setTipoSuperficie(superficie.trim());
                p.setPrecioHora(precio);
                p.setCubierta(chkPistaCubierta.isSelected());
                p.setIluminacion(chkPistaIluminacion.isSelected());
                org.example.sportacus.dao.InstalacionDAO instDAO = new org.example.sportacus.dao.InstalacionDAO();
                org.example.sportacus.model.Instalacion inst = instDAO.findFirst();
                if (inst == null) { mostrarAlerta("No hay ninguna instalación activa."); return; }
                p.setInstalacion(inst);
                pistaService.guardar(p);
                mostrarInfo("Pista guardada correctamente.");
                txtPistaNombre.clear(); txtPistaSuperficie.clear(); txtPistaPrecio.clear();
                cboPistaDeporte.setValue(null);
            }
            cargarTablaPistas();
        } catch (NumberFormatException e) {
            mostrarAlerta("El precio debe ser un número válido (ej: 12.50).");
        } catch (Exception e) { mostrarAlerta("Error: " + e.getMessage()); }
    }

    @FXML
    private void eliminarPista() {
        Pista sel = tablaPistas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona una pista."); return; }
        if (!sel.isActiva()) { mostrarAlerta("La pista ya está desactivada."); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("¿Desactivar la pista '" + sel.getNombre() + "'?");
        c.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    pistaService.desactivar(sel.getId());
                    cargarTablaPistas();
                    mostrarInfo("Pista desactivada.");
                } catch (Exception e) { mostrarAlerta("Error: " + e.getMessage()); }
            }
        });
    }

    @FXML
    private void eliminarPistaFisico() {
        Pista sel = tablaPistas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona una pista."); return; }
        if (sel.isActiva()) { mostrarAlerta("Solo se pueden eliminar pistas desactivadas."); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("¿Eliminar definitivamente la pista '" + sel.getNombre() + "'?\nEsta acción no se puede deshacer.");
        c.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    pistaService.eliminar(sel.getId());
                    cargarTablaPistas();
                    mostrarInfo("Pista eliminada.");
                } catch (Exception e) { mostrarAlerta("Error al eliminar: " + e.getMessage()); }
            }
        });
    }

    // ===== USUARIOS =====
    private void configurarTablaUsuarios() {
        colUsuId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colUsuNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colUsuEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colUsuRol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRol()));
        colUsuActivo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActivo() ? "Sí" : "No"));
    }

    private java.util.List<Usuario> todosUsuarios = new java.util.ArrayList<>();

    private void cargarTablaUsuarios() {
        paginaUsuarios = 0;
        try {
            todosUsuarios = usuarioService.getAll();
            aplicarPaginaUsuarios();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void aplicarPaginaUsuarios() {
        int total = todosUsuarios.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        paginaUsuarios = Math.min(paginaUsuarios, totalPaginas - 1);
        int desde = paginaUsuarios * PAGE_SIZE;
        tablaUsuarios.setItems(FXCollections.observableArrayList(
                todosUsuarios.subList(desde, Math.min(desde + PAGE_SIZE, total))));
        lblUsuPagina.setText("Página " + (paginaUsuarios + 1) + " de " + totalPaginas);
        btnUsuPrev.setDisable(paginaUsuarios == 0);
        btnUsuNext.setDisable(paginaUsuarios >= totalPaginas - 1);
    }

    @FXML private void usuariosPrevPage() { paginaUsuarios--; aplicarPaginaUsuarios(); }
    @FXML private void usuariosNextPage() { paginaUsuarios++; aplicarPaginaUsuarios(); }

    @FXML
    private void desactivarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona un usuario."); return; }
        if (sel.getId().equals(SessionManager.getUsuarioActual().getId())) {
            mostrarAlerta("No puedes desactivarte a ti mismo."); return;
        }
        if (!sel.isActivo()) { mostrarAlerta("El usuario ya está desactivado."); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("¿Desactivar al usuario " + sel.getEmail() + "?");
        c.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    usuarioService.desactivar(sel.getId());
                    cargarTablaUsuarios();
                    mostrarInfo("Usuario desactivado.");
                } catch (Exception e) { mostrarAlerta("Error: " + e.getMessage()); }
            }
        });
    }

    @FXML
    private void eliminarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona un usuario."); return; }
        if (sel.getId().equals(SessionManager.getUsuarioActual().getId())) {
            mostrarAlerta("No puedes eliminarte a ti mismo."); return;
        }
        if (sel.isActivo()) { mostrarAlerta("Solo se pueden eliminar usuarios desactivados."); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("¿Eliminar definitivamente al usuario " + sel.getEmail() + "?\nEsta acción no se puede deshacer.");
        c.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    usuarioService.eliminar(sel.getId());
                    cargarTablaUsuarios();
                    mostrarInfo("Usuario eliminado.");
                } catch (Exception e) { mostrarAlerta("Error al eliminar: " + e.getMessage()); }
            }
        });
    }

    // ===== DEPORTES =====
    private void configurarTablaDeportes() {
        colDepId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colDepNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
    }

    private void cargarTablaDeportes() {
        try { tablaDeportes.setItems(FXCollections.observableArrayList(deporteService.getAll())); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void eliminarDeporte() {
        Deporte sel = tablaDeportes.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona un deporte."); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("¿Eliminar el deporte '" + sel.getNombre() + "'?\nSolo es posible si no tiene pistas asociadas.");
        c.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    deporteService.eliminar(sel.getId());
                    mostrarInfo("Deporte eliminado.");
                    cargarTablaDeportes();
                    cboPistaDeporte.setItems(FXCollections.observableArrayList(deporteService.getAll()));
                } catch (Exception e) {
                    mostrarAlerta("No se puede eliminar: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void guardarDeporte() {
        String nombre = txtDeporteNombre.getText();
        if (nombre.isBlank()) { mostrarAlerta("El nombre no puede estar vacio."); return; }
        try {
            deporteService.guardar(new Deporte(nombre.trim()));
            mostrarInfo("Deporte guardado.");
            txtDeporteNombre.clear();
            cargarTablaDeportes();
            cboPistaDeporte.setItems(FXCollections.observableArrayList(deporteService.getAll()));
        } catch (Exception e) { mostrarAlerta("Error: " + e.getMessage()); }
    }

    // ===== SESION =====
    @FXML
    private void cerrarSesion() {
        SessionManager.cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(SportacusApp.class.getResource("fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = (Stage) lblAdminNombre.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
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

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
