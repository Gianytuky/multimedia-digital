package com.example.demo.controller;

import com.example.demo.modelo.OrdenTrabajo;
import com.example.demo.modelo.Usuario;
import com.example.demo.servicio.OrdenTrabajoService;
import com.example.demo.servicio.ServicioTarifaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final OrdenTrabajoService ordenService;
    private final ServicioTarifaService tarifaService;

    public AdminController(OrdenTrabajoService ordenService, ServicioTarifaService tarifaService) {
        this.ordenService = ordenService;
        this.tarifaService = tarifaService;
    }

    @GetMapping("/admin")
    public String panelAdmin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        // Control de roles: solo Admin puede ingresar
        if (usuario == null) {
            return "redirect:/login?denegado=true";
        }
        if (!"Admin".equalsIgnoreCase(usuario.getRol())) {
            return "redirect:/login?denegado=true";
        }

        List<OrdenTrabajo> ordenes = ordenService.obtenerTodas();

        // Métricas KPIs gerenciales
        model.addAttribute("totalIngresos", ordenService.calcularTotalGeneral());
        model.addAttribute("totalOTs", ordenes.size());
        model.addAttribute("totalHoras", ordenService.obtenerTotalHoras());
        model.addAttribute("totalGB", ordenService.obtenerTotalGB());
        model.addAttribute("tarifas", tarifaService.obtenerTarifario());

        // Gráfico de Ingresos por Formato
        Map<String, Double> ingresosPorFormato = ordenes.stream()
                .collect(Collectors.groupingBy(
                        OrdenTrabajo::getFormato,
                        Collectors.summingDouble(OrdenTrabajo::getMontoEstimado)
                ));
        model.addAttribute("etiquetasIngresos", List.copyOf(ingresosPorFormato.keySet()));
        model.addAttribute("valoresIngresos", List.copyOf(ingresosPorFormato.values()));

        // Gráfico Circular de Participación
        Map<String, Long> conteoPorFormato = ordenes.stream()
                .collect(Collectors.groupingBy(
                        OrdenTrabajo::getFormato,
                        Collectors.counting()
                ));
        model.addAttribute("formatos", List.copyOf(conteoPorFormato.keySet()));
        model.addAttribute("cantidades", List.copyOf(conteoPorFormato.values()));

        return "admin";
    }
}
