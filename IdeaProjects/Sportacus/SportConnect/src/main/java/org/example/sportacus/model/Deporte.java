package org.example.sportacus.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deportes")
public class Deporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true, length = 40)
    private String nombre;

    @Column(length = 120)
    private String icono;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @OneToMany(mappedBy = "deporte", fetch = FetchType.LAZY)
    private List<Pista> pistas = new ArrayList<>();

    public Deporte() {}

    public Deporte(String nombre) {
        this.nombre = nombre;
    }

    public Short getId() { return id; }
    public void setId(Short id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public List<Pista> getPistas() { return pistas; }
    public void setPistas(List<Pista> pistas) { this.pistas = pistas; }

    @Override
    public String toString() { return nombre; }
}
