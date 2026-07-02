package com.telco.telco_co_app.service;

import java.util.List;

import com.telco.telco_co_app.model.WageTpye;

public interface WageTypeService {
	
	WageTpye findByPaymentId(int id);
	
	List<WageTpye> findAll();

}
