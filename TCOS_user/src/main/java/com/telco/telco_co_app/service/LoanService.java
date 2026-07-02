package com.telco.telco_co_app.service;

import java.util.Date;
import java.util.List;

import com.telco.telco_co_app.model.Loan;
import com.telco.telco_co_app.model.LoanStatus;
import com.telco.telco_co_app.model.LoanType;

public interface LoanService {
	
	Loan save(Loan loan);
	List<Loan> findByUserId(String userId);
	List<Loan> findById(int id);
	List<Loan> finalAll();
	List<Loan> findByLoanType(LoanType loanType);
	List<Loan> findByUserIdAndLoanType(String id,LoanType loanType);
	Date findByLastLoanApplyDateByUserId(String userId, LoanType loanType);
	Date findByLastLoanApplyDateByUserIdForStatus(String userId, LoanType loanType);
	Double findByLastLoanOutStandingByUserId(String userId, LoanType loanType);
	Double findByOutStandingLoan(String userId,LoanType loanType);
	LoanStatus findLastLoanStatus(String userId,LoanType loanType);
	Loan findByLastLoanTypeAndStatus(String userId,LoanType loanType,LoanStatus loanStatus);
	Loan findLastOsLoan(String userId,LoanType loanType);
	Loan findLastApproveLoan(String userId,LoanType loanType);
	int findInstalment(Date retirementDate, Date todaysDate);
	Date findByLastLoanApproveDateByUserId(String userId, LoanType loanType);

}
