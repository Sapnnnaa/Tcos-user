package com.telco.telco_co_app.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.telco.telco_co_app.model.AccountStatus;
import com.telco.telco_co_app.model.ApplicationType;
import com.telco.telco_co_app.model.Banks;
import com.telco.telco_co_app.model.CDShareDeductions;
import com.telco.telco_co_app.model.Company;
import com.telco.telco_co_app.model.Loan;
import com.telco.telco_co_app.model.LoanInfo;
import com.telco.telco_co_app.model.LoanStatus;
import com.telco.telco_co_app.model.LoanType;
import com.telco.telco_co_app.model.User;
import com.telco.telco_co_app.service.BankService;
import com.telco.telco_co_app.service.CDShareDeductionService;
import com.telco.telco_co_app.service.CompanyService;
import com.telco.telco_co_app.service.LoanInfoService;
import com.telco.telco_co_app.service.LoanService;
import com.telco.telco_co_app.service.PdfService;
import com.telco.telco_co_app.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.thymeleaf.context.Context;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Controller
@RequestMapping("/user")
public class LoanApplicationController {

	@Autowired
	private UserService userService;

	@Autowired
	private LoanService loanService;

	@Autowired
	private CDShareDeductionService cdShareDeductionService;

	@Autowired
	private BankService bankService;

	@Autowired
	private PdfService pdfService;

	@Autowired
	private LoanInfoService loanInfoService;

	@Autowired
	private CompanyService companyService;

	// session attributes
	private static final String SESSION_USER = "sessionUser";
	private static final String SESSION_CD_SHARE = "sessionCDShare";
	private static final String SESSION_BANKS = "sessionBanks";
	private static final String SESSION_COMPANY = "sessionCompany";
	private static final String SESSION_LOAN_AMT = "sessionLoanAmt";
	private static final String SESSION_INSTALMENT_AMT = "sessionInstalmentAmt";
	private static final String SESSION_APPLICATION_TYPE = "sessionApplicationType";
	private static final String SESSION_DUE_LOAN = "sessionDueLoan";
	private static final String SESSION_ELIGIBLE = "sessionEligible";
	private static final String SESSION_LOAN_APPLIED_AMT = "sessionLoanAppliedAmt";
	private static final String SESSION_LOAN_REASON = "sessionLoanReason";
	private static final String SESSION_NEW_MEMBER = "sessionNewMember";
	private static final String SESSION_LOAN_INFO = "sessionLoanInfo";
	private static final String SESSION_LOAN_APPLIED = "sessionLoanApplied";
	private static final String SESSION_PREV_LOAN = "sessionPrevLoan";
	private static final String SESSION_INSTALMENT_LEFT = "sessionInstalmentLeft";
	private static final String SESSION_DESCRIPTION = "sessionDescription";

	private boolean checkAccountStatus(AccountStatus status) {
		return status == AccountStatus.ACTIVE;
	}

	// initialize session attributes
	private void initializeSession(HttpSession session) {
		session.setAttribute(SESSION_LOAN_APPLIED, false);
	}

	// clear loan application session data
	private void clearLoanSession(HttpSession session) {
		session.removeAttribute(SESSION_LOAN_AMT);
		session.removeAttribute(SESSION_INSTALMENT_AMT);
		session.removeAttribute(SESSION_APPLICATION_TYPE);
		session.removeAttribute(SESSION_DUE_LOAN);
		session.removeAttribute(SESSION_ELIGIBLE);
		session.removeAttribute(SESSION_LOAN_APPLIED_AMT);
		session.removeAttribute(SESSION_LOAN_REASON);
		session.removeAttribute(SESSION_NEW_MEMBER);
		session.removeAttribute(SESSION_LOAN_INFO);
		session.removeAttribute(SESSION_PREV_LOAN);
		session.removeAttribute(SESSION_INSTALMENT_LEFT);
		session.removeAttribute(SESSION_DESCRIPTION);
	}

	private String validateLoanApplication(User user, CDShareDeductions cdShareDeductions, double loanAmount,
			Model model) {
		if (user == null) {
			return "User information not found";
		}

		if (cdShareDeductions == null) {
			return "Share deduction information not found";
		}

		String userType = user.getUserType();
		if (userType == null || userType.trim().isEmpty()) {
			return "User type not defined";
		}
		userType = userType.trim();

		double shareTotal = cdShareDeductions.getShareDeductionTotal();
		if (shareTotal <= 0) {
			return "Invalid share amount: " + shareTotal;
		}

		double multiplier;
		boolean isNewMember;
		if ("New Member".equalsIgnoreCase(userType)) {
			multiplier = 5.0;
			isNewMember = true;
		} else if ("Existing Member".equalsIgnoreCase(userType)) {
			multiplier = 10.0;
			isNewMember = false;
		} else {
			multiplier = 5.0;
			isNewMember = true;
		}

		double maxAllowedLoan = shareTotal * multiplier;

		if (loanAmount <= 0) {
			return "Loan amount must be greater than 0";
		}

		if (loanAmount > maxAllowedLoan) {
			return String.format(
					"Loan amount ₹%.2f exceeds maximum allowed ₹%.2f for %s. You can get %.0f times your share amount (₹%.2f)",
					loanAmount, maxAllowedLoan, userType, multiplier, shareTotal);
		}

		if (shareTotal < 16000) {
			return "Minimum share amount of ₹16,000 required to apply for loan. Current share amount: ₹" + shareTotal;
		}

		return null; // No validation errors
	}

	// For personal loan
	@GetMapping("/termsper")
	public String loanTerms(Model model, HttpSession session) {
		model.addAttribute("activePage", "Personal Loans");
		initializeSession(session);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName();

		// Fetch user details and store in session
		User user = userService.findByUserId(userId);
		session.setAttribute(SESSION_USER, user);

		AccountStatus status = user.getAccountStatus();
		boolean allAccess = checkAccountStatus(status);
		if (!allAccess)
			return "account_pending";
		else
			return "personal_loan_tc";
	}

	@GetMapping("/termsper/personalloan")
	public String personalLoan(Model model, HttpSession session) {
		model.addAttribute("activePage", "Personal Loans");

		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null) {
			return "redirect:/login";
		}

		CDShareDeductions cdShareDeductions = cdShareDeductionService.findLatestRecordByUserId(user.getUserId());
		if (cdShareDeductions == null) {
			cdShareDeductions = new CDShareDeductions();
		}
		session.setAttribute(SESSION_CD_SHARE, cdShareDeductions);

		Banks banks = bankService.getByUserId(user.getUserId());
		session.setAttribute(SESSION_BANKS, banks);

		model.addAttribute("user", user);
		model.addAttribute("cdShare", cdShareDeductions);

		double loanDue = loanService.findByOutStandingLoan(user.getUserId(), LoanType.PERSONAL);
		session.setAttribute(SESSION_DUE_LOAN, loanDue);
		model.addAttribute("loandue", loanDue);

		// for instalment
		int monthsDiff = loanService.findInstalment(user.getDateOfRetirement(), new Date());
		session.setAttribute(SESSION_INSTALMENT_LEFT, monthsDiff);
		model.addAttribute("monthsDiff", monthsDiff);

		LoanInfo loanInfo = loanInfoService.findLoanInfo();
		session.setAttribute(SESSION_LOAN_INFO, loanInfo);

		String userType = user.getUserType();
		boolean isNewMember = "New Member".equalsIgnoreCase(userType != null ? userType.trim() : "");
		session.setAttribute(SESSION_NEW_MEMBER, isNewMember ? 1 : 0);

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		cal1.setTime(new Date());
		try {
			cal2.setTime(user.getApprovedAt());
		} catch (Exception e) {
			model.addAttribute("message", "You need to get your account approved and complete the process.");
			return "personal_loan";
		}

		int yearDiff = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		double monthDiff = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
		double totalMonths = yearDiff * 12 + monthDiff;
		if (totalMonths < 4.0 || cdShareDeductions.getShareDeductionTotal() < 16000) {
			model.addAttribute("message", "You have to wait till 10 months from date of enrollment to apply for loan."
					+ " You also have to make sure your share amount is paid full. After you have completed the process you can apply");
		} else if (totalMonths >= 4.0) {

			Date applyDate = new Date();

			Date lDate = null;
			try {
				lDate = loanService.findByLastLoanApproveDateByUserId(user.getUserId(), LoanType.PERSONAL);
			} catch (Exception e) {
				System.out.println(e);
			}

			ApplicationType applicationType = ApplicationType.NA;
			String description= "";
			if (lDate != null) {

				cal1 = Calendar.getInstance();
				cal2 = Calendar.getInstance();

				cal1.setTime(applyDate);
				cal2.setTime(lDate);

				yearDiff = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
				monthDiff = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
				totalMonths = yearDiff * 12 + monthDiff;

				if (cdShareDeductions.getShareDeductionTotal() >= 16000 && totalMonths >= 12) {
					applicationType = ApplicationType.NA;
					model.addAttribute("aplimsgna", "No Application needed");
					description = "No Application needed.";
				} else if (cdShareDeductions.getShareDeductionTotal() >= 16000 && totalMonths >= 4
						&& totalMonths < 12) {
					applicationType = ApplicationType.Application;
					model.addAttribute("aplimsga", "<strong>Application needed</strong> for this loan");
					description = "Application to the society is needed in this"
							+ " condition as last loan taken is between 4 and 12 months.";
				} else if(totalMonths < 4) {
					applicationType = ApplicationType.Management_approval;
					model.addAttribute("aplimsgma",
							"There is a need of an <strong>appllication to the management</strong> for this loan approval.");
					description = "Application to management is needed as last loan taken was within 4 months.";
				}
			} else {
				if (cdShareDeductions.getShareDeductionTotal() >= 16000 && totalMonths >= 12.0 
			            && cdShareDeductions.getShareDeductionTotal() <= loanInfo.getShareAmount()) {
			        model.addAttribute("aplimsgna", "There is no requirement for application in this loan.");
			        applicationType = ApplicationType.NA;
			        description = "No Application needed.";
			    } else if (cdShareDeductions.getShareDeductionTotal() > 16000 && totalMonths >= 4.0) {
			        model.addAttribute("aplimsga","This loan requires <strong>Application</strong> for approval.");
			        applicationType = ApplicationType.Application;
			        description = "There is a need of application as joining date criteria is not fulfilled.";
			    } else { 
			    	model.addAttribute("aplimsgma","Cannot get loan now.");
			    }
			}
			session.setAttribute(SESSION_APPLICATION_TYPE, applicationType);
			session.setAttribute(SESSION_DESCRIPTION, description);
		}

		return "personal_loan";
	}

	@PostMapping("/termsper/personalloan")
	public String applyPersonalLoan(Model model, @RequestParam("loanamt") double loanamt,
			@RequestParam("instalmentAmt") double instalmentAmt, @RequestParam("loanReason") String loanReason,
			@RequestParam("monthsDiff") int monthsDiff, // This stays the same
			HttpSession session) {
		model.addAttribute("activePage", "Personal Loans");

		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null) {
			return "redirect:/login";
		}

		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);

		String validationError = validateLoanApplication(user, cdShareDeductions, loanamt, model);
		if (validationError != null) {
			model.addAttribute("message", validationError);
			model.addAttribute("user", user);
			model.addAttribute("cdShare", cdShareDeductions);
			model.addAttribute("monthsDiff", monthsDiff);
			model.addAttribute("loanamt", loanamt);

			Double loanDue = (Double) session.getAttribute(SESSION_DUE_LOAN);
			if (loanDue != null) {
				model.addAttribute("loandue", loanDue);
			}

			return "personal_loan";
		}

		session.setAttribute(SESSION_LOAN_REASON, loanReason);
		session.setAttribute(SESSION_LOAN_APPLIED_AMT, loanamt);
		session.setAttribute(SESSION_LOAN_AMT, loanamt);
		session.setAttribute(SESSION_INSTALMENT_AMT, instalmentAmt);
		session.setAttribute(SESSION_INSTALMENT_LEFT, monthsDiff);

		model.addAttribute("monthsDiff", monthsDiff);

		ApplicationType applicationType = (ApplicationType) session.getAttribute(SESSION_APPLICATION_TYPE);
		if (applicationType == ApplicationType.NA) {
			model.addAttribute("aplimsgna", "No Application needed");
		} else if (applicationType == ApplicationType.Application) {
			model.addAttribute("aplimsga", "Application needed for this loan");
		} else {
			model.addAttribute("aplimsgma",
					"There is a need of an appllication to the management for this loan approval.");
		}

		String userType = user.getUserType();
		boolean isNewMember = "New Member".equalsIgnoreCase(userType != null ? userType.trim() : "");
		session.setAttribute(SESSION_NEW_MEMBER, isNewMember ? 1 : 0);

		double dueLoan = (Double) session.getAttribute(SESSION_DUE_LOAN);
		int currInstalment = (int) Math.ceil((double) (loanamt + dueLoan) / instalmentAmt);

		if (currInstalment > monthsDiff) {
			model.addAttribute("messageerr",
					"EMI period (" + currInstalment + " months) exceeds time until retirement (" + monthsDiff
							+ " months). " + "Please increase your installment amount to at least ₹"
							+ Math.ceil((loanamt + dueLoan) / monthsDiff));
			model.addAttribute("loanamt", loanamt);
			return "personal_loan";
		}

		if (currInstalment > 50) {
			model.addAttribute("messageerr", "Emi should be less than 50 months. "
					+ "Current amount entered makes instalments more than 50, enter another higher amount.");
			model.addAttribute("loanamt", loanamt);
			return "personal_loan";
		}

		return "redirect:personalloan/check";
	}

	@GetMapping("/termsper/personalloan/check")
	public String finalApplypersonal(Model model, HttpSession session) {
		model.addAttribute("activePage", "Personal Loans");
		session.setAttribute(SESSION_LOAN_APPLIED, false);

		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null) {
			return "redirect:/login";
		}
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		Loan prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
				LoanStatus.PENDING);
		if (prevLoan == null)
			prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
					LoanStatus.APPROVED);
		if (prevLoan == null)
			prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL, 
					LoanStatus.CHECKED);
		if (prevLoan == null)
			prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
					LoanStatus.VERIFIED);
		
		model.addAttribute("prevLoan", prevLoan);
		session.setAttribute(SESSION_PREV_LOAN, prevLoan);

		if (loanamt != null) {
			String validationError = validateLoanApplication(user, cdShareDeductions, loanamt, model);
			if (validationError != null) {
				model.addAttribute("messageerr", "Validation failed: " + validationError);
				session.setAttribute(SESSION_LOAN_APPLIED, true);
				return "personal_loan_chk";
			}
		}

		LoanStatus prevLoanStatus = loanService.findLastLoanStatus(user.getUserId(), LoanType.PERSONAL);

		if (prevLoanStatus == LoanStatus.PENDING || prevLoanStatus == LoanStatus.CHECKED
				|| prevLoanStatus == LoanStatus.VERIFIED) {
			session.setAttribute(SESSION_LOAN_APPLIED, true);
			model.addAttribute("messageerr",
					"Previous loan is not approved, " + "you can apply for new loan after previous loan is approved. "
							+ "You can check in your loan history for the loans you applied.");
		}

		double loanDue = (Double) session.getAttribute(SESSION_DUE_LOAN);
		Integer newMemberInt = (Integer) session.getAttribute(SESSION_NEW_MEMBER);
		boolean isNewMember = (newMemberInt != null && newMemberInt == 1);

		double shareTotal = cdShareDeductions != null ? cdShareDeductions.getShareDeductionTotal() : 0;
		double multiplier = isNewMember ? 5.0 : 10.0;
		double maxLoan = shareTotal * multiplier;

		double overhead = loanDue + loanamt - maxLoan;
		double applied = loanamt;
		double eligible;

		if (overhead < 0) {
			eligible = maxLoan - loanDue;
		} else {
			loanamt = loanamt - overhead;
			eligible = loanamt;
		}

		if (eligible <= 0) {
			session.setAttribute(SESSION_LOAN_APPLIED, true);
			model.addAttribute("messageerr", "You are not eligible for loan. Eligible amount is less.");
		}

		String userType = user.getUserType();
		if ("Existing Member".equalsIgnoreCase(userType != null ? userType.trim() : "")) {
			if (eligible > shareTotal * 10) {
				eligible = shareTotal * 10;
			}
		} else {
			if (eligible > shareTotal * 5) {
				eligible = shareTotal * 5;
			}
		}

		session.setAttribute(SESSION_ELIGIBLE, eligible);
		model.addAttribute("eligibleloan", eligible);
		model.addAttribute("loandue", loanDue);
		model.addAttribute("appliedloan", applied);

		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		model.addAttribute("bank", banks);

		return "personal_loan_chk";
	}

	@PostMapping("/termsper/personalloan/check")
	public String personalApply(Model model, HttpSession session) {
		model.addAttribute("activePage", "Personal Loans");

		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null) {
			return "redirect:/login";
		}

		Loan prevLoan = (Loan) session.getAttribute(SESSION_PREV_LOAN);
		model.addAttribute("prevLoan", prevLoan);

		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		Double instalmentAmt = (Double) session.getAttribute(SESSION_INSTALMENT_AMT);
		String loanReason = (String) session.getAttribute(SESSION_LOAN_REASON);
		Double loanAppliedAmt = (Double) session.getAttribute(SESSION_LOAN_APPLIED_AMT);
		ApplicationType applicationType = (ApplicationType) session.getAttribute(SESSION_APPLICATION_TYPE);
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);
		Boolean loanApplied = (Boolean) session.getAttribute(SESSION_LOAN_APPLIED);
		String description = (String) session.getAttribute(SESSION_DESCRIPTION);
		double eligible = (double) session.getAttribute(SESSION_ELIGIBLE);

		if (loanamt != null) {
			String validationError = validateLoanApplication(user, cdShareDeductions, loanamt, model);
			if (validationError != null) {
				model.addAttribute("messageerr", "Final validation failed: " + validationError);
				session.setAttribute(SESSION_LOAN_APPLIED, true);
				Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
				model.addAttribute("bank", banks);
				return "personal_loan_chk";
			}
		}

		// Create new Loan object
		Loan personalLoan = new Loan();
		personalLoan.setUserId(user.getUserId());
		
		if(loanamt<=eligible)
			personalLoan.setLoanAmount(loanamt);
		else
			personalLoan.setLoanAmount(eligible);
		
		personalLoan.setLoanApplyDate(new Date());
		personalLoan.setLoanStatus(LoanStatus.PENDING);
		personalLoan.setLoanType(LoanType.PERSONAL);
		personalLoan.setIntrestRate(loanInfo.getInterestOnNormal());
		personalLoan.setApplicationType(applicationType);
		personalLoan.setUpdatedAt(new Date());
		personalLoan.setInstalmentAmount(instalmentAmt);
		personalLoan.setEmiBalance(50);
		personalLoan.setEmiPaid(0);
		personalLoan.setAppliedLoan(loanAppliedAmt);
		personalLoan.setLoanReason(loanReason);
		personalLoan.setDescription(description);

		// Calculate time difference
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		cal1.setTime(new Date());
		cal2.setTime(user.getApprovedAt());

		int yearDiff = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		double monthDiff = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
		double totalMonths = yearDiff * 12 + monthDiff;

		try {
			if (loanApplied != null && loanApplied) {
				model.addAttribute("messageerr",
						"You can go back. " + "You can check in your loan history for the loans you applied.");
			} else if (totalMonths < 4.0 || cdShareDeductions.getShareDeductionTotal() < 16000) {
				model.addAttribute("messageerr",
						"You have to wait till 10 months from date of enrollment to apply for a loan."
								+ " You also have to make sure your share amount is paid in full. After you have completed the process, you can apply.");
			} else if (cdShareDeductions.getShareDeductionTotal() >= 16000 && totalMonths >= 4.0) {

				Date applyDate = personalLoan.getLoanApplyDate();

				try {
					prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
							LoanStatus.PAID);
				} catch (Exception e) {
					System.out.println(e);
				}

				if (prevLoan != null) {
					// Previous loan exists
					Calendar repaymentCalendar = Calendar.getInstance();
					repaymentCalendar.setTime(prevLoan.getEndDate());
					repaymentCalendar.add(Calendar.DAY_OF_MONTH, 60);
					Date sixtyDaysFromRepayment = repaymentCalendar.getTime();

					cal2.setTime(prevLoan.getLoanApproveDate());
					cal2.add(Calendar.MONTH, 4);
					Date fourMonthsFromLastLoan = cal2.getTime();

					Date earliestEligibilityDate = (fourMonthsFromLastLoan.before(sixtyDaysFromRepayment))
							? fourMonthsFromLastLoan
							: sixtyDaysFromRepayment;

					if (applyDate.before(earliestEligibilityDate)) {
						model.addAttribute("messageerr",
								"You are not eligible to apply for a loan yet. "
										+ "Eligibility starts after either 60 days from the repayment date "
										+ "or 4 months from the last loan date, whichever is earlier.");
					} else {
						handleLoanConditions(model, personalLoan, totalMonths, session);
					}
				} else {
					// First time loan application - Check based on current share amount multiplier
					double shareAmount = cdShareDeductions.getShareDeductionTotal();

					if (totalMonths >= 4.0 && shareAmount > 0) {
						loanService.save(personalLoan);
						model.addAttribute("message", "Loan application submitted successfully! "
								+ "You can download your loan application PDF.");
						model.addAttribute("downloadUrl", "/telco_co/user/termsper/personalloan/downloadPdf");
						personalLoan.setApplicationType(ApplicationType.NA);
					} else {
						model.addAttribute("messageerr",
								"You need to meet the minimum 4-month enrollment period and have some share amount before applying for a loan.");
					}
				}
			}
			session.setAttribute(SESSION_LOAN_APPLIED, true);
		} catch (Exception e) {
			model.addAttribute("messageerr", "Failed to submit loan application. Please try again.");
			e.printStackTrace();
		}

		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		model.addAttribute("bank", banks);
		model.addAttribute("personalloan", personalLoan);
		model.addAttribute("eligibleloan",eligible);

		return "personal_loan_chk";
	}

	// Helper method to handle conditions based on share deductions and total months
	private void handleLoanConditions(Model model, Loan personalLoan, double totalMonths, HttpSession session) {
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);

		if (cdShareDeductions.getShareDeductionTotal() >= loanInfo.getShareAmount() && totalMonths >= 12) {
			loanService.save(personalLoan);
			personalLoan.setApplicationType(ApplicationType.NA);
			model.addAttribute("message", "Loan application submitted successfully! "
					+ "You can check in your loan history for the loans you applied.");
		} else if (cdShareDeductions.getShareDeductionTotal() >= loanInfo.getShareAmount() && totalMonths >= 4
				&& totalMonths < 12) {
			loanService.save(personalLoan);
			personalLoan.setApplicationType(ApplicationType.Application);
			model.addAttribute("message", "Loan application submitted successfully but "
					+ "you need to submit an application to the society. Once the application is cleared, you can get the loan. "
					+ "You can check in your loan history for the loans you applied.");
		} else {
			loanService.save(personalLoan);
			personalLoan.setApplicationType(ApplicationType.Management_approval);
			model.addAttribute("message", "Loan application submitted successfully but "
					+ "you need to submit an application to the society for management approval. If cleared, you will get your loan. "
					+ "You can check in your loan history for the loans you applied.");
		}
	}

	@GetMapping("/termscom")
	public String loancTerms(Model model, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");
		initializeSession(session);
		clearLoanSession(session);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName();

		User user = userService.findByUserId(userId);
		session.setAttribute(SESSION_USER, user);

		AccountStatus status = user.getAccountStatus();
		boolean allAccess = checkAccountStatus(status);
		if (!allAccess)
			return "account_pending";
		else
			return "commodity_loan_tc";
	}

	@GetMapping("/termscom/commodityloan")
	public String commLoan(Model model, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");

		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null) {
			return "redirect:/login";
		}

		CDShareDeductions cdShareDeductions = cdShareDeductionService.findLatestRecordByUserId(user.getUserId());
		session.setAttribute(SESSION_CD_SHARE, cdShareDeductions);

		Banks banks = bankService.getByUserId(user.getUserId());
		session.setAttribute(SESSION_BANKS, banks);

		model.addAttribute("user", user);
		model.addAttribute("cdShare", cdShareDeductions);

		LoanInfo loanInfo = loanInfoService.findLoanInfo();
		session.setAttribute(SESSION_LOAN_INFO, loanInfo);

		if (user.getUserType().equals("New Member"))
			model.addAttribute("maxcomloan", loanInfo.getMaxCommodityLoan());
		else
			model.addAttribute("maxcomloan", 100000);

		return "comm_loan";
	}

	@PostMapping("/termscom/commodityloan")
	public String applyCommodityLoan(Model model, @RequestParam("instalmentAmt") double instalmentAmt,
			@RequestParam("loanReason") String loanReason, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");

		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null) {
			return "redirect:/login";
		}

		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		Boolean loanApplied = (Boolean) session.getAttribute(SESSION_LOAN_APPLIED);

		double loanamt;
		if (user.getUserType().equals("New Member"))
			loanamt = loanInfo.getMaxCommodityLoan();
		else
			loanamt = 100000;

		session.setAttribute(SESSION_LOAN_REASON, loanReason);
		session.setAttribute(SESSION_LOAN_AMT, loanamt);
		session.setAttribute(SESSION_INSTALMENT_AMT, instalmentAmt);
		session.setAttribute(SESSION_LOAN_APPLIED_AMT, loanamt);

		double share;
		try {
			share = cdShareDeductions.getShareDeductionTotal();
		} catch (Exception e) {
			share = 0;
		}
		if (loanApplied != null && loanApplied) {
			model.addAttribute("messageerr",
					"You can go back. " + "You can check in your loan history for the loans you applied.");
		} else if (share < 16000) {
			model.addAttribute("messageerr",
					"You have to wait till 10 months from date of enrollment and complete share amount to 16000 to apply for loan."
							+ " You also have to make sure your share amount is paid full. After you have completed the process you can apply");
			session.setAttribute(SESSION_LOAN_APPLIED, true);
		} else if (share >= 16000) {
			double loan = loanService.findByLastLoanOutStandingByUserId(user.getUserId(), LoanType.COMMODITY);
			if (loan > 0.0) {
				model.addAttribute("messageerr", "You cannot get loan now. "
						+ "Clear your previous due amount and then you can apply for loan.");
				session.setAttribute(SESSION_LOAN_APPLIED, true);
			}
		}

		if (session.getAttribute(SESSION_LOAN_APPLIED) != null
				&& (Boolean) session.getAttribute(SESSION_LOAN_APPLIED)) {
			return "comm_loan";
		}

		int currInstalment = (int) Math.ceil((double) loanamt / instalmentAmt);

		if (currInstalment > 50) {
			model.addAttribute("messageerr", "Emi should be less than 50 months. "
					+ "Current amount entered makes instalments more than 50, enter another higher amount.");
			model.addAttribute("loanamt", loanamt);
			return "comm_loan";
		}

		return "redirect:commodityloan/check";
	}

	@GetMapping("/termscom/commodityloan/check")
	public String finalApplycommodity(Model model, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");
		session.setAttribute(SESSION_LOAN_APPLIED, false);

		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null) {
			return "redirect:/login";
		}

		if (loanService.findLastLoanStatus(user.getUserId(), LoanType.COMMODITY) == LoanStatus.PENDING) {
			session.setAttribute(SESSION_LOAN_APPLIED, true);
			model.addAttribute("messageerr",
					"Previous loan is on pending status, "
							+ "you can apply for new loan after previous loan is approved. "
							+ "You can check in your loan history for the loans you applied.");
		}

		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		session.setAttribute(SESSION_LOAN_APPLIED_AMT, loanamt);

		Loan prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.COMMODITY,
				LoanStatus.PENDING);
		if (prevLoan == null) {
			prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.COMMODITY,
					LoanStatus.APPROVED);
		}
		session.setAttribute(SESSION_PREV_LOAN, prevLoan);
		model.addAttribute("prevLoan", prevLoan);

		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		model.addAttribute("bank", banks);
		return "commodity_loan_chk";
	}

	@PostMapping("/termscom/commodityloan/check")
	public String commodityApply(Model model, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");

		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null) {
			return "redirect:/login";
		}

		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		Double instalmentAmt = (Double) session.getAttribute(SESSION_INSTALMENT_AMT);
		String loanReason = (String) session.getAttribute(SESSION_LOAN_REASON);
		Double loanAppliedAmt = (Double) session.getAttribute(SESSION_LOAN_APPLIED_AMT);
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		Boolean loanApplied = (Boolean) session.getAttribute(SESSION_LOAN_APPLIED);
		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);

		// Create new Loan object
		Loan commodityLoan = new Loan();
		commodityLoan.setUserId(user.getUserId());
		commodityLoan.setLoanAmount(loanamt);
		commodityLoan.setLoanApplyDate(new Date());
		commodityLoan.setLoanStatus(LoanStatus.PENDING);
		commodityLoan.setLoanType(LoanType.COMMODITY);
		commodityLoan.setIntrestRate(loanInfo.getInterestOnCommodity());
		commodityLoan.setUpdatedAt(new Date());
		commodityLoan.setApplicationType(ApplicationType.NA);
		commodityLoan.setInstalmentAmount(instalmentAmt);
		commodityLoan.setEmiBalance(50);
		commodityLoan.setEmiPaid(0);
		commodityLoan.setAppliedLoan(loanAppliedAmt);
		commodityLoan.setLoanReason(loanReason);

		try {
			double share;
			try {
				share = cdShareDeductions.getShareDeductionTotal();
			} catch (Exception e) {
				share = 0;
			}
			if (loanApplied != null && loanApplied) {
				model.addAttribute("messageerr",
						"You can go back. " + "You can check in your loan history for the loans you applied.");
			} else if (share < 16000) {
				model.addAttribute("messageerr",
						"You have to wait till 4 months from date of enrollment and complete share amount to 16000 to apply for loan."
								+ " You also have to make sure your share amount is paid full. After you have completed the process you can apply");
			} else if (share >= 16000) {
				double loan = loanService.findByLastLoanOutStandingByUserId(user.getUserId(), LoanType.COMMODITY);
				if (loan <= 0.0) {
					loanService.save(commodityLoan);
					model.addAttribute("message", "Loan application submitted successfully! "
							+ "You can check in your loan history for the loans you applied.");
					model.addAttribute("downloadUrl", "/telco_co/user/termscom/commodityloan/downloadPdf");
				} else {
					model.addAttribute("messageerr", "You cannot get loan now. "
							+ "Clear your previous due amount and then you can apply for loan.");
				}
			}
			session.setAttribute(SESSION_LOAN_APPLIED, true);
		} catch (Exception e) {
			model.addAttribute("messageerr", "Failed to submit loan application. Please try again.");
		}

		model.addAttribute("personalloan", commodityLoan);
		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		model.addAttribute("bank", banks);
		Loan prevLoan = (Loan) session.getAttribute(SESSION_PREV_LOAN);
		model.addAttribute("prevLoan", prevLoan);

		return "commodity_loan_chk";
	}

	@GetMapping("/termsper/personalloan/downloadPdf")
	public ResponseEntity<byte[]> downloadPdf(HttpSession session) {
	    User user = (User) session.getAttribute(SESSION_USER);
	    if (user == null) {
	        System.out.println("Error on PDF generation - User not found in session");
	        return ResponseEntity.badRequest().build();
	    }

	    // Get application type from session
	    ApplicationType applicationType = (ApplicationType) session.getAttribute(SESSION_APPLICATION_TYPE);
	    String description = (String) session.getAttribute(SESSION_DESCRIPTION);
	    
	    if (applicationType == null) {
	        applicationType = ApplicationType.NA;
	    }

	    Company company = companyService.findbyCompanyId(user.getCompanyId());
	    if (company == null) {
	        company = new Company();
	    }
	    session.setAttribute(SESSION_COMPANY, company);

	    Loan lloan = loanService.findLastApproveLoan(user.getUserId(), LoanType.PERSONAL);
	    CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
	    Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
	    Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
	    Double instalmentAmt = (Double) session.getAttribute(SESSION_INSTALMENT_AMT);
	    Double dueLoan = (Double) session.getAttribute(SESSION_DUE_LOAN);
	    Double eligible = (Double) session.getAttribute(SESSION_ELIGIBLE);
	    String loanReason = (String) session.getAttribute(SESSION_LOAN_REASON);
	    LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);

	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	    DecimalFormat df = new DecimalFormat("#.00");
	    
	    // Current date and year for letter references
	    Date currentDate = new Date();
	    String currentDateStr = dateFormat.format(currentDate);
	    String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

	    Context context = new Context();
	    
	    // Common variables for all templates
	    context.setVariable("userName", safeStr(user.getName()));
	    context.setVariable("ticketNumber", safeStr(user.getUserId()));
	    context.setVariable("address", safeStr(user.getCurrentAddr()));
	    context.setVariable("companyName", safeStr(company.getCompanyName()));
	    context.setVariable("deptNo", safeStr(user.getBcNumber()));
	    context.setVariable("servicePeriod", user.getSerivcePeriod() != ""? safeStr(user.getSerivcePeriod()):"              ");
	    context.setVariable("dateOfRetirement", 
	        user.getDateOfRetirement() != null ? dateFormat.format(user.getDateOfRetirement()) : "              ");
	    context.setVariable("loanAmount", df.format(safeDouble(loanamt)));
	    context.setVariable("monthlyInstalment", df.format(safeDouble(instalmentAmt)));
	    context.setVariable("interestRate", loanInfo != null ? safeStr(loanInfo.getInterestOnNormal()) : "             ");
	    context.setVariable("loanReason", safeStr(loanReason));
	    context.setVariable("shareTotal", 
	        cdShareDeductions != null ? df.format(safeDouble(cdShareDeductions.getShareDeductionTotal())) : "0");
	    context.setVariable("outstandingLoan", df.format(safeDouble(dueLoan)));
	    context.setVariable("mobileNumber", safeStr(user.getPhoneNum()));
	    context.setVariable("email", safeStr(user.getEmail()));
	    context.setVariable("currentDate", currentDateStr);
	    context.setVariable("year", currentYear);
	    context.setVariable("approvalReason", safeStr(description));
	    context.setVariable("fathersname", safeStr(user.getFathersName()));
	    
	 // Add additional variables needed for the original form
        context.setVariable("loanType", "NORMAL");
        context.setVariable("bankName", banks != null ? safeStr(banks.getBankName()) : "                  ");
        context.setVariable("osLoan", df.format(safeDouble(dueLoan)));
        context.setVariable("dateOfLastLoan",
            (lloan != null && lloan.getLoanApproveDate() != null) ? dateFormat.format(lloan.getLoanApproveDate()) : "				");
        context.setVariable("loanTaken", lloan != null ? df.format(safeDouble(lloan.getLoanAmount())) : "0");
        context.setVariable("noOfEmiPaid", lloan != null ? safeStr(lloan.getEmiPaid()) : "0");
        context.setVariable("amountRecovered", lloan != null ? df.format(safeDouble(lloan.getLoanRepaid())) : "0");
        context.setVariable("loanOs", lloan != null ? df.format(safeDouble(lloan.getLoanBalance())) : "0");
        context.setVariable("cdBalance",
            cdShareDeductions != null ? df.format(safeDouble(cdShareDeductions.getCdDeductionTotal())) : "0");
        context.setVariable("accountNumber", banks != null ? safeStr(banks.getAccountNum()) : "					");
        context.setVariable("branch", banks != null ? safeStr(banks.getBranchName()) : "			");
        context.setVariable("ifsc", banks != null ? safeStr(banks.getIfscCode()) : "			");
        context.setVariable("eligible", df.format(safeDouble(eligible)));
        context.setVariable("dos", user.getDateOfRetirement() != null ? dateFormat.format(user.getDateOfRetirement()) : "			");

	    String imagePath = "";
	    try {
	        imagePath = getClass().getResource("/static/dist/images/company_logo.png").toExternalForm();
//	        System.out.println(imagePath);
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	    context.setVariable("logoPath", imagePath);

	    // Determine which template to use and filename based on application type
	    String templateName;
	    String filename;
	    
	    switch (applicationType) {
	        case Management_approval:
	            templateName = "management_approval_letter";
	            filename = "management_approval_letter.pdf";
	            break;
	        case Application:
	            templateName = "society_approval_letter";
	            filename = "society_approval_letter.pdf";
	            break;
	        case NA:
	        default:
	            // Use the original loan application form for direct processing
	            templateName = "loan_application_normal";
	            filename = "loan_application.pdf";
	            
	            
	            break;
	    }

	    // Generate PDF
	    byte[] pdfBytes = pdfService.generatePdf(templateName, context);

	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
	        .contentType(MediaType.APPLICATION_PDF)
	        .body(pdfBytes);
	}


	@GetMapping("/termscom/commodityloan/downloadPdf")
	public ResponseEntity<byte[]> downloadPdfComm(HttpSession session) {
		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null) {
			return ResponseEntity.badRequest().build();
		}

		Company company = (Company) session.getAttribute(SESSION_COMPANY);
		if (company == null) {
			company = companyService.findbyCompanyId(user.getCompanyId());
			if (company == null) {
				company = new Company();
			}
			session.setAttribute(SESSION_COMPANY, company);
		}

		Loan lloan = loanService.findLastApproveLoan(user.getUserId(), LoanType.PERSONAL);
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		Double instalmentAmt = (Double) session.getAttribute(SESSION_INSTALMENT_AMT);
		Double dueLoan = (Double) session.getAttribute(SESSION_DUE_LOAN);
		String loanReason = (String) session.getAttribute(SESSION_LOAN_REASON);
		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		DecimalFormat df = new DecimalFormat("#.00");

		Context context = new Context();

		context.setVariable("userName", safeStr(user.getName()));
		context.setVariable("bankName", banks != null ? safeStr(banks.getBankName()) : "			");
		context.setVariable("loanAmount", df.format(safeDouble(loanamt)));
		context.setVariable("ticketNumber", safeStr(user.getUserId()));
		context.setVariable("Address", safeStr(user.getCurrentAddr()));
		context.setVariable("companyName", safeStr(company.getCompanyName()));
		context.setVariable("osLoan", safeDouble(dueLoan));
		context.setVariable("dateOfLastLoan",
				(lloan != null && lloan.getLoanApproveDate() != null) ? dateFormat.format(lloan.getLoanApproveDate())
						: "			");
		context.setVariable("loanTaken", lloan != null ? df.format(safeDouble(lloan.getLoanAmount())) : "0");
		context.setVariable("noOfEmiPaid", lloan != null ? safeStr(lloan.getEmiPaid()) : "0");
		context.setVariable("amountRecovered", lloan != null ? df.format(safeDouble(lloan.getLoanRepaid())) : "0");
		context.setVariable("loanOs", lloan != null ? df.format(safeDouble(lloan.getLoanBalance())) : "0");

		context.setVariable("cdBalance",
				cdShareDeductions != null ? df.format(safeDouble(cdShareDeductions.getCdDeductionTotal())) : "0");

		context.setVariable("monthlyInstalment", df.format(safeDouble(instalmentAmt)));
		context.setVariable("interestRate", loanInfo != null ? safeStr(loanInfo.getInterestOnCommodity()) : "			");
		context.setVariable("deptNo", safeStr(user.getBcNumber()));
		context.setVariable("mobileNumber", safeStr(user.getPhoneNum()));
		context.setVariable("email", safeStr(user.getEmail()));
		context.setVariable("accountNumber", banks != null ? safeStr(banks.getAccountNum()) : "				");
		context.setVariable("branch", banks != null ? safeStr(banks.getBranchName()) : "");
		context.setVariable("ifsc", banks != null ? safeStr(banks.getIfscCode()) : "				");
		context.setVariable("eligible", loanInfo != null ? df.format(safeDouble(loanInfo.getMaxCommodityLoan())) : "0");
		context.setVariable("fathersname", safeStr(user.getFathersName()));

		context.setVariable("sharebalance",
				cdShareDeductions != null ? df.format(safeDouble(cdShareDeductions.getShareDeductionTotal())) : "0");

		context.setVariable("dos",
				user.getDateOfRetirement() != null ? dateFormat.format(user.getDateOfRetirement()) : "				");
		context.setVariable("serviceperiod", user.getSerivcePeriod() != ""? safeStr(user.getSerivcePeriod()):"              ");
		context.setVariable("loanReason", safeStr(loanReason));

		String imagePath = "";
		try {
			imagePath = getClass().getResource("/static/dist/images/company_logo.png").toExternalForm();
		} catch (Exception e) {
			System.out.println(e);
		}
		context.setVariable("logoPath", imagePath);

		byte[] pdfBytes = pdfService.generatePdf("loan_application_commodity", context);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=loan_application.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	// Helper method to avoid nulls
	String safeStr(Object val) {
		return val != null ? val.toString() : "";
	}

	double safeDouble(Double val) {
		return val != null ? val : 0.0;
	}

}