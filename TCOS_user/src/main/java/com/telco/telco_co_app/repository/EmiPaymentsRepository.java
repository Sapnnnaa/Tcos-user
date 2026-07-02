package com.telco.telco_co_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.telco_co_app.model.EmiPaymentsDetails;

public interface EmiPaymentsRepository extends JpaRepository<EmiPaymentsDetails ,Integer> {
	
	List<EmiPaymentsDetails> findByUserId(String userId);
	List<EmiPaymentsDetails> findById(int id);
	List<EmiPaymentsDetails> findByLoanId(int loanId);

}
