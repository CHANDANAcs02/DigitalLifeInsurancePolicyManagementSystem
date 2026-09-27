package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.controller;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.CustomerResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.LoginRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.RegisterRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.UserResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.User;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.InvalidCredentialsException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.UserRepository;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.security.JwtService;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service.CustomerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public CustomerResponse register(@Valid @RequestBody RegisterRequest request) {
        return customerService.register(request);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(), request.password()));

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().name(),
                    jwtService.generateToken(user));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new InvalidCredentialsException("Authenticated user not found"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                null);
    }
}
