package com.telco.telco_co_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.telco_co_app.dto.LoanDto;
import com.telco.telco_co_app.model.Loan;
import com.telco.telco_co_app.model.LoanStatus;
import com.telco.telco_co_app.model.LoanType;

public interface LoanRespository extends JpaRepository<Loan, Integer> {
		
	List<Loan> findByUserId(String userId);
	
	List<Loan> findById(int id);
	
	Loan save(LoanDto loanDto);
	
	List<Loan> findAll();
	
	List<Loan> findByLoanType(LoanType loanType);
	
	List<Loan> findByUserIdAndLoanType(String userId,LoanType loanType);
		
	Loan findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(String userId, LoanType loanType);
	
	@Query(value = "SELECT COUNT(*) FROM loans l WHERE l.user_id = :userId AND l.loan_type = :loanType AND "
			+ "(l.loan_status = 0 OR l.loan_status=1 OR l.loan_status=2)", nativeQuery = true)
	int countPendingLoans(@Param("userId") String userId, @Param("loanType") int loanType);
	
	Loan findTop1ByUserIdAndLoanTypeOrderByLoanApplyDateDesc(String userId, LoanType loanType);
	
	List<Loan> findByUserIdAndLoanTypeAndLoanBalanceGreaterThanOrderByLoanApproveDateDesc(String userId,LoanType loanType,double amt);
	
	Loan findTop1ByUserIdAndLoanTypeAndLoanStatusOrderByLoanApproveDateDesc(String userId,LoanType loanType,LoanStatus loanStatus);

}
