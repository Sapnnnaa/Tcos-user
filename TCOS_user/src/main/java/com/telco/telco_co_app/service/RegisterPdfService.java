package com.telco.telco_co_app.service;

import java.util.Map;

public interface RegisterPdfService {
	//public void generatePdfFromHtml(String htmlFilePath, String pdfOutputPath, String name, String email, String empID) throws Exception;
	public void generatePdfFromHtml(String htmlFilePath, String pdfOutputPath, Map<String, Object> userData) throws Exception;

}
