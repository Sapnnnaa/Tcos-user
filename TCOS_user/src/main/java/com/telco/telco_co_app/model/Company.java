package com.telco.telco_co_app.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "company")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;
	
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    private int companyId;
    private String companyName;
}
