package com.telco.telco_co_app;

import java.security.Principal;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class Common {
	
	@ModelAttribute
	public void sharedData(Model model,Principal principal) {
		if(principal!=null) {
			model.addAttribute("principal", principal.getName());
		}
	}
}
