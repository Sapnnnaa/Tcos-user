package com.telco.telco_co_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.telco_co_app.model.Banks;

public interface BankRepository extends JpaRepository<Banks, Integer> {
	
	Banks findByUserId(String userId);
	
	Banks findById(int id);

}
