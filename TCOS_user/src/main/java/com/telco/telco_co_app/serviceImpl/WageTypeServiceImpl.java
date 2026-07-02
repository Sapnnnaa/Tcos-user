package com.telco.telco_co_app.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.telco_co_app.model.WageTpye;
import com.telco.telco_co_app.repository.WageTypeRepository;
import com.telco.telco_co_app.service.WageTypeService;

@Service
public class WageTypeServiceImpl implements WageTypeService {
	
	@Autowired
	private WageTypeRepository wageTypeRepository;

	@Override
	public WageTpye findByPaymentId(int id) {
		// TODO Auto-generated method stub
		return wageTypeRepository.findByPaymentId(id);
	}

	@Override
	public List<WageTpye> findAll() {
		// TODO Auto-generated method stub
		return wageTypeRepository.findAll();
	}

}
