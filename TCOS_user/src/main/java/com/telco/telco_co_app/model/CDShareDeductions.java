package com.telco.telco_co_app.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Controller
@Table(name="cd_share_deductions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CDShareDeductions implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String userId;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date paymentDate;
	@NumberFormat(pattern = "#,##0.00")
	private double cdDeduction;
	@NumberFormat(pattern = "#,##0.00")
	private double cdDeductionTotal;
	@NumberFormat(pattern = "#,##0.00")
	private double shareDeduction;
	@NumberFormat(pattern = "#,##0.00")
	private double shareDeductionTotal;
	@NumberFormat(pattern = "#,##0.00")
	private double totalDeduction;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdAt;
	@NumberFormat(pattern = "#,##0.00")
	private double cdInterestTotal;

}
