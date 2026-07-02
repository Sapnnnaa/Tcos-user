package com.telco.telco_co_app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.SessionAttributes;

@Configuration
@SessionAttributes("user")
@EnableWebSecurity
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(requests -> requests
				.requestMatchers("/dist/**", "/pdf/**", "/telco_co/register","/telco_co/registerOld", "/login", "/user-email/**", "/user-adrpan",
						"/pdf-view/**","/telco_co/offlineform","/ticketnum/**","/confirm-otp","/sendOtp","/forgotpassword"
						,"/checkTicketNumber","/resetpasswordotpsend","/checkresetpasswordotp","/resetpasswordotp","/user-data/**")
				.permitAll() // Ensure login is allowed for all
				.anyRequest().authenticated()) 
				.formLogin(login -> login.loginPage("/userlogin") // The login page URL
						.loginProcessingUrl("/userlogin") 
						.usernameParameter("userId").defaultSuccessUrl("/user/profile", true) 
						.failureUrl("/userlogin?error=true") 
						.permitAll()) // Allow everyone to access the login page
				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL to trigger
																									// logout
						.invalidateHttpSession(true) 
						.clearAuthentication(true) 
						.deleteCookies("JSESSIONID") // Delete session cookie
						.logoutSuccessUrl("/userlogin?logout") 
						.permitAll()); 

		return http.build();
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
