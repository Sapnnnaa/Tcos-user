package com.telco.telco_co_app.service;

import java.util.List;

import com.telco.telco_co_app.model.EmiPaymentsDetails;

public interface EmiPaymentsService {
	
	List<EmiPaymentsDetails> findByUserId(String userId);
	
	List<EmiPaymentsDetails> findById(int id);
	
	List<EmiPaymentsDetails> findByLoanId(int loanId);

}
