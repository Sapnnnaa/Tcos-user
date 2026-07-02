package com.telco.telco_co_app.model;


import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="banks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banks implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String userId;
    private String accountNum;
    private String bankName;
    private String branchName;
    private String ifscCode;
    private String payableAt;

}
