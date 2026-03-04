package org.example.sportacus.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pista_id", nullable = false)
    private Pista pista;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fin;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "precio_total", nullable = false, precision = 8, scale = 2)
    private BigDecimal precioTotal = BigDecimal.ZERO;

    @Column(name = "creada_en", insertable = false, updatable = false)
    private LocalDateTime creadaEn;

    @Column(name = "cancelada_en")
    private LocalDateTime canceladaEn;

    @Column(name = "motivo_cancelacion", length = 200)
    private String motivoCancelacion;

    public Reserva() {}

    public Reserva(Usuario usuario, Pista pista, LocalDateTime inicio, LocalDateTime fin, BigDecimal precioTotal) {
        this.usuario = usuario;
        this.pista = pista;
        this.inicio = inicio;
        this.fin = fin;
        this.precioTotal = precioTotal;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Pista getPista() { return pista; }
    public void setPista(Pista pista) { this.pista = pista; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }

    public LocalDateTime getCreadaEn() { return creadaEn; }
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }

    public LocalDateTime getCanceladaEn() { return canceladaEn; }
    public void setCanceladaEn(LocalDateTime canceladaEn) { this.canceladaEn = canceladaEn; }

    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }

    // Métodos de conveniencia para TableView
    public String getPistaNombre() { return pista != null ? pista.getNombre() : ""; }
    public String getDeporteNombre() { return pista != null && pista.getDeporte() != null ? pista.getDeporte().getNombre() : ""; }
    public String getUsuarioNombre() { return usuario != null ? usuario.getNombreCompleto() : ""; }

    @Override
    public String toString() {
        return "Reserva{id=" + id + ", pista=" + (pista != null ? pista.getNombre() : "") + ", inicio=" + inicio + "}";
    }
}
