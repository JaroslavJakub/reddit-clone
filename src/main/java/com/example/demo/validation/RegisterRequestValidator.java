package com.example.demo.validation;

import org.springframework.stereotype.Component;

import com.example.demo.dto.RegisterRequest;

@Component
public class RegisterRequestValidator {
	
	public boolean isValid(RegisterRequest registerRequest) {
		
		if (registerRequest == null) {
			return false;
		}
		
		if (registerRequest.getPassword() == null || registerRequest.getPassword().equals("")) {
			return false;
		}
		
		if (registerRequest.getEmail() == null || registerRequest.getEmail().equals("")) {
			return false;
		}
		
		if (registerRequest.getUsername() == null || registerRequest.getUsername().equals("")) {
			return false;
		}
		
		return true;
	}
}
