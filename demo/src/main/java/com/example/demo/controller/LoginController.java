package com.example.demo.controller;

import com.example.demo.modelo.Usuario;
import com.example.demo.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String loginView(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String denegado,
                            HttpSession session,
                            Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion != null) {
            return redirigirSegunRol(usuarioSesion.getRol());
        }

        if ("credenciales-invalidas".equalsIgnoreCase(error)) {
            model.addAttribute("errorMsg", "Credenciales incorrectas. Verifique su correo y contraseña.");
        }
        if ("true".equalsIgnoreCase(logout)) {
            model.addAttribute("logoutMsg", "Ha cerrado su sesión correctamente.");
        }
        if ("true".equalsIgnoreCase(denegado)) {
            model.addAttribute("denegadoMsg", "Acceso denegado. Inicie sesión con el rol autorizado para acceder a este módulo.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String password,
                                HttpSession session) {
        Usuario usuario = usuarioService.autenticar(correo, password);

        if (usuario == null) {
            return "redirect:/login?error=credenciales-invalidas";
        }

        // Guardamos usuario en sesión HTTP
        session.setAttribute("usuario", usuario);

        // Redirección automática según el rol
        return redirigirSegunRol(usuario.getRol());
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }

    private String redirigirSegunRol(String rol) {
        if ("Ejecutivo".equalsIgnoreCase(rol)) {
            return "redirect:/ordenes";
        } else if ("Técnico".equalsIgnoreCase(rol)) {
            return "redirect:/tecnico";
        } else if ("Admin".equalsIgnoreCase(rol)) {
            return "redirect:/admin";
        }
        return "redirect:/";
    }
}
