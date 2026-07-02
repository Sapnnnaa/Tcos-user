package com.telco.telco_co_app.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "voucher")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String vId;
    private String vSln;
    private String vouchertype;
    private double amount;
}