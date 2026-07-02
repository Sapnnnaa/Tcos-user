package com.telco.telco_co_app.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.telco_co_app.model.Company;
import com.telco.telco_co_app.repository.CompanyRepository;
import com.telco.telco_co_app.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {
	
	@Autowired
	private CompanyRepository companyRepository;

	@Override
	public Company findbyCompanyId(int id) {
		// TODO Auto-generated method stub
		return companyRepository.findByCompanyId(id);
	}
	
	@Override
	public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

}
