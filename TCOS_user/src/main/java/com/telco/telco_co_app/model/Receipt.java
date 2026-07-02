package com.telco.telco_co_app.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "receipt")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String vId;
    private String vSln;
    private String vouchertype;
    private double amount;
}
