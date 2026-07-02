package com.telco.telco_co_app.service;

import com.telco.telco_co_app.dto.BankRegistrationDTO;
import com.telco.telco_co_app.model.Banks;

public interface BankService {
	
	Banks save(BankRegistrationDTO bank);
	Banks getByUserId(String userId);
	Banks getById(int string);
	Banks save(Banks bank);

}
