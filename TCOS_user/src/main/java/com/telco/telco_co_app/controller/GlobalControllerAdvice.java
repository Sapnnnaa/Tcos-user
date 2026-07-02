package com.telco.telco_co_app.controller;

import com.telco.telco_co_app.model.User;
import com.telco.telco_co_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String userId = authentication.getName();
            User user = userService.findByUserId(userId);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("principal", userId); // Also add principal if needed
            }
        }
    }
}