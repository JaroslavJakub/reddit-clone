package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtEncoder jwtEncoder;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return generateTokenWithUserName(principal.getUsername());
    }

    public String generateTokenWithUserName(String username) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtExpirationInMillis))
                .subject(username)
                .claim("scope", "ROLE_USER")
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}
























//package com.example.demo.security;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.PrivateKey;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.CertificateException;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.stereotype.Service;
//
//import com.example.demo.exception.SpringRedditException;
//
//import io.jsonwebtoken.Jwts;
//
//@Service
//public class JwtProvider {
//	
//	private KeyStore keyStore;
//	
//	@PostConstruct
//	public void init() {
//		try {
//			keyStore = KeyStore.getInstance("JKS");
//			InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
//			keyStore.load(resourceAsStream, "secret".toCharArray());
//		} catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {	
//			throw new SpringRedditException("Exception occured while loading keystore");
//		}
//	}
//	
//	public String generateToken(Authentication authentication) {
//		User principal = (User) authentication.getPrincipal();
//		return Jwts.builder()
//				.setSubject(principal.getUsername())
//				.signWith(getPrivateKey())
//				.compact();
//	}
//	
//	private PrivateKey getPrivateKey() {
//		try {
//			return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
//		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e){
//			throw new SpringRedditException("Exception occured while retrieving public key from KeyStore");
//		}
//	}
//
//}
