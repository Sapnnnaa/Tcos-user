package com.telco.telco_co_app.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="loan_voucher")
public class LoanVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String userId;
    private double amount;
    private int adviceId;
    private String chequeNo;
    private Paymenttype paymentType;
    private String paymentBank;
    private String accountNumber;
    private Date adviceDate;
    private int adviceDownloaded;
    private int adviceApproved;
    private Date adviceApprovedDate;
}
