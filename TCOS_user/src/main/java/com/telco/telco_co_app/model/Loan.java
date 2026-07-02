package com.telco.telco_co_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "loans")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int loanId;
	private String userId;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date loanApplyDate;
	@NumberFormat(pattern = "#,##0.00")
	private double loanAmount;
	@NumberFormat(pattern = "#,##0.00")
	private double intrestRate;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;
	@NumberFormat(pattern = "#,##0.00")
	private double loanRepaid;
	@NumberFormat(pattern = "#,##0.00")
	private double loanBalance;
	private LoanStatus loanStatus;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date loanApproveDate;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updatedAt;
	private LoanType loanType;
	private ApplicationType applicationType;
	private int emiBalance;
	private int emiPaid;
	@NumberFormat(pattern = "#,##0.00")
	private double instalmentAmount;
	
	@NumberFormat(pattern = "#,##0.00")
	private double emiAmount;
	@NumberFormat(pattern = "#,##0.00")
	private double appliedLoan;
	private String loanReason;
	private double interestDiff;
	private String description;
	
}
