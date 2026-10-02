package com.example.demo.controller;

import com.example.demo.modelo.OrdenTrabajo;
import com.example.demo.modelo.Usuario;
import com.example.demo.servicio.OrdenTrabajoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class TecnicoController {

    private final OrdenTrabajoService ordenService;

    public TecnicoController(OrdenTrabajoService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping("/tecnico")
    public String panelTecnico(@RequestParam(required = false) String criterio,
                               @RequestParam(required = false) Boolean mantFiltro,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                               @RequestParam(required = false) String codCaptura,
                               @RequestParam(required = false) String exito,
                               HttpSession session,
                               Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Técnico".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        // Obtener cola filtrada
        List<OrdenTrabajo> colaPendientes = ordenService.buscarPendientesTecnicoAvanzado(criterio, mantFiltro, fechaInicio, fechaFin);
        List<OrdenTrabajo> todas = ordenService.obtenerTodas();

        model.addAttribute("colaPendientes", colaPendientes);
        model.addAttribute("todas", todas);
        model.addAttribute("totalPendientes", ordenService.obtenerPendientesTecnico().size());
        model.addAttribute("totalHoras", ordenService.obtenerTotalHoras());
        model.addAttribute("totalGB", ordenService.obtenerTotalGB());

        // Mantener valores de los filtros en la vista
        model.addAttribute("criterio", criterio);
        model.addAttribute("mantFiltro", mantFiltro);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        if (codCaptura != null && !codCaptura.isEmpty()) {
            OrdenTrabajo otCaptura = ordenService.buscarPorCodigo(codCaptura);
            model.addAttribute("otCaptura", otCaptura);
        }

        if ("captura-guardada".equalsIgnoreCase(exito)) {
            model.addAttribute("exitoMsg", "¡Captura técnica registrada exitosamente! La orden pasó a estado 'Por Notificar'.");
        }

        return "tecnico";
    }

    @PostMapping("/tecnico/captura")
    public String registrarCaptura(@RequestParam String codigoOT,
                                   @RequestParam double horasReales,
                                   @RequestParam double pesoGB,
                                   @RequestParam String medioEntrega,
                                   HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Técnico".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        ordenService.procesarCapturaTecnica(codigoOT, horasReales, pesoGB, medioEntrega);
        return "redirect:/tecnico?exito=captura-guardada";
    }
}