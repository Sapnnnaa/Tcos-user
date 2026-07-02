package com.telco.telco_co_app.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.telco_co_app.model.EmiPaymentsDetails;
import com.telco.telco_co_app.repository.EmiPaymentsRepository;
import com.telco.telco_co_app.service.EmiPaymentsService;

@Service
public class EmiPaymentsServiceImpl implements EmiPaymentsService {
	
	@Autowired
	private EmiPaymentsRepository emiPaymentsRepository;

	@Override
	public List<EmiPaymentsDetails> findByUserId(String userId) {
		// TODO Auto-generated method stub
		
		return emiPaymentsRepository.findByUserId(userId);
	}

	@Override
	public List<EmiPaymentsDetails> findById(int id) {
		// TODO Auto-generated method stub
		return emiPaymentsRepository.findById(id);
	}

	@Override
	public List<EmiPaymentsDetails> findByLoanId(int loanId) {
		// TODO Auto-generated method stub
		return emiPaymentsRepository.findByLoanId(loanId);
	}
	
	

}
