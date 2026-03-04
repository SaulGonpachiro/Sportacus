package org.example.sportacus.service;

import org.example.sportacus.dao.PistaDAO;
import org.example.sportacus.model.Pista;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class PistaService {

    private final PistaDAO pistaDAO = new PistaDAO();

    public List<Pista> getAll() {
        return pistaDAO.getAll();
    }

    public List<Pista> getPaginated(int page, int offset, HashMap<String, String> filtros) {
        return pistaDAO.getPaginated(page, offset, filtros);
    }

    public long count(HashMap<String, String> filtros) {
        return pistaDAO.count(filtros);
    }

    public List<Pista> getDisponibles(Short deporteId, LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        if (!fin.isAfter(inicio)) throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio.");
        return pistaDAO.getPistasDisponibles(deporteId, inicio, fin);
    }

    public List<Pista> getByDeporte(Short deporteId) {
        return pistaDAO.getByDeporte(deporteId);
    }

    public void guardar(Pista pista) {
        if (pista.getNombre() == null || pista.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre de la pista no puede estar vacío.");
        if (pista.getDeporte() == null)
            throw new IllegalArgumentException("Debe seleccionar un deporte.");
        if (pista.getPrecioHora() == null)
            throw new IllegalArgumentException("El precio por hora es obligatorio.");
        pistaDAO.save(pista);
    }

    public void actualizar(Pista pista) {
        guardar(pista); // mismas validaciones
        pistaDAO.update(pista);
    }

    public void desactivar(Long id) {
        pistaDAO.delete(id);
    }

    public void eliminar(Long id) {
        pistaDAO.hardDelete(id);
    }
}
