package com.telco.telco_co_app.service;
import java.util.List;

import com.telco.telco_co_app.dto.UserRegistrationDTO;
import com.telco.telco_co_app.dto.UserregisterDTO;
import com.telco.telco_co_app.model.AccountStatus;
import com.telco.telco_co_app.model.User;

public interface UserService {
    List<User> findByEmail(String email);
    boolean checkPassword(String email, String rawPassword);
	User save(UserRegistrationDTO userRegistrationDTO);
	boolean emailExists(String email);
	User findByUserId(String userId);
//	User createUser(User user);
	String findLastUserId();
	User updateUser(User user);
	boolean findByAadharNumber(String aadharNum);
	boolean findByPanCard(String panNum);
	public void newUser(UserregisterDTO userregisterDTO);
	boolean findByTicket(String userId);
	public void update(User user);
	boolean checkIfUserExist(String userId);
	User findByUserIdAndAccountStatus(String userId);
}
