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
@Table(name="re_Advice")
public class re_Advice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String userId;
    private double amount;
    private int adviceId;
    private Paymenttype paymentType;
    private String chequeNo;
    private String paymentBank;
    private Date adviceDate;
    private Date adviceDate2;
    private int adviceTimes;
}
