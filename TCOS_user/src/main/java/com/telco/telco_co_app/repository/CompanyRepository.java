package com.telco.telco_co_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.telco_co_app.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
	
	Company findByCompanyId(int companyId);

}
