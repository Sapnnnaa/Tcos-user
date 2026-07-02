package com.telco.telco_co_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PendingController {
	
	@GetMapping("/pendinguser")
	public String accountPending(Model model) {
		
		
		
		return "account_pending";
	}

}
