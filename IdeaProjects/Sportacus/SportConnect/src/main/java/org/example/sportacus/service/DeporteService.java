package org.example.sportacus.service;

import org.example.sportacus.dao.DeporteDAO;
import org.example.sportacus.model.Deporte;

import java.util.List;

public class DeporteService {

    private final DeporteDAO deporteDAO = new DeporteDAO();

    public List<Deporte> getAll() {
        return deporteDAO.getAll();
    }

    public void eliminar(Short id) {
        deporteDAO.delete(id);
    }

    public void guardar(Deporte deporte) {
        if (deporte.getNombre() == null || deporte.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del deporte no puede estar vacío.");
        deporteDAO.save(deporte);
    }
}
