package com.telco.telco_co_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.telco_co_app.model.Nominiees;

public interface NominieesRepository extends JpaRepository<Nominiees, Integer> {
	
	Nominiees findByUserId(String userId);
	
	Nominiees findById(int id);
	
}
