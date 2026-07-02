package com.telco.telco_co_app.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BankRegistrationDTO {
	
	private String userId;
	private String accountNum;
	private String bankName;
	private String branchName;
	private String ifscCode;
	private String payableAt;

}
