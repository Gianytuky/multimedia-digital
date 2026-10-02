package com.example.demo.servicio;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generarPdfDesdeThymeleaf(String nombrePlantilla, Map<String, Object> datos) {
        Context context = new Context();
        context.setVariables(datos);

        // 1. Renderiza el HTML con la data de Thymeleaf
        String htmlContent = templateEngine.process(nombrePlantilla, context);

        // 2. Convierte el HTML renderizado a PDF mediante Flying Saucer
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el documento PDF: " + e.getMessage());
        }

        return outputStream.toByteArray();
    }
}