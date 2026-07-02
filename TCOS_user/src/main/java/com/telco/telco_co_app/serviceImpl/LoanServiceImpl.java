package com.telco.telco_co_app.serviceImpl;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.telco.telco_co_app.model.Loan;
import com.telco.telco_co_app.model.LoanStatus;
import com.telco.telco_co_app.model.LoanType;
import com.telco.telco_co_app.repository.LoanRespository;
import com.telco.telco_co_app.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

	private final LoanRespository loanRespository;

	public LoanServiceImpl(LoanRespository loanRespository) {
		this.loanRespository = loanRespository;
	}

	@Override
	public Loan save(Loan loan) {
		// TODO Auto-generated method stub
		return loanRespository.save(loan);
	}

	@Override
	public List<Loan> findByUserId(String userId) {
		// TODO Auto-generated method stub
		return loanRespository.findByUserId(userId);
	}

	@Override
	public List<Loan> finalAll() {
		// TODO Auto-generated method stub
		return loanRespository.findAll();
	}

	@Override
	public List<Loan> findByLoanType(LoanType loanType) {
		// TODO Auto-generated method stub
		return loanRespository.findByLoanType(loanType);
	}

	@Override
	public List<Loan> findByUserIdAndLoanType(String userId, LoanType loanType) {
		// TODO Auto-generated method stub
		return loanRespository.findByUserIdAndLoanType(userId, loanType);
	}

	@Override
	public Date findByLastLoanApplyDateByUserId(String userId, LoanType loanType) {
		// TODO Auto-generated method stub
		Loan record = loanRespository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(userId, loanType);
		return record.getLoanApplyDate();
	}

	@Override
	public Double findByLastLoanOutStandingByUserId(String userId, LoanType loanType) {
		// TODO Auto-generated method stub
		Loan record;
		double amt;
		try {
			record = loanRespository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(userId, loanType);
			amt = record.getLoanBalance();
		} catch (Exception e) {
//			System.out.println("no loans found");
			amt = 0;
		}
		return amt;
	}

	@Override
	public Double findByOutStandingLoan(String userId, LoanType loanType) {
		// TODO Auto-generated method stub

		Double osLoan = 0.0;
		List<Loan> lst = loanRespository
				.findByUserIdAndLoanTypeAndLoanBalanceGreaterThanOrderByLoanApproveDateDesc(userId, loanType, 0);
		if (lst == null)
			return 0.0;

		try {
			osLoan += lst.get(0).getLoanBalance();
		} catch (Exception e) {
			// TODO: handle exception
			osLoan=0.0;
		}
		

		return osLoan;
	}

	@Override
	public Date findByLastLoanApplyDateByUserIdForStatus(String userId, LoanType loanType) {
		// TODO Auto-generated method stub

		Loan record = loanRespository.findTop1ByUserIdAndLoanTypeOrderByLoanApplyDateDesc(userId, loanType);
		return record.getLoanApplyDate();
	}

	@Override
	public LoanStatus findLastLoanStatus(String userId, LoanType loanType) {
		// TODO Auto-generated method stub
		int ls = 0;
		if (loanType == LoanType.PERSONAL) {
			ls = loanRespository.countPendingLoans(userId, 0);
		} else {
			ls = loanRespository.countPendingLoans(userId, 1);
		}

		if (ls != 0)
			return LoanStatus.PENDING;
		else
			return LoanStatus.APPROVED;
	}

	@Override
	public List<Loan> findById(int id) {
		// TODO Auto-generated method stub
		return loanRespository.findById(id);
	}

	@Override
	public Loan findByLastLoanTypeAndStatus(String userId, LoanType loanType, LoanStatus loanStatus) {
		// TODO Auto-generated method stub
		Loan l = loanRespository.findTop1ByUserIdAndLoanTypeAndLoanStatusOrderByLoanApproveDateDesc(userId, loanType,
				loanStatus);
		return l;
	}

	@Override
	public Loan findLastOsLoan(String userId, LoanType loanType) {
		// TODO Auto-generated method stub

		List<Loan> loans = loanRespository
				.findByUserIdAndLoanTypeAndLoanBalanceGreaterThanOrderByLoanApproveDateDesc(userId, loanType, 0);

		return loans.get(0);
	}

	@Override
	public Loan findLastApproveLoan(String userId, LoanType loanType) {
		// TODO Auto-generated method stub
		return loanRespository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(userId, loanType);
	}

	@Override
	public int findInstalment(Date retirementDate, Date todaysDate) {
	    int maxInstalments = 50;
	    
	    // Add null check and debug logging
	    if (retirementDate == null) {
	        System.err.println("Retirement date is null");
	        return maxInstalments;
	    }
	    
	    if (todaysDate.compareTo(retirementDate) >= 0) {
	        System.err.println("Retirement date is in the past or today");
	        return maxInstalments;
	    }
	    
	    try {
	        LocalDate retirementLocalDate;
	        LocalDate todaysLocalDate;
	        
	        // Handle java.sql.Date (from @Temporal annotation)
	        if (retirementDate instanceof java.sql.Date) {
	            retirementLocalDate = ((java.sql.Date) retirementDate).toLocalDate();
	        } else {
	            // Handle java.util.Date
	            retirementLocalDate = retirementDate.toInstant()
	                .atZone(ZoneId.systemDefault())
	                .toLocalDate();
	        }
	        
	        if (todaysDate instanceof java.sql.Date) {
	            todaysLocalDate = ((java.sql.Date) todaysDate).toLocalDate();
	        } else {
	            todaysLocalDate = todaysDate.toInstant()
	                .atZone(ZoneId.systemDefault())
	                .toLocalDate();
	        }
	        
	        // Calculate months until retirement
	        long monthsUntilRetirement = ChronoUnit.MONTHS.between(todaysLocalDate, retirementLocalDate);
	        
	        // Ensure positive result and within limits
	        int availableInstalments = (int) Math.min(Math.max(monthsUntilRetirement, 1), maxInstalments);
	        
//	        System.out.println("Months until retirement: " + monthsUntilRetirement);
//	        System.out.println("Available instalments: " + availableInstalments);
	        
	        return availableInstalments;
	        
	    } catch (Exception e) {
	        System.err.println("Error calculating instalments: " + e.getMessage());
	        e.printStackTrace();
	        return maxInstalments;
	    }
	}

	@Override
	public Date findByLastLoanApproveDateByUserId(String userId, LoanType loanType) {
		
		Loan record = loanRespository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(userId, loanType);
		return record.getLoanApproveDate();
		
	}

}
