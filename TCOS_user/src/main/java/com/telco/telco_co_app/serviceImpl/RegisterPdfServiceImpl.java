package com.telco.telco_co_app.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.telco.telco_co_app.service.RegisterPdfService;

@Service
public class RegisterPdfServiceImpl implements RegisterPdfService {

    @Override
    public void generatePdfFromHtml(String htmlFilePath, String pdfOutputPath, Map<String, Object> userData) throws Exception {
        // Load the HTML content from the file
        String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)));

        // Replace placeholders in the HTML with user data
        for (Map.Entry<String, Object> entry : userData.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            htmlContent = htmlContent.replace(placeholder, value);
        }

        // Set up the renderer
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);

        // Create the output PDF file
        try (FileOutputStream os = new FileOutputStream(new File(pdfOutputPath))) {
            renderer.layout();
            renderer.createPDF(os);
        }
    }

}
