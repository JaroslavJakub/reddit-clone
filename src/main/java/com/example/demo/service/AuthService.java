package com.example.demo.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.model.NotificationEmail;
import com.example.demo.model.User;
import com.example.demo.model.VerificationToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.security.JwtProvider;

import static com.example.demo.util.Constants.ACTIVATION_EMAIL;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AuthService {
	
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final MailContentBuilder mailContentBuilder;
	private final MailService mailService;
	private final JwtProvider jwtProvider;
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenService refreshTokenService;
	
	
	@Transactional
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setCreated(Instant.now());
		user.setEnabled(false);
		
		userRepository.save(user);
		
		String token = generateVerificationToken(user);
//		String message = mailContentBuilder.build("Thank you for signing up to SpringReddit, please click on the below URL"
//													+ " to activate your account : " + ACTIVATION_EMAIL + "/" + token);
		
//		mailService.sendMail(new NotificationEmail("Please, activate your account", user.getEmail(), "Thank you for signing up to SpringReddit, please click on the below URL"
//				+ " to activate your account : " + "<a href='" + ACTIVATION_EMAIL + "/" + token + "'>Click on URL to activate your account.</a>"));
		
		String message = ACTIVATION_EMAIL + "/" + token;
		mailService.sendMail(new NotificationEmail("Please, activate your account", user.getEmail(), message));
	}
	
	public AuthenticationResponse login(LoginRequest loginRequest) {
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
		Authentication authenticate = authenticationManager.authenticate(upat);
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		String authenticationToken = jwtProvider.generateToken(authenticate);	
		return AuthenticationResponse.builder()
				.authenticationToken(authenticationToken)
				.username(loginRequest.getUsername().toString())
				.refreshToken(refreshTokenService.generateRefreshToken().getToken().toString())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
				.build();
	}
	
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
	
	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationTokenRepository.save(verificationToken);
		
		return token;
	}
	
	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}
	
	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
		verificationTokenOptional.orElseThrow(() -> new SpringRedditException("Invalid token!!"));
		fetchUserAndEnable(verificationTokenOptional.get());
	}
	
	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		String username = verificationToken.getUser().getUsername();
		User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User: " + username + "not found!!"));
		user.setEnabled(true);
		userRepository.save(user);
		
	}
	
	@Transactional(readOnly = true)
	public User getCurrentUser() {
		Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(principal.getSubject())
				.orElseThrow(()-> new UsernameNotFoundException(principal.getSubject()));
	}
	
	public boolean isLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
	}
	
}
