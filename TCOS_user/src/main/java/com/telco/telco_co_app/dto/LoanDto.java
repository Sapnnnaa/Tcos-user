package com.telco.telco_co_app.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.telco.telco_co_app.model.LoanStatus;
import com.telco.telco_co_app.model.LoanType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoanDto {
	
	private String empId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date loanApplyDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date loanApproveDate;
	private double loanAmount;
	private String purposeOfLoan;
	private String ticketNumber;
	private LoanStatus loanStatus;
	private LoanType loanType;
	private int emiBalance;
	private int emiAmount;

}
