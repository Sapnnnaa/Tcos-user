package com.telco.telco_co_app.serviceImpl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import com.telco.telco_co_app.dto.UserRegistrationDTO;
import com.telco.telco_co_app.dto.UserregisterDTO;
import com.telco.telco_co_app.model.AccountStatus;
import com.telco.telco_co_app.model.Banks;
import com.telco.telco_co_app.model.Nominiees;
import com.telco.telco_co_app.model.User;
import com.telco.telco_co_app.repository.BankRepository;
import com.telco.telco_co_app.repository.NominieesRepository;
import com.telco.telco_co_app.repository.UserRepository;
import com.telco.telco_co_app.service.LoanInfoService;
import com.telco.telco_co_app.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private NominieesRepository nomineeRepository;
	@Autowired
	private BankRepository bankrepository;

	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public String generateEmpID(int companyID, String userId) {
		Map<Integer, String> companyPrefixes = Map.of(
				101, "E0100",
				102, "ETCN0",
				103, "ETTL",
				104, "E0223"
		);

		String prefix = companyPrefixes.getOrDefault(companyID, "E0213");
		return prefix + userId;
	}

	@Override
	public User save(UserRegistrationDTO userRegistrationDTO) {
		User user = new User();
		user.setUserId(userRegistrationDTO.getUserId());
		user.setEmpId(generateEmpID(userRegistrationDTO.getCompanyId(), userRegistrationDTO.getUserId()));
//		user.setEmpId(userRegistrationDTO.getEmpId());
		user.setName(userRegistrationDTO.getName().toUpperCase());
		user.setBcNumber(userRegistrationDTO.getBcNumber());
		user.setGender(userRegistrationDTO.getGender());
		user.setFathersName(userRegistrationDTO.getFathersName().toUpperCase());
		user.setDob(userRegistrationDTO.getDob());
		user.setDateOfRetirement(userRegistrationDTO.getDateOfRetirement());
		user.setAadharNumber(userRegistrationDTO.getAadharNumber());
		user.setPanCard(userRegistrationDTO.getPanCard());
		if (userRegistrationDTO.getEmail() == null
				|| userRegistrationDTO.getEmail().trim().isEmpty()) {

			user.setEmail(null);

		} else {

			user.setEmail(userRegistrationDTO.getEmail().trim());
		}
		user.setPhoneNum(userRegistrationDTO.getPhoneNum());
		user.setWhatsappNumber(userRegistrationDTO.getWhatsappNumber());
		user.setSerivcePeriod(userRegistrationDTO.getSerivcePeriod());

		user.setPermanentAddr(userRegistrationDTO.getPermanentAddr());
		user.setPermanentPinCode(userRegistrationDTO.getPermanentPinCode());
		user.setPermanentState(userRegistrationDTO.getPermanentState());
		user.setPermanentCity(userRegistrationDTO.getPermanentCity());

		user.setCurrentAddr(userRegistrationDTO.getCurrentAddr());
		user.setCurrentPinCode(userRegistrationDTO.getCurrentPinCode());
		user.setCurrentState(userRegistrationDTO.getCurrentState());
		user.setCurrentCity(userRegistrationDTO.getCurrentCity());

		user.setAccountStatus(AccountStatus.PENDING);
		user.setCompanyId(userRegistrationDTO.getCompanyId());
		user.setPassword(userRegistrationDTO.getPassword());
		user.setCreatedAt(new Date());
		user.setAccountStatus(AccountStatus.PENDING);
		user.setCdCut(loanInfoService.findLoanInfo().getCdMinimum());
		user.setShareCut(4000);

		return userRepository.save(user);
	}

	@Override
	public List<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public boolean checkPassword(String userId, String rawPassword) {
		System.out.println("inside service");
		User user = null;
		try {
			user = findByUserId(userId);
		} catch (Exception e) {
			System.out.println(e);
		}

		// Handle null user explicitly
		if (user == null) {
			System.out.println("User not found for userId: " + userId);
			return false;
		}

		// Check if passwords match
		boolean isPasswordMatch = passwordEncoder.matches(rawPassword, user.getPassword());

		if (!isPasswordMatch) {
			System.out.println("Password mismatch for email: " + userId);
		}

		return isPasswordMatch;
	}

	@Override
	public boolean emailExists(String email) {
		return !userRepository.findByEmail(email).isEmpty(); // Assuming findByEmail returns a User or null
	}

	@Override
	public User findByUserId(String userId) {
		return userRepository.findByUserId(userId);
	}


	@Override
	public String findLastUserId() {
		return userRepository.findLastUserId();
	}

	@Override
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public boolean findByAadharNumber(String aadharNum) {
		return !userRepository.findByAadharNumber(aadharNum).isEmpty();
	}

	@Override
	public boolean findByPanCard(String panNum) {
		return !userRepository.findByPanCard(panNum).isEmpty();
	}
	
	@Override
	public boolean findByTicket(String userId) {
		return userRepository.findByUserId(userId)!=null;
	}
	
	@Override
	@Transactional
	public void newUser(UserregisterDTO userregisterDTO) {
	    Optional<User> existingUser = userRepository.findByUserIDs(userregisterDTO.getUserId());
	    
	    User user;
	    if (existingUser.isPresent()) {
	        // Update existing user
	        user = existingUser.get();
	        System.out.println("Updating existing user with ID: " + userregisterDTO.getUserId());
	    } else {
	        // Create new user
	        user = new User();
	        user.setUserId(userregisterDTO.getUserId());
	        // Set default values for new users
	        user.setCdCut(loanInfoService.findLoanInfo().getCdMinimum());
	        user.setShareCut(4000);
	        user.setCreatedAt(new Date());
	        System.out.println("Creating new user with ID: " + userregisterDTO.getUserId());
	    }

	    // Common fields for both new and existing users
		if (userregisterDTO.getEmail() == null
				|| userregisterDTO.getEmail().trim().isEmpty()) {

			user.setEmail(null);

		} else {

			user.setEmail(userregisterDTO.getEmail().trim());
		}
	    user.setDob(userregisterDTO.getDob());
	    user.setName(userregisterDTO.getName().toUpperCase());
	    user.setPhoneNum(userregisterDTO.getPhoneNum());
	    user.setWhatsappNumber(userregisterDTO.getWhatsappNumber());
	    user.setDateOfRetirement(userregisterDTO.getDor());
	    user.setGender(userregisterDTO.getGender());
	    user.setBcNumber(userregisterDTO.getBcNumber());
	    user.setEmpId(userregisterDTO.getEmpId());
	    user.setCompanyId(userregisterDTO.getCompanyId());
	    user.setPanCard(userregisterDTO.getPanCard());
	    user.setAadharNumber(userregisterDTO.getAadharNumber());
	    user.setSerivcePeriod(userregisterDTO.getSerivcePeriod());
	    user.setPassword(userregisterDTO.getPassword());
	    user.setUserType(userregisterDTO.getUserType());
	    user.setFathersName(userregisterDTO.getFathersName().toUpperCase());
	    user.setPermanentAddr(userregisterDTO.getPermanentAddr());
	    user.setPermanentCity(userregisterDTO.getPermanentCity());
	    user.setPermanentState(userregisterDTO.getPermanentState());
	    user.setPermanentPinCode(userregisterDTO.getPermanentPinCode());
	    user.setCurrentAddr(userregisterDTO.getCurrentAddr());
	    user.setCurrentCity(userregisterDTO.getCurrentCity());
	    user.setCurrentState(userregisterDTO.getCurrentState());
	    user.setCurrentPinCode(userregisterDTO.getCurrentPinCode());
	    user.setAccountStatus(userregisterDTO.getAccountStatus());
	    user.setApprovedAt(java.sql.Date.valueOf(LocalDate.now()));

	    // Save user first
	    user = userRepository.save(user);
	    System.out.println("User saved successfully with ID: " + user.getId());

	    // Handle Bank details - update existing or create new
	    try {
	        Banks bank = bankrepository.findByUserId(userregisterDTO.getUserId());
	        if (bank == null) {
	            // Create new bank record
	            bank = new Banks();
	            bank.setUserId(userregisterDTO.getUserId());
	            System.out.println("Creating new bank record for user: " + userregisterDTO.getUserId());
	        } else {
	            System.out.println("Updating existing bank record for user: " + userregisterDTO.getUserId());
	        }

	        // Set/Update bank details
	        bank.setBankName(userregisterDTO.getBname());
	        bank.setBranchName(userregisterDTO.getBranch());
	        bank.setAccountNum(userregisterDTO.getAccountNum());
	        bank.setIfscCode(userregisterDTO.getIfscode());
	        bank.setPayableAt(userregisterDTO.getPayableAt());
	        
	        bankrepository.save(bank);
	        System.out.println("Bank details saved successfully for user: " + userregisterDTO.getUserId());
	    } catch (Exception e) {
	        System.err.println("Error saving bank details: " + e.getMessage());
	        e.printStackTrace();
	        throw new RuntimeException("Failed to save bank details", e);
	    }

	    // Handle Nominee details - update existing or create new
	    try {
	        Nominiees nominee = nomineeRepository.findByUserId(userregisterDTO.getUserId());
	        if (nominee == null) {
	            // Create new nominee record
	            nominee = new Nominiees();
	            nominee.setUserId(userregisterDTO.getUserId());
	            System.out.println("Creating new nominee record for user: " + userregisterDTO.getUserId());
	        } else {
	            System.out.println("Updating existing nominee record for user: " + userregisterDTO.getUserId());
	        }

	        // Set/Update nominee details
	        nominee.setNomineeName(userregisterDTO.getNomineeName());
	        nominee.setRelation(userregisterDTO.getRelation());
	        nominee.setAadharNum(userregisterDTO.getAadharNum());
	        nominee.setMobileNum(userregisterDTO.getMobileNum());
	        nominee.setAddr(userregisterDTO.getNomineeAddress());
	        
	        nomineeRepository.save(nominee);
	        System.out.println("Nominee details saved successfully for user: " + userregisterDTO.getUserId());
	    } catch (Exception e) {
	        System.err.println("Error saving nominee details: " + e.getMessage());
	        e.printStackTrace();
	        throw new RuntimeException("Failed to save nominee details", e);
	    }
	}

	public void update(User user) {
		userRepository.save(user);
	}

	@Override
	public boolean checkIfUserExist(String userId) {
		
		if(userRepository.checkUserIdExist(userId)==1)
			return true;
		
		return false;
	}

	@Override
	public User findByUserIdAndAccountStatus(String userId) {
		
		return userRepository.findByUserIdAndAccountStatus(userId, AccountStatus.PENDING);
	}

}