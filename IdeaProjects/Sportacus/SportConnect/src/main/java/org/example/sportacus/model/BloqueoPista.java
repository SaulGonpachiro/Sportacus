package org.example.sportacus.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bloqueos_pista")
public class BloqueoPista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pista_id", nullable = false)
    private Pista pista;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fin;

    @Column(length = 200)
    private String motivo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();

    public BloqueoPista() {}

    public BloqueoPista(Pista pista, LocalDateTime inicio, LocalDateTime fin, String motivo, Usuario creadoPor) {
        this.pista = pista;
        this.inicio = inicio;
        this.fin = fin;
        this.motivo = motivo;
        this.creadoPor = creadoPor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pista getPista() { return pista; }
    public void setPista(Pista pista) { this.pista = pista; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Usuario getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Usuario creadoPor) { this.creadoPor = creadoPor; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
