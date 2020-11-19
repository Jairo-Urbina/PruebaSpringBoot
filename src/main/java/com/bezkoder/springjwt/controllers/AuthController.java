package com.bezkoder.springjwt.controllers;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.response.JwtResponse;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		boolean sigue = true;
		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		} catch (Exception e) {
			LOGGER.info("Usuario: "+loginRequest.getUsername()+"          --datos incorrectos en el logueo--");
			sigue = false;
		}
		if(sigue) {
		
		String jwt =null;
		UserDetailsImpl userDetails = null;
		
		try {
			SecurityContextHolder.getContext().setAuthentication(authentication);
			jwt = jwtUtils.generateJwtToken(authentication);

			userDetails = (UserDetailsImpl) authentication.getPrincipal();
		} catch (Exception e) {
			LOGGER.error("Usuario: "+loginRequest.getUsername()+"          --error--");
			sigue = false;
		}
		if(sigue) {
		LOGGER.debug("Usuario: "+loginRequest.getUsername()+"          --logueo exitoso--");
		}
		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername()));
		}
		return null;
	}

}
