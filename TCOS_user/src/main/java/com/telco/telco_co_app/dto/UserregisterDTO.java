package com.telco.telco_co_app.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.telco.telco_co_app.model.AccountStatus;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Data
@Setter
public class UserregisterDTO {
        private int id;
        private String empId;
        private String userId;
        private String bcNumber;
        private String gender;
        private String fathersName;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date dob;
        private String panCard;
        private String aadharNumber;
        private long whatsappNumber;
        private int companyId;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date createdAt;
        private String serivcePeriod;
        private String permanentAddr;
        private String permanentCity;
        private String permanentState;
        private String permanentPinCode;
        private String currentAddr;
        private String currentCity;
        private String currentState;
        private String currentPinCode;
        private String nomineeName;
        private String nomineeAddress;
        private String relation;
        private String aadharNum;
        private String mobileNum;
        private String accountNum;
        private String bname;
        private String branch;
        private String ifscode;
        private String payableAt;
        private String email;
        private String name;
        private String password;
        private String userType;
        private long phoneNum;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date approvedate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date closeddate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date dor;
        private AccountStatus accountStatus;
    }
