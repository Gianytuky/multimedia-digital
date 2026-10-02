package com.example.demo.modelo;

import java.time.LocalDateTime;

public class ContactoMensaje {
    private String codigo;
    private String nombre;
    private String email;
    private String telefono;
    private String mensaje;
    private LocalDateTime fechaEnvio; // Nuevo atributo de marca de tiempo

    public ContactoMensaje() {
        this.fechaEnvio = LocalDateTime.now();
    }

    public ContactoMensaje(String codigo, String nombre, String email, String telefono, String mensaje) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.mensaje = mensaje;
        this.fechaEnvio = LocalDateTime.now(); // Asigna automáticamente la fecha/hora actual
    }

    public ContactoMensaje(String codigo, String nombre, String email, String telefono, String mensaje, LocalDateTime fechaEnvio) {
        this(codigo, nombre, email, telefono, mensaje);
        this.fechaEnvio = fechaEnvio;
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}