package com.example.demo.controller;

import com.example.demo.modelo.OrdenTrabajo;
import com.example.demo.modelo.Usuario;
import com.example.demo.servicio.OrdenTrabajoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final OrdenTrabajoService ordenService;

    public DashboardController(OrdenTrabajoService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping("/ingresos")
    public String graficoIngresos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"Admin".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/login?denegado=true";
        }

        List<OrdenTrabajo> ordenes = ordenService.obtenerTodas();
        Map<String, Double> ingresosPorFormato = ordenes.stream()
                .collect(Collectors.groupingBy(
                        OrdenTrabajo::getFormato,
                        Collectors.summingDouble(OrdenTrabajo::getMontoEstimado)
                ));

        model.addAttribute("etiquetas", List.copyOf(ingresosPorFormato.keySet()));
        model.addAttribute("valores", List.copyOf(ingresosPorFormato.values()));

        return "grafico-ingresos";
    }

    @GetMapping("/distribucion")
    public String graficoDistribucion(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"Admin".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/login?denegado=true";
        }

        List<OrdenTrabajo> ordenes = ordenService.obtenerTodas();
        Map<String, Long> conteoPorFormato = ordenes.stream()
                .collect(Collectors.groupingBy(OrdenTrabajo::getFormato, Collectors.counting()));

        Map<String, Long> conteoPorEstado = ordenes.stream()
                .collect(Collectors.groupingBy(OrdenTrabajo::getEstado, Collectors.counting()));

        model.addAttribute("formatos", List.copyOf(conteoPorFormato.keySet()));
        model.addAttribute("cantidades", List.copyOf(conteoPorFormato.values()));
        model.addAttribute("estados", List.copyOf(conteoPorEstado.keySet()));
        model.addAttribute("cantidadesEstados", List.copyOf(conteoPorEstado.values()));

        return "grafico-distribucion";
    }
}