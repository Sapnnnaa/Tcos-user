package com.telco.telco_co_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@Table(name = "users") // Ensure this maps to the correct table
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(unique = true)
	private String userId;// this is ticket number
	private String memberId;
	@Column(name = "emp_id")
	private String empId;
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
	@Column(unique = true)
	private String aadharNumber;
	@Column(unique = true)
	private String panCard;
	@Column(unique = true, nullable = true)
	private String email;
	private long phoneNum;
	private long whatsappNumber;
	private String serivcePeriod;
	private String permanentAddr;
	private String permanentCity;
	private String permanentState;
	private String permanentPinCode;
	private String currentAddr;
	private String currentCity;
	private String currentState;
	private String currentPinCode;
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "account_status")
	private AccountStatus accountStatus;
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "account_settlement")
	private AccountSettlement accountSettlement;
	@Column(name = "company_id")
	private int companyId;
	@Column(nullable = false)
	private String password;
	@Transient
	private String confirmPassword;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdAt;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date approvedAt;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date ClosedAt;
	private String utr;
	private String userType; // Existing or New Member
	private int PaymentMode;
	private double cdCut;
	private double shareCut;
	private double personalLoanEmi;
	private double commodityLoanEmi;
	private double personalLoanInterest;
	private double commodityLoanInterest;

}
