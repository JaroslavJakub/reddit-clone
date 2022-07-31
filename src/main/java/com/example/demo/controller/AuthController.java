package com.example.demo.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.VerificationToken;
import com.example.demo.service.AuthService;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.validation.RegisterRequestValidator;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/api/auth")
@AllArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	private final RegisterRequestValidator registerRequestValidator;
	private final RefreshTokenService refreshTokenService;
	
	
	@PostMapping(path = "/signup")
	public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
		if (registerRequestValidator.isValid(registerRequest)) {
		authService.signup(registerRequest);
		return ResponseEntity.ok("User registration successful!");
		} else {
			return new ResponseEntity<String>("Invalid request body!!!", HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/accountVerification/{token}")
	public ResponseEntity<String> verifyAccount(@PathVariable String token) {
		authService.verifyAccount(token);
		return new ResponseEntity<String>("Verification successful - account activated!", HttpStatus.OK);
	}
	
	@PostMapping(path = "/login")
	public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
		return authService.login(loginRequest);	
	}
	
	@PostMapping(path = "/refresh/token")
	public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		return authService.refreshToken(refreshTokenRequest);
	}
	
	@PostMapping(path = "/logout")
	public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
		return ResponseEntity.status(HttpStatus.OK).body("Refresh Token deleted successfully!!");
	}
	
}
