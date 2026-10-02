package com.example.demo.modelo;

import java.time.LocalDateTime;

public class OrdenTrabajo { 
    private String codigoOT; 
    private String cliente; 
    private String telefono; 
    private String formato; 
    private int cantidad; 
    private boolean mantenimiento; 
    private double montoEstimado; 
    private double horasReales; 
    private double pesoGB; 
    private String medioEntrega = "Memoria USB (32 GB)"; 
    private String estado; // "En Recepción", "Por Notificar", "Finalizado", "Cancelado"
    private LocalDateTime fechaRegistro; // <--- NUEVO ATRIBUTO DE FECHA/HORA DE RECEPCIÓN

    public OrdenTrabajo() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public OrdenTrabajo(String codigoOT, String cliente, String telefono, String formato, int cantidad, 
                        boolean mantenimiento, double montoEstimado, double horasReales, double pesoGB, String estado) {
        this.codigoOT = codigoOT;
        this.cliente = cliente;
        this.telefono = telefono;
        this.formato = formato;
        this.cantidad = cantidad;
        this.mantenimiento = mantenimiento;
        this.montoEstimado = montoEstimado;
        this.horasReales = horasReales;
        this.pesoGB = pesoGB;
        this.estado = estado;
        this.medioEntrega = "Memoria USB (32 GB)";
        this.fechaRegistro = LocalDateTime.now(); // Asigna fecha y hora actual automáticamente
    }

    public OrdenTrabajo(String codigoOT, String cliente, String telefono, String formato, int cantidad, 
                        boolean mantenimiento, double montoEstimado, double horasReales, double pesoGB, 
                        String medioEntrega, String estado, LocalDateTime fechaRegistro) {
        this(codigoOT, cliente, telefono, formato, cantidad, mantenimiento, montoEstimado, horasReales, pesoGB, estado);
        this.medioEntrega = medioEntrega;
        this.fechaRegistro = (fechaRegistro != null) ? fechaRegistro : LocalDateTime.now();
    }

    // Regla de seguridad RF-06: editable solo si está En Recepción
    public boolean isEditable() {
        return "En Recepción".equalsIgnoreCase(this.estado);
    }

    // Getters y Setters
    public String getCodigoOT() { return codigoOT; }
    public void setCodigoOT(String codigoOT) { this.codigoOT = codigoOT; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public boolean isMantenimiento() { return mantenimiento; }
    public void setMantenimiento(boolean mantenimiento) { this.mantenimiento = mantenimiento; }

    public double getMontoEstimado() { return montoEstimado; }
    public void setMontoEstimado(double montoEstimado) { this.montoEstimado = montoEstimado; }

    public double getHorasReales() { return horasReales; }
    public void setHorasReales(double horasReales) { this.horasReales = horasReales; }

    public double getPesoGB() { return pesoGB; }
    public void setPesoGB(double pesoGB) { this.pesoGB = pesoGB; }

    public String getMedioEntrega() { return medioEntrega; }
    public void setMedioEntrega(String medioEntrega) { this.medioEntrega = medioEntrega; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}