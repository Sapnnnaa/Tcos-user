package com.telco.telco_co_app.service;

import java.util.List;

import com.telco.telco_co_app.model.Company;

public interface CompanyService {
	
	Company findbyCompanyId(int id);
	
	public List<Company> getAllCompanies();

}
