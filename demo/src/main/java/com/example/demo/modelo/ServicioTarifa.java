package com.example.demo.modelo;

public class ServicioTarifa {
    private String codigo;
    private String categoria;
    private String formato;
    private double precioBase;
    private String unidadCobro;
    private String estado;
    private String imagen;

    public ServicioTarifa() {
    }

    public ServicioTarifa(String codigo, String categoria, String formato, double precioBase, 
                          String unidadCobro, String estado, String imagen) {
        this.codigo = codigo;
        this.categoria = categoria;
        this.formato = formato;
        this.precioBase = precioBase;
        this.unidadCobro = unidadCobro;
        this.estado = estado;
        this.imagen = imagen;
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }

    public String getUnidadCobro() { return unidadCobro; }
    public void setUnidadCobro(String unidadCobro) { this.unidadCobro = unidadCobro; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}