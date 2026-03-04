package org.example.sportacus.util;

import org.example.sportacus.model.Usuario;

public class SessionManager {

    private static Usuario usuarioActual;

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean isAdmin() {
        return usuarioActual != null && "ADMIN".equals(usuarioActual.getRol());
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
