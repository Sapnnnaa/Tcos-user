package com.telco.telco_co_app.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.telco_co_app.model.LoanInfo;
import com.telco.telco_co_app.repository.LoanInfoRepository;
import com.telco.telco_co_app.service.LoanInfoService;

@Service
public class LoanInfoServiceImpl implements LoanInfoService {
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;

	@Override
	public LoanInfo findLoanInfo() {
		// TODO Auto-generated method stub
		return loanInfoRepository.findById(1);
	}

	@Override
	public LoanInfo updateLoanInfo(LoanInfo loanInfo) {
		// TODO Auto-generated method stub
		return loanInfoRepository.save(loanInfo);
	}

}
