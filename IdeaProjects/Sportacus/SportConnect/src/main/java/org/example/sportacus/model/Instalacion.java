package org.example.sportacus.model;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instalaciones")
public class Instalacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 200)
    private String direccion;

    @Column(length = 25)
    private String telefono;

    @Column(name = "hora_apertura")
    private LocalTime horaApertura;

    @Column(name = "hora_cierre")
    private LocalTime horaCierre;

    @Column(nullable = false)
    private boolean activa = true;

    @OneToMany(mappedBy = "instalacion", fetch = FetchType.LAZY)
    private List<Pista> pistas = new ArrayList<>();

    public Instalacion() {}

    public Instalacion(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalTime getHoraApertura() { return horaApertura; }
    public void setHoraApertura(LocalTime horaApertura) { this.horaApertura = horaApertura; }

    public LocalTime getHoraCierre() { return horaCierre; }
    public void setHoraCierre(LocalTime horaCierre) { this.horaCierre = horaCierre; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public List<Pista> getPistas() { return pistas; }
    public void setPistas(List<Pista> pistas) { this.pistas = pistas; }

    @Override
    public String toString() { return nombre; }
}
