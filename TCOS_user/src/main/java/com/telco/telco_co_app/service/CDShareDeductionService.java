package com.telco.telco_co_app.service;

import java.util.List;

import com.telco.telco_co_app.model.CDShareDeductions;

public interface CDShareDeductionService {
	
	List<CDShareDeductions> findByUserId(String userId);
	
	CDShareDeductions findById(int id);
	
	int updateCdAmount(String userId,double amount);
	
	CDShareDeductions findLatestRecordByUserId(String userId);


}
