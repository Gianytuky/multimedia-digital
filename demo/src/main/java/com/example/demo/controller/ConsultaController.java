package com.example.demo.controller;

import com.example.demo.modelo.ContactoMensaje;
import com.example.demo.modelo.Usuario;
import com.example.demo.servicio.ContactoMensajeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ConsultaController {

    private final ContactoMensajeService contactoService;

    public ConsultaController(ContactoMensajeService contactoService) {
        this.contactoService = contactoService;
    }

    // Ruta pública de formulario de contacto
    @GetMapping("/contacto")
    public String vistaContacto(@RequestParam(required = false) String enviado, Model model) {
        model.addAttribute("enviado", "true".equalsIgnoreCase(enviado));
        return "contacto";
    }

    @PostMapping("/contacto")
    public String registrarContacto(@RequestParam String nombre,
                                    @RequestParam String email,
                                    @RequestParam String telefono,
                                    @RequestParam String mensaje) {
        contactoService.registrarMensaje(nombre, email, telefono, mensaje);
        return "redirect:/contacto?enviado=true";
    }

    // RUTA PRIVADA BANDEJA DE CONSULTAS CON FILTRO POR FECHAS
    @GetMapping("/consultas")
    public String verBandejaConsultas(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                      HttpSession session, 
                                      Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Ejecutivo".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        List<ContactoMensaje> listaFiltrada = contactoService.buscarPorRangoFechas(fechaInicio, fechaFin);

        model.addAttribute("mensajes", listaFiltrada);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        return "consultas";
    }

    @GetMapping("/consultas/eliminar/{codigo}")
    public String eliminarConsulta(@PathVariable("codigo") String codigo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Ejecutivo".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        contactoService.eliminarMensaje(codigo);
        return "redirect:/consultas";
    }
}