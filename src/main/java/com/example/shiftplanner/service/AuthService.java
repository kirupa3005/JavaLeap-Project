package com.example.shiftplanner.service;

import com.example.shiftplanner.dto.LoginRequest;
import com.example.shiftplanner.dto.LoginResponse;
import com.example.shiftplanner.dto.RegisterRequest;
import com.example.shiftplanner.entity.Employee;
import com.example.shiftplanner.exception.BusinessException;
import com.example.shiftplanner.repository.EmployeeRepository;
import com.example.shiftplanner.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(EmployeeRepository employeeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse register(RegisterRequest request) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already registered: " + request.getEmail());
        }

        // Create new employee with BCrypt hashed password
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setPhone(request.getPhone());
        employee.setRole(request.getRole());

        Employee savedEmployee = employeeRepository.save(employee);

        // Generate JWT token
        String token = jwtService.generateToken(savedEmployee);
        return new LoginResponse(token, "User registered successfully");
    }

    public LoginResponse login(LoginRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Verify password with BCrypt
        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(employee);
        return new LoginResponse(token, "Login successful");
    }
}
