package org.example.sportacus.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pistas")
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instalacion_id", nullable = false)
    private Instalacion instalacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deporte_id", nullable = false)
    private Deporte deporte;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(name = "tipo_superficie", length = 40)
    private String tipoSuperficie;

    @Column(nullable = false)
    private boolean cubierta = false;

    @Column(nullable = false)
    private boolean iluminacion = false;

    private Integer capacidad;

    @Column(name = "precio_hora", nullable = false, precision = 8, scale = 2)
    private BigDecimal precioHora = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean activa = true;

    @OneToMany(mappedBy = "pista", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>();

    public Pista() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instalacion getInstalacion() { return instalacion; }
    public void setInstalacion(Instalacion instalacion) { this.instalacion = instalacion; }

    public Deporte getDeporte() { return deporte; }
    public void setDeporte(Deporte deporte) { this.deporte = deporte; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoSuperficie() { return tipoSuperficie; }
    public void setTipoSuperficie(String tipoSuperficie) { this.tipoSuperficie = tipoSuperficie; }

    public boolean isCubierta() { return cubierta; }
    public void setCubierta(boolean cubierta) { this.cubierta = cubierta; }

    public boolean isIluminacion() { return iluminacion; }
    public void setIluminacion(boolean iluminacion) { this.iluminacion = iluminacion; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public BigDecimal getPrecioHora() { return precioHora; }
    public void setPrecioHora(BigDecimal precioHora) { this.precioHora = precioHora; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }

    @Override
    public String toString() {
        return nombre + " (" + (deporte != null ? deporte.getNombre() : "") + ")";
    }
}
