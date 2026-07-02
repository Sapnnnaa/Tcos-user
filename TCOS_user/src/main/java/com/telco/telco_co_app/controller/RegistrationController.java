package com.telco.telco_co_app.controller;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.thymeleaf.context.Context;
import org.springframework.core.io.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.telco.telco_co_app.controller.useridgenerator.UserIdGenerator;
import com.telco.telco_co_app.dto.UserregisterDTO;
import com.telco.telco_co_app.model.AccountStatus;
import com.telco.telco_co_app.model.Banks;
import com.telco.telco_co_app.model.Company;
import com.telco.telco_co_app.model.Nominiees;
import com.telco.telco_co_app.model.User;
import com.telco.telco_co_app.service.BankService;
import com.telco.telco_co_app.service.CompanyService;
import com.telco.telco_co_app.service.NominieesService;
import com.telco.telco_co_app.service.PdfService;
import com.telco.telco_co_app.service.UserService;

import jakarta.servlet.http.HttpSession;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Controller
@SessionAttributes({ "tempUserData" })
public class RegistrationController {

	@Autowired
	private UserService userService;

	@Autowired
	private BankService bankService;

	@Autowired
	private NominieesService nominieesService;

	@Autowired
	private PdfService pdfService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CompanyService companyService;

	@Value("${my.api.key}")
	private String apiKey;

	@GetMapping("/user-email/{email}")
	public ResponseEntity<Map<String, Boolean>> checkEmailExists(@PathVariable("email") String email,
			HttpSession session) {
		boolean exists = userService.emailExists(email);
		Map<String, Boolean> response = new HashMap<>();
		response.put("exists", exists);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/user-adrpan")
	public ResponseEntity<Map<String, Boolean>> checkAadrPanUnique(@RequestParam("aadhar") String aadhar,
			@RequestParam("pan") String pan, HttpSession session) {

		Map<String, Boolean> response = new HashMap<>();

		response.put("aadhar", userService.findByAadharNumber(aadhar));
		response.put("pan", userService.findByPanCard(pan));

		return ResponseEntity.ok(response);
	}

	public static String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	public static CompletableFuture<String> generateOtpAsync() {
		return CompletableFuture.supplyAsync(() -> generateOtp());
	}

	@GetMapping("/sendOtp")
	public ResponseEntity<Map<String, Boolean>> sendOtp(@RequestParam("phoneNum") String phno, HttpSession session) {

		Map<String, Boolean> responseMap = new HashMap<>();
		OkHttpClient client = new OkHttpClient();

		// clear any existing OTP data before generating new one
		session.removeAttribute("registrationOtp");
		session.removeAttribute("otpGeneratedAt");
		session.removeAttribute("otpVerified");

		// generate OTP async
		String generatedOtp;
		try {
			generatedOtp = generateOtpAsync().get(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			responseMap.put("msg", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
		}

		session.setAttribute("registrationOtp", generatedOtp);
		session.setAttribute("otpGeneratedAt", System.currentTimeMillis());

		String senderId = "TCOSMS";
		String message = "Your OTP for mobile number verification is " + generatedOtp
				+ ". It is valid for 5 minutes. Please do not share it with anyone.\r\n" + "\r\n"
				+ "-Sigma e Solution Pvt. Ltd.";

		RequestBody formBody = new FormBody.Builder().add("module", "TRANS_SMS").add("apikey", apiKey).add("to", phno)
				.add("from", senderId).add("msg", message).build();

		Request request = new Request.Builder().url("https://2factor.in/API/R1/").post(formBody).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				responseMap.put("msg", true);
//				System.out.println("OTP sent successfully to " + phno + ". OTP: " + generatedOtp);
			} else {
				responseMap.put("msg", false);
				System.out.println("Failed to send OTP to " + phno);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseMap.put("msg", false);
		}

		return ResponseEntity.ok(responseMap);
	}

	@GetMapping("/confirm-otp")
	public ResponseEntity<Map<String, Boolean>> checkOtp(@RequestParam("otp") String receivedOtp, HttpSession session) {

		Map<String, Boolean> responseMap = new HashMap<>();
		String sessionOtp = (String) session.getAttribute("registrationOtp");
		Long otpGeneratedAt = (Long) session.getAttribute("otpGeneratedAt");

		// check if OTP exists in session
		if (sessionOtp == null) {
			responseMap.put("msg", false);
			return ResponseEntity.ok(responseMap);
		}

		// check OTP expiry (5 minutes = 300000ms)
		if (otpGeneratedAt != null) {
			long currentTime = System.currentTimeMillis();
			long timeDifference = currentTime - otpGeneratedAt;
			if (timeDifference > 300000) { // 5 minutes
				responseMap.put("msg", false);
				// Clear expired OTP from session
				session.removeAttribute("registrationOtp");
				session.removeAttribute("otpGeneratedAt");
				return ResponseEntity.ok(responseMap);
			}
		}

		// Verify OTP
		if (receivedOtp.equals(sessionOtp)) {
			responseMap.put("msg", true);
			session.setAttribute("otpVerified", true);

			// Optional: Remove OTP from session after successful verification
			session.removeAttribute("registrationOtp");
			session.removeAttribute("otpGeneratedAt");

//			System.out.println("OTP verified successfully for session: " + session.getId()); // For debugging
		} else {
			responseMap.put("msg", false);
			System.out.println("OTP verification failed. Expected: " + sessionOtp + ", Received: " + receivedOtp); // For
																													// debugging
		}

		return ResponseEntity.ok(responseMap);
	}

	@GetMapping("/pdf-view/{email}")
	public ResponseEntity<InputStreamResource> viewPdf(@PathVariable("email") String email, HttpSession session) {
		try {
			String pdfPath = "src/main/resources/generated_pdfs/" + email + "_registration.pdf";
			File pdfFile = new File(pdfPath);

			if (!pdfFile.exists()) {
				return ResponseEntity.notFound().build();
			}

			InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + pdfFile.getName())
					.contentType(MediaType.APPLICATION_PDF).contentLength(pdfFile.length()).body(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/ticketnum/{ticketNumber}")
	public ResponseEntity<Map<String, Boolean>> validateTicket(@PathVariable("ticketNumber") String userId,
			HttpSession session) {

		Map<String, Boolean> response = new HashMap<>();

		response.put("exists", userService.findByTicket(userId));

		return ResponseEntity.ok(response);
	}

	@GetMapping("/telco_co/register")
	public String showRegistrationForm(Model model, HttpSession session) {

		session.invalidate();

		List<Company> companies = companyService.getAllCompanies();
		model.addAttribute("companyList", companies);
		model.addAttribute("UserregisterDTO", new UserregisterDTO());

		return "register";
	}

	@GetMapping("/telco_co/registerOld")
	public String showRegistrationFormOldMember(Model model, HttpSession session) {

		session.invalidate();

		List<Company> companies = companyService.getAllCompanies();
		model.addAttribute("companyList", companies);
		model.addAttribute("UserregisterDTO", new UserregisterDTO());

		return "registerOld";
	}

	@PostMapping("/telco_co/registerOld")
	public ResponseEntity<byte[]> registerUserAccountOld(
			@ModelAttribute("UserregisterDTO") UserregisterDTO userregisterDTOS, Model model, HttpSession session) {

		// Check if OTP was verified (optional security check)
		Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
		if (otpVerified == null || !otpVerified) {
			System.out.println("Warning: Registration attempted without OTP verification");
		}

		try {

			UserIdGenerator generator = new UserIdGenerator();
			userregisterDTOS.setAccountStatus(AccountStatus.PENDING);
			userregisterDTOS.setUserType("Existing Member");
			String encryptedPassword = passwordEncoder.encode(userregisterDTOS.getPassword());
			userregisterDTOS.setPassword(encryptedPassword);
			userregisterDTOS.setEmpId(
					generator.generateNextUserId(userregisterDTOS.getUserId(), userregisterDTOS.getCompanyId()));
			userService.newUser(userregisterDTOS);
			model.addAttribute("successMessage", "User saved successfully!");

			Context context = new Context();
			Company company = companyService.findbyCompanyId(userregisterDTOS.getCompanyId());

			try {
				userregisterDTOS.getEmail();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

				context.setVariable("date", userregisterDTOS.getCreatedAt());
				context.setVariable("ticketNumber", userregisterDTOS.getUserId());
				context.setVariable("EMP_ID", userregisterDTOS.getEmpId());
				context.setVariable("NAME", userregisterDTOS.getName());
				context.setVariable("BC_NUMBER", userregisterDTOS.getBcNumber());
				context.setVariable("GENDER", userregisterDTOS.getGender());
				context.setVariable("FATHERS_NAME", userregisterDTOS.getFathersName());
				context.setVariable("DOB", dateFormat.format(userregisterDTOS.getDob()));
				context.setVariable("DATE_OF_RETIREMENT", dateFormat.format(userregisterDTOS.getDor()));
				context.setVariable("AADHAR_NUMBER", userregisterDTOS.getAadharNumber());
				context.setVariable("PAN_CARD", userregisterDTOS.getPanCard());
				context.setVariable("EMAIL", userregisterDTOS.getEmail());
				context.setVariable("PHONE_NUM", String.valueOf(userregisterDTOS.getPhoneNum()));
				context.setVariable("WHATSAPP_NUMBER", String.valueOf(userregisterDTOS.getWhatsappNumber()));
				context.setVariable("SERVICE_PERIOD", userregisterDTOS.getSerivcePeriod());
				context.setVariable("DEPARTMENT_ID", userregisterDTOS.getBcNumber());
				context.setVariable("employeer", company.getCompanyName());

				// Permanent Address
				context.setVariable("PERMANENT_ADDR", userregisterDTOS.getPermanentAddr());
				context.setVariable("PERMANENT_STATE", userregisterDTOS.getPermanentState());
				context.setVariable("PERMANENT_PIN_CODE", userregisterDTOS.getPermanentPinCode());
				context.setVariable("PERMANENT_CITY", userregisterDTOS.getPermanentCity());

				// Current Address
				context.setVariable("CURRENT_ADDR", userregisterDTOS.getCurrentAddr());
				context.setVariable("CURRENT_STATE", userregisterDTOS.getCurrentState());
				context.setVariable("CURRENT_PIN_CODE", userregisterDTOS.getCurrentPinCode());
				context.setVariable("CURRENT_CITY", userregisterDTOS.getCurrentCity());

				// Bank details
				context.setVariable("ACCOUNT_NUM", userregisterDTOS.getAccountNum());
				context.setVariable("BANK_NAME", userregisterDTOS.getBname());
				context.setVariable("BRANCH_NAME", userregisterDTOS.getBranch());
				context.setVariable("IFSC_CODE", userregisterDTOS.getIfscode());

				// Nominee details
				context.setVariable("NOMINEE_NAME", userregisterDTOS.getNomineeName());
				context.setVariable("NOMINEE_RELATION", userregisterDTOS.getRelation());
				context.setVariable("NOMINEE_ADDR", userregisterDTOS.getNomineeAddress());
				context.setVariable("NOMINEE_AADHAR_NUM", userregisterDTOS.getAadharNum());
				context.setVariable("NOMINEE_MOBILE_NUM", userregisterDTOS.getMobileNum());

				// Metadata
				context.setVariable("COMPANY_ID", userregisterDTOS.getCompanyId());
				context.setVariable("PASSWORD", userregisterDTOS.getPassword());
				context.setVariable("CREATED_AT", userregisterDTOS.getCreatedAt());

				String imagePath = getClass().getResource("/static/dist/images/company_logo.png").toExternalForm();
				context.setVariable("logoPath", imagePath);

				String pdfUrl = "/generated_pdfs/" + userregisterDTOS.getEmail() + "_registration.pdf";
				model.addAttribute("pdfUrl", pdfUrl);

			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("error", "Error generating PDF.");
			}
			
			
			byte[] pdfBytes = pdfService.generatePdf("registrationpdfold", context);

			
			// Clean up session attributes
			session.removeAttribute("registrationOtp");
			session.removeAttribute("otpVerified");
			session.removeAttribute("otpGeneratedAt");
			session.removeAttribute("tempUserData");

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registration_application.pdf")
					.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);

		} catch (Exception e) {
			System.err.println("Registration failed: " + e.getMessage());
	        e.printStackTrace();
	        // Return appropriate error response
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GetMapping("/user-data/{userId}")
	public ResponseEntity<Map<String, Object>> getUserData(@PathVariable("userId") String userId, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		try {
			// Call service to get user entity by userId
			User user = userService.findByUserIdAndAccountStatus(userId);

			if (user != null) {
				// Convert User entity to UserregisterDTO
				UserregisterDTO userData = convertUserToDTO(user);

				// Fetch Banks data for this user
				Banks bankData = bankService.getByUserId(userId);
				if (bankData != null) {
					populateBankData(userData, bankData);
				}

				// Fetch Nominees data for this user
				Nominiees nomineeData = nominieesService.findByUserId(userId);
				if (nomineeData != null) {
					populateNomineeData(userData, nomineeData);
				}

				response.put("exists", true);
				response.put("data", userData);
			} else {
				response.put("exists", false);
				response.put("message", "No data found for this ticket number");
			}

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("exists", false);
			response.put("message", "Error retrieving user data");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Helper method to populate bank data
	private void populateBankData(UserregisterDTO dto, Banks bankData) {
		dto.setAccountNum(bankData.getAccountNum());
		dto.setBname(bankData.getBankName());
		dto.setBranch(bankData.getBranchName());
		dto.setIfscode(bankData.getIfscCode());
		dto.setPayableAt(bankData.getPayableAt());
	}

	// Helper method to populate nominee data
	private void populateNomineeData(UserregisterDTO dto, Nominiees nomineeData) {
		dto.setNomineeName(nomineeData.getNomineeName());
		dto.setRelation(nomineeData.getRelation());
		dto.setNomineeAddress(nomineeData.getAddr());
		dto.setAadharNum(nomineeData.getAadharNum());
		dto.setMobileNum(nomineeData.getMobileNum());
	}


	private UserregisterDTO convertUserToDTO(User user) {
		UserregisterDTO dto = new UserregisterDTO();

		// Basic Information
		dto.setId(user.getId()); // Important: Include the primary key for updates
		dto.setUserId(user.getUserId());
		dto.setEmpId(user.getEmpId());
		dto.setName(user.getName());
		dto.setBcNumber(user.getBcNumber());
		dto.setGender(user.getGender());
		dto.setFathersName(user.getFathersName());
		dto.setDob(user.getDob());
		dto.setDor(user.getDateOfRetirement());
		dto.setAadharNumber(user.getAadharNumber());
		dto.setPanCard(user.getPanCard());
		dto.setEmail(user.getEmail());
		dto.setPhoneNum(user.getPhoneNum());
		dto.setWhatsappNumber(user.getWhatsappNumber());
		dto.setSerivcePeriod(user.getSerivcePeriod());

		// Address Information
		dto.setPermanentAddr(user.getPermanentAddr());
		dto.setPermanentCity(user.getPermanentCity());
		dto.setPermanentState(user.getPermanentState());
		dto.setPermanentPinCode(user.getPermanentPinCode());
		dto.setCurrentAddr(user.getCurrentAddr());
		dto.setCurrentCity(user.getCurrentCity());
		dto.setCurrentState(user.getCurrentState());
		dto.setCurrentPinCode(user.getCurrentPinCode());

		// Company and Status
		dto.setCompanyId(user.getCompanyId());
		dto.setAccountStatus(user.getAccountStatus());
		dto.setUserType(user.getUserType());
		dto.setCreatedAt(user.getCreatedAt());

		return dto;
	}

	@PostMapping("/telco_co/register")
	public ResponseEntity<byte[]> registerUserAccount(
			@ModelAttribute("UserregisterDTO") UserregisterDTO userregisterDTOS, Model model, HttpSession session) {

		// Check if OTP was verified
		Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
		if (otpVerified == null || !otpVerified) {
			// Handle case where OTP was not verified
			System.out.println("Warning: Registration attempted without OTP verification");
		}

		UserIdGenerator generator = new UserIdGenerator();
		userregisterDTOS.setAccountStatus(AccountStatus.PENDING);
		userregisterDTOS.setUserType("New Member");
		String encryptedPassword = passwordEncoder.encode(userregisterDTOS.getPassword());
		userregisterDTOS.setPassword(encryptedPassword);
		userregisterDTOS
				.setEmpId(generator.generateNextUserId(userregisterDTOS.getUserId(), userregisterDTOS.getCompanyId()));
		userService.newUser(userregisterDTOS);
		model.addAttribute("successMessage", "User saved successfully!");

		Context context = new Context();
		Company company = companyService.findbyCompanyId(userregisterDTOS.getCompanyId());

		try {
			userregisterDTOS.getEmail();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

			context.setVariable("date", userregisterDTOS.getCreatedAt());
			context.setVariable("ticketNumber", userregisterDTOS.getUserId());
			context.setVariable("EMP_ID", userregisterDTOS.getEmpId());
			context.setVariable("NAME", userregisterDTOS.getName());
			context.setVariable("BC_NUMBER", userregisterDTOS.getBcNumber());
			context.setVariable("GENDER", userregisterDTOS.getGender());
			context.setVariable("FATHERS_NAME", userregisterDTOS.getFathersName());
			context.setVariable("DOB", dateFormat.format(userregisterDTOS.getDob()));
			context.setVariable("DATE_OF_RETIREMENT", dateFormat.format(userregisterDTOS.getDor()));
			context.setVariable("AADHAR_NUMBER", userregisterDTOS.getAadharNumber());
			context.setVariable("PAN_CARD", userregisterDTOS.getPanCard());
			context.setVariable("EMAIL", userregisterDTOS.getEmail());
			context.setVariable("PHONE_NUM", String.valueOf(userregisterDTOS.getPhoneNum()));
			context.setVariable("WHATSAPP_NUMBER", String.valueOf(userregisterDTOS.getWhatsappNumber()));
			context.setVariable("SERVICE_PERIOD", userregisterDTOS.getSerivcePeriod());
			context.setVariable("DEPARTMENT_ID", userregisterDTOS.getBcNumber());
			context.setVariable("employeer", company.getCompanyName());

			// Permanent Address
			context.setVariable("PERMANENT_ADDR", userregisterDTOS.getPermanentAddr());
			context.setVariable("PERMANENT_STATE", userregisterDTOS.getPermanentState());
			context.setVariable("PERMANENT_PIN_CODE", userregisterDTOS.getPermanentPinCode());
			context.setVariable("PERMANENT_CITY", userregisterDTOS.getPermanentCity());

			// Current Address
			context.setVariable("CURRENT_ADDR", userregisterDTOS.getCurrentAddr());
			context.setVariable("CURRENT_STATE", userregisterDTOS.getCurrentState());
			context.setVariable("CURRENT_PIN_CODE", userregisterDTOS.getCurrentPinCode());
			context.setVariable("CURRENT_CITY", userregisterDTOS.getCurrentCity());

			// Bank details
			context.setVariable("ACCOUNT_NUM", userregisterDTOS.getAccountNum());
			context.setVariable("BANK_NAME", userregisterDTOS.getBname());
			context.setVariable("BRANCH_NAME", userregisterDTOS.getBranch());
			context.setVariable("IFSC_CODE", userregisterDTOS.getIfscode());

			// Nominee details
			context.setVariable("NOMINEE_NAME", userregisterDTOS.getNomineeName());
			context.setVariable("NOMINEE_RELATION", userregisterDTOS.getRelation());
			context.setVariable("NOMINEE_ADDR", userregisterDTOS.getNomineeAddress());
			context.setVariable("NOMINEE_AADHAR_NUM", userregisterDTOS.getAadharNum());
			context.setVariable("NOMINEE_MOBILE_NUM", userregisterDTOS.getMobileNum());

			// Metadata
			context.setVariable("COMPANY_ID", userregisterDTOS.getCompanyId());
			context.setVariable("PASSWORD", userregisterDTOS.getPassword());
			context.setVariable("CREATED_AT", userregisterDTOS.getCreatedAt());

			String imagePath = getClass().getResource("/static/dist/images/company_logo.png").toExternalForm();
			context.setVariable("logoPath", imagePath);

			String pdfUrl = "/generated_pdfs/" + userregisterDTOS.getEmail() + "_registration.pdf";
			model.addAttribute("pdfUrl", pdfUrl);

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error generating PDF.");
		}

		byte[] pdfBytes = pdfService.generatePdf("registrationpdf", context);

		// Clean up session attributes
		session.removeAttribute("registrationOtp");
		session.removeAttribute("otpVerified");
		session.removeAttribute("otpGeneratedAt");
		session.removeAttribute("tempUserData");

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registration_application.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	@GetMapping("/telco_co/offlineform")
	public ResponseEntity<Resource> downloadPdf(HttpSession session) throws IOException {
		ClassPathResource pdfFile = new ClassPathResource("templates/Membership_offline.pdf");

		if (!pdfFile.exists()) {
			throw new FileNotFoundException("PDF file not found in classpath");
		}

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Membership_offline.pdf\"")
				.body(pdfFile);
	}
}