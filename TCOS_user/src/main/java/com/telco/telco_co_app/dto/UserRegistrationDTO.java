package com.telco.telco_co_app.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.telco.telco_co_app.model.AccountSettlement;
import com.telco.telco_co_app.model.AccountStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserRegistrationDTO {

	private String empId;
	private String userId;
	private String name;
	private String bcNumber;
	private String gender;
	private String fathersName;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dob;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOfRetirement;
	private String aadharNumber;
	private String panCard;
	@Column(unique = true, nullable = false)
	private String email;
	private long phoneNum;
	private long whatsappNumber;
	private String serivcePeriod;
	// permanent address
	private String permanentAddr;
	private String permanentState;
	private String permanentPinCode;
	private String permanentCity;
	// current address
	private String currentAddr;
	private String currentState;
	private String currentPinCode;
	private String currentCity;
	
	private String joiningReason;
	private AccountSettlement accountSettlement;
	private AccountStatus accountStatus;
	
	private int companyId;
	@Column(nullable = false)
	private String password;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String createdAt;
	

}
