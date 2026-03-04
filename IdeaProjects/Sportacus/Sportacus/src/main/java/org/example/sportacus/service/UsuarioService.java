package org.example.sportacus.service;

import org.example.sportacus.dao.UsuarioDAO;
import org.example.sportacus.model.Usuario;

import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Optional<Usuario> login(String email, String password) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("El email no puede estar vacío.");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        return usuarioDAO.findByEmailAndPassword(email.trim(), password);
    }

    public void registrar(String nombre, String apellidos, String email, String password, String telefono) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (apellidos == null || apellidos.isBlank()) throw new IllegalArgumentException("Los apellidos no pueden estar vacíos.");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("El email no es válido.");
        if (password == null || password.length() < 4) throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");

        if (usuarioDAO.findByEmail(email.trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email.");
        }

        Usuario u = new Usuario(nombre.trim(), apellidos.trim(), email.trim().toLowerCase(), password, telefono);
        usuarioDAO.save(u);
    }

    public List<Usuario> getAll() {
        return usuarioDAO.getAll();
    }

    public List<Usuario> getPaginated(int page, int offset) {
        return usuarioDAO.getPaginated(page, offset);
    }

    public long count() {
        return usuarioDAO.count();
    }

    public void actualizar(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        usuarioDAO.update(usuario);
    }

    public void updateUltimoLogin(Long id) {
        usuarioDAO.updateUltimoLogin(id);
    }

    public void desactivar(Long id) {
        usuarioDAO.delete(id);
    }

    public void eliminar(Long id) {
        usuarioDAO.hardDelete(id);
    }
}
