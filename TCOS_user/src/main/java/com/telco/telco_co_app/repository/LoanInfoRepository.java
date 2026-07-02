package com.telco.telco_co_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.telco_co_app.model.LoanInfo;

public interface LoanInfoRepository extends JpaRepository<LoanInfo, Integer> {
	
	LoanInfo findById(int id);
	
}
