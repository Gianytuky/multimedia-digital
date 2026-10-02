package com.example.demo.servicio;

import com.example.demo.modelo.ContactoMensaje;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactoMensajeService {

    private final List<ContactoMensaje> mensajes = new ArrayList<>();
    private int contadorMensajes = 1;

    public ContactoMensajeService() {
        // Mensaje 1 de prueba estándar
        registrarMensaje("Roberto Sánchez", "roberto@gmail.com", "987112233", "Deseo cotizar la digitalización de 5 cintas VHS y 2 cassettes.");
        
        // Mensaje 2 de prueba estándar
        registrarMensaje("Mariana Valverde", "mariana.v@outlook.com", "944556677", "¿Tienen servicio de recojo y entrega a domicilio?");

        // Mensaje 3: Tu mensaje extenso para demostración en clase (UTP)
        registrarMensaje(
            "Giancarlo Rivas", 
            "u18210506@utp.edu.pe", 
            "994643120", 
            "Estimado equipo de MultiMedia Digital,\n\n" +
            "Nos comunicamos desde el área de archivo histórico institucional para solicitar un presupuesto formal de digitalización masiva. " +
            "Actualmente contamos con un lote compuesto por aproximadamente 45 cintas VHS en formato NTSC, 12 discos de vinilo de 33 RPM " +
            "y más de 300 diapositivas/slides fotográficas del año 1985 que requieren un proceso prioritario de escaneo en alta resolución.\n\n" +
            "Asimismo, nos gustaría consultar si dentro del servicio incluyen el tratamiento de limpieza o eliminación de hongos en aquellas cintas de video " +
            "que han permanecido almacenadas por largo tiempo, y si el archivo final digitalizado nos puede ser entregado comprimido en un disco duro externo o en la nube.\n\n" +
            "Quedamos a la espera de sus comentarios, disponibilidad de tiempos de entrega y el desglose de los costos por categoría. Muchas gracias."
        );
    }

    public void registrarMensaje(String nombre, String email, String telefono, String mensaje) {
        String codigo = String.format("MSG-%03d", contadorMensajes++);
        mensajes.add(new ContactoMensaje(codigo, nombre, email, telefono, mensaje, LocalDateTime.now()));
    }

    public List<ContactoMensaje> obtenerTodos() {
        return mensajes;
    }

    // Filtrado por rango de fechas usando Java Streams
    public List<ContactoMensaje> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return mensajes.stream()
                .filter(m -> {
                    LocalDate fechaMsg = m.getFechaEnvio().toLocalDate();
                    boolean cumpleInicio = (fechaInicio == null) || !fechaMsg.isBefore(fechaInicio);
                    boolean cumpleFin = (fechaFin == null) || !fechaMsg.isAfter(fechaFin);
                    return cumpleInicio && cumpleFin;
                })
                .collect(Collectors.toList());
    }

    public void eliminarMensaje(String codigo) {
        mensajes.removeIf(m -> m.getCodigo().equalsIgnoreCase(codigo));
    }
}