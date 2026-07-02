package com.telco.telco_co_app.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="member_voucher")
public class MemberVoucher {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	private String vId;
	private String name;
	private String userId;
	private String category;
	private String accountNum;
	private String bankName;
	private String branchName;
	private String ifscCode;
	private String payableAt;
	private double totalAmount;
	private Paymenttype paymenttype;
	private String paymentBank;
	private String paymentAccountNum;
	private String chequeNo;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updatedDate;
	private String preparedBy;
	private String checkedBy;
	private String passedBy;
	private int voucherApproved;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date voucherApprovedDate;
	private int voucherDownloaded;
	private String description;
	private int generatedTimes;
	private int regenerated;
}
