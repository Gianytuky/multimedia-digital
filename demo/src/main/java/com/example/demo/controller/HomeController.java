package com.example.demo.controller;

import com.example.demo.servicio.ServicioTarifaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ServicioTarifaService tarifaService;

    // Inyección del servicio de tarifas
    public HomeController(ServicioTarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/servicios")
    public String servicios(Model model) {
        // Se envían los formatos y precios a la plantilla de Thymeleaf
        model.addAttribute("tarifas", tarifaService.obtenerTarifario());
        return "servicios";
    }
}