package com.example.demo.servicio;

import com.example.demo.modelo.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();

    public UsuarioService() {
        // Usuarios semilla para los 3 roles del sistema
        usuarios.add(new Usuario("Carlos Mendoza", "ejecutivo@tienda.com", "123", "Ejecutivo"));
        usuarios.add(new Usuario("Ana Torres", "tecnico@tienda.com", "123", "Técnico"));
        usuarios.add(new Usuario("Gerente General", "admin@tienda.com", "123", "Admin"));
    }

    public Usuario autenticar(String correo, String password) {
        if (correo == null || password == null) return null;
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo.trim()) && u.getPassword().equals(password.trim()))
                .findFirst()
                .orElse(null);
    }

    public Usuario buscarPorCorreo(String correo) {
        if (correo == null) return null;
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo.trim()))
                .findFirst()
                .orElse(null);
    }

    public List<Usuario> obtenerTodos() {
        return usuarios;
    }
}
