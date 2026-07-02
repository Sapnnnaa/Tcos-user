package com.telco.telco_co_app.model;

import org.springframework.stereotype.Controller;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Controller
@Table(name="nominiees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nominiees {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(unique = true)
	private String userId;
	private String nomineeName;
	private String relation;
	private String addr;
	private String aadharNum;
	private String mobileNum;

}
