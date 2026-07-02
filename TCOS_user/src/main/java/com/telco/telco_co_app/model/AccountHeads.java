package com.telco.telco_co_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name="AccountHead")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountHeads {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String slNo;
    private String accountHead;
}
