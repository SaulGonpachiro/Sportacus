package org.example.sportacus.service;

import org.example.sportacus.dao.ReservaDAO;
import org.example.sportacus.model.Pista;
import org.example.sportacus.model.Reserva;
import org.example.sportacus.model.Usuario;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ReservaService {

    private final ReservaDAO reservaDAO = new ReservaDAO();

    public Reserva crear(Usuario usuario, Pista pista, LocalDateTime inicio, LocalDateTime fin) {
        if (usuario == null) throw new IllegalArgumentException("El usuario no puede ser nulo.");
        if (pista == null) throw new IllegalArgumentException("Debe seleccionar una pista.");
        if (inicio == null || fin == null) throw new IllegalArgumentException("Las fechas son obligatorias.");
        if (!fin.isAfter(inicio)) throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio.");
        if (inicio.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("No se puede reservar en el pasado.");

        if (reservaDAO.existeSolape(pista.getId(), inicio, fin)) {
            throw new IllegalStateException("La pista ya está ocupada en ese horario.");
        }

        // Calcular precio: precio_hora * horas
        long minutos = Duration.between(inicio, fin).toMinutes();
        BigDecimal horas = BigDecimal.valueOf(minutos).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal precioTotal = pista.getPrecioHora().multiply(horas).setScale(2, RoundingMode.HALF_UP);

        Reserva reserva = new Reserva(usuario, pista, inicio, fin, precioTotal);
        reserva.setEstado("CONFIRMADA");
        reservaDAO.save(reserva);
        return reserva;
    }

    public void cancelar(Long reservaId, String motivo) {
        if (reservaId == null) throw new IllegalArgumentException("ID de reserva inválido.");
        reservaDAO.cancelar(reservaId, motivo);
    }

    public void eliminar(Long reservaId) {
        if (reservaId == null) throw new IllegalArgumentException("ID de reserva inválido.");
        reservaDAO.delete(reservaId);
    }

    public List<Reserva> getMisReservas(Long usuarioId) {
        return reservaDAO.getByUsuario(usuarioId);
    }

    public List<Reserva> getProximas(Long usuarioId) {
        return reservaDAO.getProximasByUsuario(usuarioId);
    }

    public List<Reserva> getAll() {
        return reservaDAO.getAll();
    }

    public List<Reserva> getPaginated(int page, int offset) {
        return reservaDAO.getPaginated(page, offset);
    }

    public long count() {
        return reservaDAO.count();
    }
}
