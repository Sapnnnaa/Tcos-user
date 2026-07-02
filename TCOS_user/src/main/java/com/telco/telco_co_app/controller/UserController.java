package com.telco.telco_co_app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.telco.telco_co_app.model.User;
import com.telco.telco_co_app.repository.UserRepository;
import com.telco.telco_co_app.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    private String otp = "";
    @Value("${my.api.key}")
    private String apiKey;//api key 

    private long otpGenerationTime = 0;
    private static final long OTP_VALIDITY_DURATION = 5 * 60 * 1000; // 5 minutes

    @GetMapping("/userlogin")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }
        return "login";
    }

    @PostMapping("/userlogin")
    public String checkPassword(@RequestParam("userId") String userId, @RequestParam("password") String password,
            HttpSession session, Model model) {
        boolean isPasswordValid = userService.checkPassword(userId, password);

        if (isPasswordValid) {
            session.setAttribute("userId", userId);
            User user = userService.findByUserId(userId);
            session.setAttribute("user", user);

            return "redirect:/user/profile";

        } else {
            model.addAttribute("errorMessage", "Invalid email or password.".toString());
            return "redirect:/userlogin";
        }
    }

    @GetMapping("/logout")
    public String logoutUser(@RequestParam(value = "logout", required = false) String logout, Model model, HttpSession session, HttpServletRequest request) {
        try {
            session.invalidate();
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error during logout.");
        }
        return "redirect:/userlogin";
    }

    @GetMapping("/forgotpassword")
    public String forgotPasswordPage() {
        return "forgot_password";
    }

    @GetMapping("/checkTicketNumber")
    public ResponseEntity<Map<String, String>> checkTicketNumber(@RequestParam("userId") String userId) {

        Map<String, String> response = new HashMap<String, String>();
        User usr = null;
        try {
            usr = userService.findByUserId(userId);
        } catch (Exception e) {
            System.out.println(e);
        }
        if (usr != null) {
            response.put("msg", "1");
            response.put("name", usr.getName());
            response.put("phonenum", Long.toString(usr.getPhoneNum()));
        } else {
            response.put("msg", "0");
        }

        return ResponseEntity.ok(response);
    }

    public static String generateOtp() { // random otp generator
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); //6-digit number
        return String.valueOf(otp);
    }

    @GetMapping("/resetpasswordotp")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestParam("ticketNumber") String ticketNumber,
            @RequestParam("newPassword") String newPassword) {
        
        System.out.println("Password reset request received for ticket: " + ticketNumber);
        
        Map<String, Object> response = new HashMap<>();
        
        // Input validation
        if (ticketNumber == null || ticketNumber.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Ticket number is required.");
            return ResponseEntity.badRequest().body(response);
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "New password is required.");
            return ResponseEntity.badRequest().body(response);
        }

        if (!isValidPassword(newPassword)) {
            response.put("success", false);
            response.put("message", "Password must meet security requirements.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            User user = userService.findByUserId(ticketNumber);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Invalid ticket number.");
                return ResponseEntity.badRequest().body(response);
            }

            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            User savedUser = userRepository.save(user);

            if (savedUser != null) {
                this.otp = ""; // Clear OTP after successful reset
                response.put("success", true);
                response.put("message", "Password reset successfully.");
                return ResponseEntity.ok().body(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to save new password.");
                return ResponseEntity.internalServerError().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Add password validation method
    private boolean isValidPassword(String password) {
        // At least 8 characters, contains uppercase, lowercase, digit, and special character
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    @GetMapping("/resetpasswordotpsend")
    public ResponseEntity<Map<String, Object>> passwordResetOtp(@RequestParam("number") String number) {
        Map<String, Object> responseMap = new HashMap<>();
        
        // Add input validation
        if (number == null || number.trim().isEmpty()) {
            responseMap.put("msg", false);
            responseMap.put("error", "Phone number is required");
            return ResponseEntity.badRequest().body(responseMap);
        }
        
        // Clean the phone number (remove spaces, special characters)
        String cleanNumber = number.replaceAll("[^0-9]", "");
        
        // Validate phone number format (assuming Indian format)
        if (cleanNumber.length() != 10) {
            responseMap.put("msg", false);
            responseMap.put("error", "Invalid phone number format");
            return ResponseEntity.badRequest().body(responseMap);
        }
        
        System.out.println("Attempting to send OTP to: " + cleanNumber);
        System.out.println("API Key configured: " + (apiKey != null && !apiKey.isEmpty()));
        
        OkHttpClient client = new OkHttpClient();

        this.otp = generateOtp();
        this.otpGenerationTime = System.currentTimeMillis();
        
        System.out.println("Generated OTP: " + this.otp); // Remove this in production

        String senderId = "TCOSMS";
        String message = "Your OTP for Password reset is " + this.otp
                + ". It is valid for 5 minutes. Please do not share it with anyone.\r\n"
                + "-Sigma e Solution Pvt. Ltd.";

        RequestBody formBody = new FormBody.Builder()
                .add("module", "TRANS_SMS")
                .add("apikey", apiKey)
                .add("to", cleanNumber)
                .add("from", senderId)
                .add("msg", message)
                .build();

        Request request = new Request.Builder()
                .url("https://2factor.in/API/R1/")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "No response body";
            System.out.println("SMS API Response Code: " + response.code());
            System.out.println("SMS API Response Body: " + responseBody);
            
            if (response.isSuccessful()) {
                // Check if the response body contains success indicators
                if (responseBody.contains("Success") || responseBody.contains("success") || 
                    response.code() == 200) {
                    responseMap.put("msg", true);
                    responseMap.put("message", "OTP sent successfully");
                } else {
                    responseMap.put("msg", false);
                    responseMap.put("error", "SMS service returned: " + responseBody);
                }
            } else {
                responseMap.put("msg", false);
                responseMap.put("error", "SMS API error: " + response.code() + " - " + responseBody);
            }
        } catch (Exception e) {
            System.err.println("Error sending OTP: " + e.getMessage());
            e.printStackTrace();
            responseMap.put("msg", false);
            responseMap.put("error", "Network error: " + e.getMessage());
        }

        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/checkresetpasswordotp")
    public ResponseEntity<Map<String, Boolean>> checkOtp(@RequestParam("otp") String receivedOtp) {
        Map<String, Boolean> responseMap = new HashMap<>();

        // Check if OTP has expired
        if (System.currentTimeMillis() - otpGenerationTime > OTP_VALIDITY_DURATION) {
            responseMap.put("msg", false);
            responseMap.put("expired", true);
            return ResponseEntity.ok(responseMap);
        }

        if (receivedOtp != null && receivedOtp.equals(this.otp)) {
            responseMap.put("msg", true);
        } else {
            responseMap.put("msg", false);
        }

        return ResponseEntity.ok(responseMap);
    }
}