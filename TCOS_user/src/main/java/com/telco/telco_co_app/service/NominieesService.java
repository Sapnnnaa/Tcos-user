package com.telco.telco_co_app.service;

import com.telco.telco_co_app.dto.NominieesRegistratioinDTO;
import com.telco.telco_co_app.model.Nominiees;

public interface NominieesService {
	
	Nominiees save(NominieesRegistratioinDTO nominiee);
	Nominiees findByUserId(String userId);
	Nominiees findById(int id);
	Nominiees save(Nominiees nominiee);

}
