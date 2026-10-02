package com.example.demo.servicio;

import com.example.demo.modelo.ServicioTarifa;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioTarifaService {

    private final List<ServicioTarifa> tarifario = List.of(
        // Categoria Video (Cobro por Hora o Fracción)
        new ServicioTarifa("ST001", "Video", "VHS", 25.00, "Por Hora o Fracción", "Activo", "vhs.jpg"),
        new ServicioTarifa("ST002", "Video", "Betamax", 35.00, "Por Hora o Fracción", "Activo", "betamax.jpg"),
        new ServicioTarifa("ST003", "Video", "Video 8", 25.00, "Por Hora o Fracción", "Activo", "video8.jpg"),
        new ServicioTarifa("ST004", "Video", "Hi8", 25.00, "Por Hora o Fracción", "Activo", "hi8.jpg"),
        new ServicioTarifa("ST005", "Video", "MiniDV", 30.00, "Por Hora o Fracción", "Activo", "mini-dv.jpg"),

        // Categoria Audio (Cobro por Unidad)
        new ServicioTarifa("ST006", "Audio", "LongPlay (LP)", 20.00, "Por Unidad", "Activo", "vinilo.jpg"),
        new ServicioTarifa("ST007", "Audio", "Disco 45 RPM", 15.00, "Por Unidad", "Activo", "45rpm.jpg"),
        new ServicioTarifa("ST008", "Audio", "Cassette K7", 15.00, "Por Unidad", "Activo", "cassete-k7.jpg"),
        new ServicioTarifa("ST009", "Audio", "Microcassette", 18.00, "Por Unidad", "Activo", "minicassette.jpg"),

        // Categoria Imagen (Cobro por Unidad)
        new ServicioTarifa("ST010", "Imagen", "Fotos Físicas", 2.00, "Por Unidad", "Activo", "fotos.jpg"),
        new ServicioTarifa("ST011", "Imagen", "Slides / Diapositivas", 2.50, "Por Unidad", "Activo", "diapositiva.jpg"),
        new ServicioTarifa("ST012", "Imagen", "Negativos", 3.00, "Por Unidad", "Activo", "negativo.jpg"),
        new ServicioTarifa("ST013", "Imagen", "Positivado", 4.00, "Por Unidad", "Activo", "positivado.jpg")
    );

    public List<ServicioTarifa> obtenerTarifario() {
        return tarifario;
    }

    public ServicioTarifa buscarPorFormato(String formato) {
        return tarifario.stream()
                .filter(t -> t.getFormato().equalsIgnoreCase(formato))
                .findFirst()
                .orElse(null);
    }

    public ServicioTarifa buscarPorCodigo(String codigo) {
        return tarifario.stream()
                .filter(t -> t.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElse(null);
    }
}