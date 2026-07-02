package com.telco.telco_co_app.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class NominieesRegistratioinDTO {
	
	private String userId;
	private String nomineeName;
	private String relation;
	private String addr;
	private String aadharNum;
	private String mobileNum;

}
