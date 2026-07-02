package com.telco.telco_co_app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.telco.telco_co_app.model.EmiPaymentsDetails;
import com.telco.telco_co_app.service.EmiPaymentsService;

@Controller
@SessionAttributes("user")
@RequestMapping("/user/history")
public class SpecificLoanController {
	
	@Autowired
	private EmiPaymentsService emiPaymentsService;
	
	@GetMapping("/peremi/{loanId}")
	public String perOneLoan(@PathVariable("loanId") int loanId,Model model) {
		model.addAttribute("activePage","EMI for loan ID "+loanId);
        List<EmiPaymentsDetails> lst=emiPaymentsService.findByLoanId(loanId);
        
        model.addAttribute("allemi", lst);
		
		return "specific_loan";
	}
	
	
	@GetMapping("/comemi/{loanId}")
	public String comOneLoan(@PathVariable("loanId") int loanId,Model model) {
		model.addAttribute("activePage","EMI for loan ID "+loanId);
        List<EmiPaymentsDetails> lst=emiPaymentsService.findByLoanId(loanId);
        
        model.addAttribute("allemi", lst);
		
		return "specific_loan";
	}
	

}
