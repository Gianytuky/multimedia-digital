package com.example.demo.servicio;

import com.example.demo.modelo.OrdenTrabajo; 
import com.example.demo.modelo.ServicioTarifa; 
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList; 
import java.util.List; 
import java.util.stream.Collectors;

@Service 
public class OrdenTrabajoService {

    private final List<OrdenTrabajo> listaOrdenes = new ArrayList<>();
    private final ServicioTarifaService tarifaService;
    private int contadorOT = 1001;

    public OrdenTrabajoService(ServicioTarifaService tarifaService) {
        this.tarifaService = tarifaService;
        
        // Datos semilla iniciales con fechas recientes
        registrarOT("Juan Pérez", "912345678", "VHS", 2, true, 0.0, 0.0, "En Recepción");
        registrarOT("María Gonzales", "987654321", "Betamax", 1, false, 1.5, 12.0, "Por Notificar");
        registrarOT("Jorge Ramos", "955443322", "LongPlay (LP)", 3, false, 0.0, 1.8, "Finalizado");
        registrarOT("Giancarlo Rivas", "998877665", "MiniDV", 2, true, 0.0, 0.0, "En Recepción");
    }

    public List<OrdenTrabajo> obtenerTodas() {
        return listaOrdenes;
    }

    public List<OrdenTrabajo> obtenerPendientesTecnico() {
        return listaOrdenes.stream()
                .filter(o -> "En Recepción".equalsIgnoreCase(o.getEstado()) || "En Proceso".equalsIgnoreCase(o.getEstado()))
                .collect(Collectors.toList());
    }

    public OrdenTrabajo buscarPorCodigo(String codigoOT) {
        return listaOrdenes.stream()
                .filter(o -> o.getCodigoOT().equalsIgnoreCase(codigoOT))
                .findFirst()
                .orElse(null);
    }

    // BÚSQUEDA AVANZADA PARA LA GESTIÓN DE OTs (EJE / ADMIN)
    public List<OrdenTrabajo> buscarAvanzado(String criterio, String estadoFiltro, Boolean mantFiltro, Double montoMin, LocalDate fechaInicio, LocalDate fechaFin) {
        return listaOrdenes.stream()
                .filter(o -> {
                    // 1. Texto libre (código, cliente, teléfono, formato)
                    boolean matchTexto = (criterio == null || criterio.isBlank()) ||
                            o.getCodigoOT().toLowerCase().contains(criterio.toLowerCase().trim()) ||
                            o.getCliente().toLowerCase().contains(criterio.toLowerCase().trim()) ||
                            o.getFormato().toLowerCase().contains(criterio.toLowerCase().trim()) ||
                            o.getTelefono().contains(criterio.trim());

                    // 2. Filtro por Estado
                    boolean matchEstado = (estadoFiltro == null || estadoFiltro.isBlank()) ||
                            o.getEstado().equalsIgnoreCase(estadoFiltro);

                    // 3. Filtro por Mantenimiento
                    boolean matchMant = (mantFiltro == null) ||
                            (o.isMantenimiento() == mantFiltro);

                    // 4. Filtro por Monto Mínimo
                    boolean matchMonto = (montoMin == null || montoMin <= 0) ||
                            (o.getMontoEstimado() >= montoMin);

                    // 5. Filtro por Rango de Fechas de Registro
                    LocalDate fechaOT = o.getFechaRegistro().toLocalDate();
                    boolean matchInicio = (fechaInicio == null) || !fechaOT.isBefore(fechaInicio);
                    boolean matchFin = (fechaFin == null) || !fechaOT.isAfter(fechaFin);

                    return matchTexto && matchEstado && matchMant && matchMonto && matchInicio && matchFin;
                })
                .collect(Collectors.toList());
    }

    // BÚSQUEDA AVANZADA PARA LA COLA DEL TÉCNICO (Criterio, Mantenimiento, Fechas)
    public List<OrdenTrabajo> buscarPendientesTecnicoAvanzado(String criterio, Boolean mantFiltro, LocalDate fechaInicio, LocalDate fechaFin) {
        return obtenerPendientesTecnico().stream()
                .filter(o -> {
                    // 1. Texto libre (código OT, cliente o formato)
                    boolean matchTexto = (criterio == null || criterio.isBlank()) ||
                            o.getCodigoOT().toLowerCase().contains(criterio.toLowerCase().trim()) ||
                            o.getCliente().toLowerCase().contains(criterio.toLowerCase().trim()) ||
                            o.getFormato().toLowerCase().contains(criterio.toLowerCase().trim());

                    // 2. Filtro por Mantenimiento (Sí / No)
                    boolean matchMant = (mantFiltro == null) || (o.isMantenimiento() == mantFiltro);

                    // 3. Filtro por Rango de Fechas
                    LocalDate fechaOT = o.getFechaRegistro().toLocalDate();
                    boolean matchInicio = (fechaInicio == null) || !fechaOT.isBefore(fechaInicio);
                    boolean matchFin = (fechaFin == null) || !fechaOT.isAfter(fechaFin);

                    return matchTexto && matchMant && matchInicio && matchFin;
                })
                .collect(Collectors.toList());
    }

    public double obtenerTotalHoras() {
        return listaOrdenes.stream()
                .mapToDouble(OrdenTrabajo::getHorasReales)
                .sum();
    }

    public double obtenerTotalGB() {
        return listaOrdenes.stream()
                .mapToDouble(OrdenTrabajo::getPesoGB)
                .sum();
    }

    public void registrarOT(String cliente, String telefono, String formato, int cantidad, 
                            boolean mantenimiento, double horasReales, double pesoGB, String estado) {
        ServicioTarifa tarifa = tarifaService.buscarPorFormato(formato);
        double precioBase = (tarifa != null) ? tarifa.getPrecioBase() : 0.0;
        
        double costoMantenimiento = mantenimiento ? 15.0 : 0.0;
        double montoEstimado = (precioBase * cantidad) + costoMantenimiento;

        String nuevoCodigo = "OT-" + (contadorOT++);
        String estadoInicial = (estado == null || estado.isBlank()) ? "En Recepción" : estado;

        OrdenTrabajo ot = new OrdenTrabajo(nuevoCodigo, cliente, telefono, formato, cantidad, 
                                          mantenimiento, montoEstimado, horasReales, pesoGB, estadoInicial);
        listaOrdenes.add(ot);
    }

    public boolean actualizarOT(String codigoOT, String cliente, String telefono, String formato, 
                                int cantidad, boolean mantenimiento, double horasReales, double pesoGB, String estado) {
        OrdenTrabajo ot = buscarPorCodigo(codigoOT);
        if (ot != null) {
            if (!ot.isEditable()) {
                return false;
            }

            ServicioTarifa tarifa = tarifaService.buscarPorFormato(formato);
            double precioBase = (tarifa != null) ? tarifa.getPrecioBase() : 0.0;
            double costoMantenimiento = mantenimiento ? 15.0 : 0.0;

            ot.setCliente(cliente);
            ot.setTelefono(telefono);
            ot.setFormato(formato);
            ot.setCantidad(cantidad);
            ot.setMantenimiento(mantenimiento);
            ot.setMontoEstimado((precioBase * cantidad) + costoMantenimiento);
            ot.setHorasReales(horasReales);
            ot.setPesoGB(pesoGB);
            ot.setEstado(estado);
            return true;
        }
        return false;
    }

    public boolean procesarCapturaTecnica(String codigoOT, double horasReales, double pesoGB, String medioEntrega) {
        OrdenTrabajo ot = buscarPorCodigo(codigoOT);
        if (ot != null) {
            ot.setHorasReales(horasReales);
            ot.setPesoGB(pesoGB);
            ot.setMedioEntrega(medioEntrega);

            ServicioTarifa tarifa = tarifaService.buscarPorFormato(ot.getFormato());
            double precioBase = (tarifa != null) ? tarifa.getPrecioBase() : 0.0;
            double costoMantenimiento = ot.isMantenimiento() ? 15.0 : 0.0;

            if (tarifa != null && "Video".equalsIgnoreCase(tarifa.getCategoria()) && horasReales > 0) {
                ot.setMontoEstimado((precioBase * horasReales) + costoMantenimiento);
            } else {
                ot.setMontoEstimado((precioBase * ot.getCantidad()) + costoMantenimiento);
            }

            ot.setEstado("Por Notificar");
            return true;
        }
        return false;
    }

    public boolean finalizarOrden(String codigoOT) {
        OrdenTrabajo ot = buscarPorCodigo(codigoOT);
        if (ot != null) {
            ot.setEstado("Finalizado");
            return true;
        }
        return false;
    }

    public boolean cancelarOT(String codigoOT) {
        OrdenTrabajo ot = buscarPorCodigo(codigoOT);
        if (ot != null) {
            ot.setEstado("Cancelado");
            return true;
        }
        return false;
    }

    public void eliminarOT(String codigoOT) {
        listaOrdenes.removeIf(o -> o.getCodigoOT().equalsIgnoreCase(codigoOT));
    }

    public double calcularTotalGeneral() {
        return listaOrdenes.stream()
                .filter(o -> !"Cancelado".equalsIgnoreCase(o.getEstado()))
                .mapToDouble(OrdenTrabajo::getMontoEstimado)
                .sum();
    }
}