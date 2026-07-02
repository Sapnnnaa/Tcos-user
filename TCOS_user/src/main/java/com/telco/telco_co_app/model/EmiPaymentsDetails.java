package com.telco.telco_co_app.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "emi_payments_details")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmiPaymentsDetails {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;
	private int loanId;
	private String userId;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date paymentDate;
	@NumberFormat(pattern = "#,##0.00")
	private double emiAmount;
	@NumberFormat(pattern = "#,##0.00")
	private double principalRemaing;
	@NumberFormat(pattern = "#,##0.00")
	private double intrestRate;
	@NumberFormat(pattern = "#,##0.00")
	private double totalPaid;
	private String reciptNum;
	private String emiMonth;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdAt;
	private EmiPaymentType emiPaymentType;

}
