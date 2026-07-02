package com.telco.telco_co_app.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="members")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Members {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name="first_name")
	private String firstName;
	@Column(name="last_name")
	private String lastName;
	@Column(unique = true, nullable = false)
	private String mid;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(name="password", nullable = false)
	private String password;
	@Transient
	private String confirmPassword;
}
