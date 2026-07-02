package com.telco.telco_co_app.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.telco.telco_co_app.model.AccountSettlement;
import com.telco.telco_co_app.model.AccountStatus;
import com.telco.telco_co_app.model.Banks;
import com.telco.telco_co_app.model.CDShareDeductions;
import com.telco.telco_co_app.model.Company;
import com.telco.telco_co_app.model.EmiPaymentsDetails;
import com.telco.telco_co_app.model.Loan;
import com.telco.telco_co_app.model.LoanInfo;
import com.telco.telco_co_app.model.LoanType;
import com.telco.telco_co_app.model.Nominiees;
import com.telco.telco_co_app.model.User;
import com.telco.telco_co_app.repository.UserRepository;
import com.telco.telco_co_app.repository.LoanRespository;
import com.telco.telco_co_app.service.BankService;
import com.telco.telco_co_app.service.CDShareDeductionService;
import com.telco.telco_co_app.service.CompanyService;
import com.telco.telco_co_app.service.EmiPaymentsService;
import com.telco.telco_co_app.service.LoanInfoService;
import com.telco.telco_co_app.service.LoanService;
import com.telco.telco_co_app.service.NominieesService;
import com.telco.telco_co_app.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

@Controller
@RequestMapping("/user")
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private LoanService loanService;

	@Autowired
	private EmiPaymentsService emiPaymentsService;

	@Autowired
	private CDShareDeductionService cdShareDeductionService;

	@Autowired
	private BankService bankService;

	@Autowired
	private NominieesService nominieesService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private LoanInfoService loanInfoService;

	@Autowired
	private LoanRespository loanRepository;

	// Helper method to get current authenticated user with request-level caching
	private User getCurrentUser(HttpServletRequest request, HttpSession session) {
	    // Check session first
	    User sessionUser = (User) session.getAttribute("user");
	    if (sessionUser != null) {
	        return sessionUser;
	    }
	    
	    // Then check request cache
	    User cachedUser = (User) request.getAttribute("currentUser");
	    if (cachedUser != null) {
	        return cachedUser;
	    }
	    
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication == null || !authentication.isAuthenticated()) {
	        throw new RuntimeException("User not authenticated");
	    }
	    
	    String uid = authentication.getName();
	    User user = userService.findByUserId(uid);
	    
	    if (user == null) {
	        throw new RuntimeException("User not found");
	    }
	    
	    // Cache in both request and session
	    request.setAttribute("currentUser", user);
	    session.setAttribute("user", user);
	    
	    return user;
	}

	// Helper method to check account status
	private boolean checkAccountStatus(AccountStatus status) {
		return status == AccountStatus.ACTIVE;
	}

	// Helper method to update session user data
	private void updateSessionUser(HttpSession session, User user) {
		session.setAttribute("user", user);
	}

	// Profile page
	@GetMapping("/profile")
	@Transactional(readOnly = true)
	public String userProfile(Model model, HttpSession session, HttpServletRequest request) {
		// Get cached user (loaded once by interceptor)
		User currentUser = getCurrentUser(request,session);

		// Fetch related data
		CDShareDeductions cdShareDeductions = cdShareDeductionService.findLatestRecordByUserId(currentUser.getUserId());
		if (cdShareDeductions == null) {
			cdShareDeductions = new CDShareDeductions();
		}

		Banks bank = bankService.getByUserId(currentUser.getUserId());
		if (bank == null) {
			bank = new Banks();
		}

		Nominiees nominiees = nominieesService.findByUserId(currentUser.getUserId());
		if (nominiees == null) {
			nominiees = new Nominiees();
		}

		Company company = companyService.findbyCompanyId(currentUser.getCompanyId());
		if (company == null) {
			company = new Company();
		}

		LoanInfo loanInfo = loanInfoService.findLoanInfo();

		// Account status check
		boolean allAccess = checkAccountStatus(currentUser.getAccountStatus());

		// Add to model
		model.addAttribute("user", currentUser);
		model.addAttribute("cdshare", cdShareDeductions);
		model.addAttribute("bank", bank);
		model.addAttribute("nominiees", nominiees);
		model.addAttribute("company", company);
		model.addAttribute("loanInfo", loanInfo);
		model.addAttribute("activePage", "Profile");

		// Update session with latest user data
		updateSessionUser(session, currentUser);

		return allAccess ? "profile" : "account_pending";
	}

	@GetMapping("/history")
	public String loanHistory(Model model, HttpSession session, HttpServletRequest request) {
		User currentUser = getCurrentUser(request,session);
		boolean allAccess = checkAccountStatus(currentUser.getAccountStatus());

		List<Loan> loans = loanService.findByUserId(currentUser.getUserId());
		model.addAttribute("loanlst", loans);
		model.addAttribute("activePage", "Loan History");

		// Update session
		updateSessionUser(session, currentUser);

		return allAccess ? "loan_history" : "account_pending";
	}

	@GetMapping("/history/personalloan")
	public String personalLoanHistory(Model model, HttpSession session, HttpServletRequest request) {
		User currentUser = getCurrentUser(request,session);
		boolean allAccess = checkAccountStatus(currentUser.getAccountStatus());

		List<Loan> personalLoan = loanService.findByUserIdAndLoanType(currentUser.getUserId(), LoanType.PERSONAL);
		model.addAttribute("personalloan", personalLoan);
		model.addAttribute("activePage", "Personal Loan History");

		// Update session
		updateSessionUser(session, currentUser);

		return allAccess ? "personal_loan_history" : "account_pending";
	}

	@GetMapping("/history/commodityloan")
	public String commodityLoanHistory(Model model, HttpSession session, HttpServletRequest request) {
		User currentUser = getCurrentUser(request,session);
		boolean allAccess = checkAccountStatus(currentUser.getAccountStatus());

		List<Loan> commodityLoan = loanService.findByUserIdAndLoanType(currentUser.getUserId(), LoanType.COMMODITY);
		model.addAttribute("user", currentUser);
		model.addAttribute("commodityloan", commodityLoan);
		model.addAttribute("activePage", "Commodity Loan History");

		// Update session
		updateSessionUser(session, currentUser);

		return allAccess ? "comm_loan_history" : "account_pending";
	}

	@GetMapping("/cdshare")
	public String cdShare(Model model,
						  HttpSession session,
						  HttpServletRequest request) {


		User currentUser = getCurrentUser(request, session);

		boolean allAccess =
				checkAccountStatus(currentUser.getAccountStatus());

		CDShareDeductions cdShareDeductions =
				cdShareDeductionService.findLatestRecordByUserId(
						currentUser.getUserId());

		if (cdShareDeductions == null) {
			cdShareDeductions = new CDShareDeductions();
		}

		Loan personalLoan =
				loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
						currentUser.getUserId(),
						LoanType.PERSONAL);


		Loan commodityLoan =
				loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
						currentUser.getUserId(),
						LoanType.COMMODITY);

		model.addAttribute("personalLoan", personalLoan);
		model.addAttribute("commodityLoan", commodityLoan);

		model.addAttribute("cdShareDeductions", cdShareDeductions);

		model.addAttribute(
				"personalLoanTotal",
				personalLoan != null
						? personalLoan.getLoanBalance()
						: 0);

		model.addAttribute(
				"commodityLoanTotal",
				commodityLoan != null
						? commodityLoan.getLoanBalance()
						: 0);

		model.addAttribute("activePage", "CD Share");

		updateSessionUser(session, currentUser);

		return allAccess ? "cd_share" : "account_pending";
	}

	@PostMapping("/cdshare")
	public String changeCdShare(@RequestParam("amt") double amt, HttpSession session, Model model, HttpServletRequest request) {


		User currentUser = getCurrentUser(request,session);
		LocalDate today = LocalDate.now();
		boolean isEditableDate = today.getDayOfMonth() >= 1 && today.getDayOfMonth() <= 15;

		if (isEditableDate && amt >= loanInfoService.findLoanInfo().getCdMinimum()) {
			try {
				currentUser.setCdCut(amt);
				// Update user and refresh cache
			currentUser = userService.updateUser(currentUser);
			request.setAttribute("currentUser", currentUser); // Update cache with fresh data

				// Update session with modified user
				updateSessionUser(session, currentUser);

				model.addAttribute("message", "CD Cut amount updated successfully!");
			} catch (Exception e) {
				model.addAttribute("messageerr", "Failed to update CD Cut amount. Please try again.");
				System.out.println("error -> " + e);
			}
		} else {
			model.addAttribute("messageerr", "CD Cut amount can only be changed between the 1st and 15th of the month "
					+ "and amount should not be more than or equal to 2000.");
		}

		// Reload CD share data
		CDShareDeductions cdShareDeductions =
				cdShareDeductionService.findLatestRecordByUserId(
						currentUser.getUserId());

		if (cdShareDeductions == null) {
			cdShareDeductions = new CDShareDeductions();
		}

		Loan personalLoan =
				loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
						currentUser.getUserId(),
						LoanType.PERSONAL);


		Loan commodityLoan =
				loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
						currentUser.getUserId(),
						LoanType.COMMODITY);

		model.addAttribute("personalLoan", personalLoan);
		model.addAttribute("commodityLoan", commodityLoan);

		model.addAttribute("cdShareDeductions", cdShareDeductions);

		model.addAttribute(
				"personalLoanTotal",
				personalLoan != null ? personalLoan.getLoanBalance() : 0);

		model.addAttribute(
				"commodityLoanTotal",
				commodityLoan != null ? commodityLoan.getLoanBalance() : 0);

		return "cd_share";
	}

	@GetMapping("/emi")
	public String emiHistory(Model model, HttpSession session, HttpServletRequest request) {
		User currentUser = getCurrentUser(request,session);

		model.addAttribute("activePage", "All EMI's");
		List<EmiPaymentsDetails> details = emiPaymentsService.findByUserId(currentUser.getUserId());

		model.addAttribute("details", details);

		// Update session
		updateSessionUser(session, currentUser);

		return "emi_history";
	}

	@GetMapping("/cdsharehis")
	public String cdShareHistory(Model model, HttpSession session, HttpServletRequest request) {
		User currentUser = getCurrentUser(request,session);

		model.addAttribute("activePage", "CD Share History");
		List<CDShareDeductions> lst = cdShareDeductionService.findByUserId(currentUser.getUserId());

		model.addAttribute("cdshare", lst);

		// Update session
		updateSessionUser(session, currentUser);

		return "cdshare_history";
	}
	
	@GetMapping("/resetpassword")
	public String newPassword(Model model, HttpSession session, HttpServletRequest request) {
		User currentUser = getCurrentUser(request,session);
		
		model.addAttribute("activePage", "Password Reset");

		// Update session
		updateSessionUser(session, currentUser);
		
		return "passwordreset";
	}
	
	@PostMapping("/resetpassword")
	public String setNewPassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, 
			HttpSession session, Model model, HttpServletRequest request) {
		
		User currentUser = getCurrentUser(request,session);
		
		boolean isPasswordValid = userService.checkPassword(currentUser.getUserId(), oldPassword);
		
		if (!isPasswordValid) {
			model.addAttribute("oldPassword", oldPassword);
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("error", "Old password did not match");
			return "passwordreset";
		}
		
		// Regex for validating password
		final String pass = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$";
		Pattern pattern = Pattern.compile(pass);
		Matcher matcher = pattern.matcher(newPassword);
		
		if (matcher.matches()) {
			String encryptedPassword = passwordEncoder.encode(newPassword);
			currentUser.setPassword(encryptedPassword);
			userRepository.save(currentUser);

			// Update cache with modified user
			request.setAttribute("currentUser", currentUser);
			// Update session with modified user
			updateSessionUser(session, currentUser);

			model.addAttribute("success", "changed password");
		} else {
			model.addAttribute("error", "password is not according to the requirements.");
			return "passwordreset";
		}
		
		return "passwordreset";
	}
	
	@GetMapping("/passcheck")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> checkPass(@RequestParam("password") String password, HttpServletRequest request, HttpSession session) {
		
		try {
			User currentUser = getCurrentUser(request,session);
			boolean samePass = userService.checkPassword(currentUser.getUserId(), password);
			
			Map<String, Object> response = new HashMap<>();
			response.put("samePass", samePass);
			response.put("userId", currentUser.getUserId());
			
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/help")
	public String userTest(Model model, HttpSession session, HttpServletRequest request) {
		User currentUser = getCurrentUser(request,session);
		
		model.addAttribute("activePage", "Help");

		// Update session
		updateSessionUser(session, currentUser);

		return "demo";
	}
	
	@GetMapping("/accountClosure")
	@ResponseBody
	public ResponseEntity<?> applyForAccountClosure(HttpSession session, HttpServletRequest request) {
		
		try {
			User currentUser = getCurrentUser(request,session);
			currentUser.setAccountSettlement(AccountSettlement.APPLIED);
			userService.update(currentUser);

			// Update cache and session with modified user
			request.setAttribute("currentUser", currentUser);
			updateSessionUser(session, currentUser);

			return ResponseEntity.ok("1");
		} catch (Exception e) {
			return ResponseEntity.ok("0");
		}
	}
	
	@GetMapping("/history/personalloan/editemi")
	public String editLoanEmiPer(Model model, HttpSession session, HttpServletRequest request) {
		User currentUser = getCurrentUser(request,session);
		
		model.addAttribute("activePage", "Change Emi");
		
		Loan loan = null;
		try {
			loan = loanService.findLastOsLoan(currentUser.getUserId(), LoanType.PERSONAL);
		} catch (Exception e) {
			// loan remains null
		}
		
		model.addAttribute("loan", loan);

		// Update session
		updateSessionUser(session, currentUser);
		
		return "edit_loan_emi";
	}
	
	@GetMapping("/history/commodityloan/editemi")
	public String editLoanEmiCom(Model model, HttpSession session, HttpServletRequest request) {
		User currentUser = getCurrentUser(request,session);
		
		model.addAttribute("activePage", "Change Emi");
		
		Loan loan = null;
		try {
			loan = loanService.findLastOsLoan(currentUser.getUserId(), LoanType.COMMODITY);
		} catch (Exception e) {
			// loan remains null
		}
		
		model.addAttribute("loan", loan);

		// Update session
		updateSessionUser(session, currentUser);
		
		return "edit_loan_emi";
	}
	
	@GetMapping("/updateEmi")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateEmi(
	        @RequestParam("amount") Double amount,
	        @RequestParam("loanType") String loanType,
	        HttpSession session, 
	        HttpServletRequest request) {
	    
	    User currentUser = getCurrentUser(request, session);
	    LocalDate today = LocalDate.now();
	    boolean isEditableDate = today.getDayOfMonth() >= 1 && today.getDayOfMonth() <= 15;
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        LoanType type = LoanType.valueOf(loanType.toUpperCase());
	        Loan loan = loanService.findLastOsLoan(currentUser.getUserId(), type);
	        
	        if (loan == null) {
	            response.put("status", 0.0);
	            response.put("message", "No active loan found");
	            return ResponseEntity.ok(response);
	        }

	        double minAmount = loan.getLoanBalance() / loan.getEmiBalance();
	        if (isEditableDate && amount >= minAmount) {
	            loan.setInstalmentAmount(amount);
	            loan = loanService.save(loan);
	            response.put("amount", amount);
	            response.put("status", 1.0);
	            updateSessionUser(session, currentUser);
	        } else {
	            response.put("status", 0.0);
	            response.put("message", isEditableDate ? 
	                "Amount must be at least " + minAmount : 
	                "Can only update EMI between 1st-15th of month");
	        }
	        
	        return ResponseEntity.ok(response);
	        
	    } catch (IllegalArgumentException e) {
	        response.put("status", 0.0);
	        response.put("message", "Invalid loan type");
	        return ResponseEntity.badRequest().body(response);
	    }
	}
}