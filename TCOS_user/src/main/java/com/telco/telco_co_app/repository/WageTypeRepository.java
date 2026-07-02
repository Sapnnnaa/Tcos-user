package com.telco.telco_co_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.telco_co_app.model.WageTpye;

public interface WageTypeRepository extends JpaRepository<WageTpye, Integer> {
	
	WageTpye findByPaymentId(int paymentId);
	List<WageTpye> findAll();

}
