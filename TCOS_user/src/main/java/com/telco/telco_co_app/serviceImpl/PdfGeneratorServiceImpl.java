package com.telco.telco_co_app.serviceImpl;

import com.telco.telco_co_app.service.PdfGeneratorService;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Override
    public String generatePdf(String email, Map<String, Object> userData) {
        try {
            // Path to the HTML template
            Path htmlTemplatePath = Paths.get("src/main/resources/templates/registrationpdf.html");

            // Read the HTML template content as a String
            String htmlContent = Files.readString(htmlTemplatePath);

            // Replace placeholders in the HTML template with actual user data
            for (Map.Entry<String, Object> entry : userData.entrySet()) {
                htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }

            // Output PDF path
            String pdfOutputPath = "src/main/resources/generated_pdfs/" + email + "_registration.pdf";

            // Convert the updated HTML content to PDF using Flying Saucer
            try (OutputStream outputStream = new FileOutputStream(pdfOutputPath)) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(outputStream);
            }

            return pdfOutputPath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
