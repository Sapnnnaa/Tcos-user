package com.telco.telco_co_app.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.telco_co_app.dto.NominieesRegistratioinDTO;
import com.telco.telco_co_app.model.Nominiees;
import com.telco.telco_co_app.repository.NominieesRepository;
import com.telco.telco_co_app.service.NominieesService;

@Service
public class NominieesServiceImpl implements NominieesService {
	
	@Autowired
	private NominieesRepository nominieesRepository;

	@Override
	public Nominiees findByUserId(String userId) {
		// TODO Auto-generated method stub
		return nominieesRepository.findByUserId(userId);
	}

	@Override
	public Nominiees save(NominieesRegistratioinDTO nominiee) {
		// TODO Auto-generated method stub
		Nominiees n=new Nominiees();
		n.setUserId(nominiee.getUserId());
		n.setAadharNum(nominiee.getAadharNum());
		n.setAddr(nominiee.getAddr());
		n.setNomineeName(nominiee.getNomineeName());
		n.setRelation(nominiee.getRelation());
		n.setMobileNum(nominiee.getMobileNum());
		
		return nominieesRepository.save(n);
	}

	@Override
	public Nominiees findById(int id) {
		// TODO Auto-generated method stub
		return nominieesRepository.findById(id);
	}

	@Override
	public Nominiees save(Nominiees nominiee) {
		// TODO Auto-generated method stub
		return nominieesRepository.save(nominiee);
	}

}
