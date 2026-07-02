package com.telco.telco_co_app.service;

import com.telco.telco_co_app.model.LoanInfo;

public interface LoanInfoService {
	
	LoanInfo findLoanInfo();
	
	LoanInfo updateLoanInfo(LoanInfo loanInfo);

}
