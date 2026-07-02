package com.telco.telco_co_app.service;

import java.util.Map;

public interface PdfGeneratorService {
	String generatePdf(String email, Map<String, Object> userData);
}
