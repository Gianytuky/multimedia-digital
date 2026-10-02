package com.example.demo.controller;

import com.example.demo.modelo.OrdenTrabajo;
import com.example.demo.modelo.ServicioTarifa;
import com.example.demo.modelo.Usuario;
import com.example.demo.servicio.OrdenTrabajoService;
import com.example.demo.servicio.ServicioTarifaService;
import com.example.demo.servicio.PdfService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ordenes")
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenService;
    private final ServicioTarifaService tarifaService;
    private final PdfService pdfService;

    public OrdenTrabajoController(OrdenTrabajoService ordenService,
                                  ServicioTarifaService tarifaService,
                                  PdfService pdfService) {
        this.ordenService = ordenService;
        this.tarifaService = tarifaService;
        this.pdfService = pdfService;
    }

    @GetMapping
    public String listarOrdenes(@RequestParam(required = false) String criterio,
                                @RequestParam(required = false) String estadoFiltro,
                                @RequestParam(required = false) Boolean mantFiltro,
                                @RequestParam(required = false) Double montoMin,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                @RequestParam(required = false) String codEditar,
                                HttpSession session,
                                Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Ejecutivo".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        // Búsqueda avanzada incluyendo el rango de fechas
        List<OrdenTrabajo> listaOTs = ordenService.buscarAvanzado(criterio, estadoFiltro, mantFiltro, montoMin, fechaInicio, fechaFin);

        double totalFiltrado = listaOTs.stream()
                .filter(o -> !"Cancelado".equalsIgnoreCase(o.getEstado()))
                .mapToDouble(OrdenTrabajo::getMontoEstimado)
                .sum();

        List<ServicioTarifa> tarifario = tarifaService.obtenerTarifario();

        model.addAttribute("ordenes", listaOTs);
        model.addAttribute("tarifas", tarifario);
        model.addAttribute("totalGeneral", totalFiltrado);
        model.addAttribute("totalRegistros", listaOTs.size());

        model.addAttribute("criterio", criterio);
        model.addAttribute("estadoFiltro", estadoFiltro);
        model.addAttribute("mantFiltro", mantFiltro);
        model.addAttribute("montoMin", montoMin);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        if (codEditar != null && !codEditar.isEmpty()) {
            OrdenTrabajo otEdit = ordenService.buscarPorCodigo(codEditar);
            if (otEdit != null) {
                if (!otEdit.isEditable() && !"Admin".equalsIgnoreCase(usuario.getRol())) {
                    model.addAttribute("bloqueoMsg", "La orden " + otEdit.getCodigoOT() + " ya fue procesada por el laboratorio técnico y se encuentra bloqueada.");
                } else {
                    model.addAttribute("otEdit", otEdit);
                }
            }
        }

        return "ordenes";
    }

    @PostMapping("/guardar")
    public String guardarOrden(@RequestParam String cliente,
                               @RequestParam String telefono,
                               @RequestParam String formato,
                               @RequestParam int cantidad,
                               @RequestParam(defaultValue = "false") boolean mantenimiento,
                               @RequestParam(defaultValue = "0.0") double horasReales,
                               @RequestParam(defaultValue = "0.0") double pesoGB,
                               HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Ejecutivo".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        ordenService.registrarOT(cliente, telefono, formato, cantidad, mantenimiento, horasReales, pesoGB, "En Recepción");
        return "redirect:/ordenes";
    }

    @PostMapping("/actualizar")
    public String actualizarOrden(@RequestParam String codigoOT,
                                  @RequestParam String cliente,
                                  @RequestParam String telefono,
                                  @RequestParam String formato,
                                  @RequestParam int cantidad,
                                  @RequestParam(defaultValue = "false") boolean mantenimiento,
                                  @RequestParam(defaultValue = "0.0") double horasReales,
                                  @RequestParam(defaultValue = "0.0") double pesoGB,
                                  @RequestParam String estado,
                                  HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Ejecutivo".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        ordenService.actualizarOT(codigoOT, cliente, telefono, formato, cantidad, mantenimiento, horasReales, pesoGB, estado);
        return "redirect:/ordenes";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarOrden(@PathVariable("id") String codigoOT, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Ejecutivo".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        ordenService.cancelarOT(codigoOT);
        return "redirect:/ordenes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarOrden(@PathVariable("id") String codigoOT, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Ejecutivo".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        ordenService.eliminarOT(codigoOT);
        return "redirect:/ordenes";
    }

    @GetMapping("/liquidar/{id}")
    public String liquidarEntrega(@PathVariable("id") String codigoOT, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Ejecutivo".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return "redirect:/login?denegado=true";
        }

        ordenService.finalizarOrden(codigoOT);
        return "redirect:/ordenes";
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> descargarBoletaPdf(@PathVariable("id") String codigoOT, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"Ejecutivo".equalsIgnoreCase(usuario.getRol()) && !"Admin".equalsIgnoreCase(usuario.getRol()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        OrdenTrabajo ot = ordenService.buscarPorCodigo(codigoOT);
        if (ot == null) {
            return ResponseEntity.notFound().build();
        }

        // CÁLCULO MÁTICO DE SUB-TOTAL E IGV (18%)
        double montoTotal = ot.getMontoEstimado();
        double subtotal = montoTotal / 1.18;
        double igv = montoTotal - subtotal;

        double unidades = (ot.getHorasReales() > 0) ? ot.getHorasReales() : ot.getCantidad();
        double precioUnitario = (unidades > 0) ? (montoTotal / unidades) : montoTotal;

        // Número correlativo de comprobante basado en el código OT
        String numeroComprobante = "B001-" + ot.getCodigoOT().replace("OT-", "");

        // Generar mapa de variables para Thymeleaf
        Map<String, Object> datos = new HashMap<>();
        datos.put("ot", ot);
        datos.put("subtotal", subtotal);
        datos.put("igv", igv);
        datos.put("precioUnitario", precioUnitario);
        datos.put("numeroComprobante", numeroComprobante);
        datos.put("fechaEmision", LocalDateTime.now());

        byte[] pdfBytes = pdfService.generarPdfDesdeThymeleaf("pdf/boleta-pdf", datos);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("Boleta_" + ot.getCodigoOT() + ".pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}