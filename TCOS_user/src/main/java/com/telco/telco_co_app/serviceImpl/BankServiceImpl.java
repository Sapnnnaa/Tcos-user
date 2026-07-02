package com.telco.telco_co_app.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.telco_co_app.dto.BankRegistrationDTO;
import com.telco.telco_co_app.model.Banks;
import com.telco.telco_co_app.repository.BankRepository;
import com.telco.telco_co_app.service.BankService;

@Service
public class BankServiceImpl implements BankService {
	
	@Autowired
	private BankRepository bankRepository;

	@Override
	public Banks getByUserId(String userId) {
		// TODO Auto-generated method stub
		return bankRepository.findByUserId(userId);
	}

	@Override
	public Banks save(BankRegistrationDTO bank) {
		// TODO Auto-generated method stub
		
		Banks b=new Banks();
		b.setUserId(bank.getUserId());
		b.setAccountNum(bank.getAccountNum());
		b.setBankName(bank.getBankName());
		b.setBranchName(bank.getBranchName());
		b.setIfscCode(bank.getIfscCode());
		b.setPayableAt(bank.getPayableAt());
		
		return bankRepository.save(b);
	}
	
	@Override
	public Banks save(Banks bank) {
		
		return bankRepository.save(bank);
	}

	@Override
	public Banks getById(int id) {
		// TODO Auto-generated method stub
		return bankRepository.findById(id);
	}

}
