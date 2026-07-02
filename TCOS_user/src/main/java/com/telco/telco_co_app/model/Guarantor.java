package com.telco.telco_co_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Entity
@Table(name = "guarantor")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Guarantor {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int guarantorId;
    private String userId;
    private String g_userId;
    private String g_name;
    private int g_value;
    private Date approvedate;
}
