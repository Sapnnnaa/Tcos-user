package com.telco.telco_co_app.model;

import java.io.Serializable;

import org.springframework.format.annotation.NumberFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatically generated primary key
    private int id;
	@NumberFormat(pattern = "#,##0.00")
    private double shareAmount;
	@NumberFormat(pattern = "#,##0.00")
    private double cdMinimum;
	@NumberFormat(pattern = "#,##0.00")
    private double interestOnNormal;
	@NumberFormat(pattern = "#,##0.00")
    private double interestOnCommodity;
	@NumberFormat(pattern = "#,##0.00")
    private double interestOnCD;
	@NumberFormat(pattern = "#,##0.00")
    private double maxCommodityLoan;
}