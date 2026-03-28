package com.smartbiz.auth;

import com.smartbiz.auth.dto.AuthResponse;
import com.smartbiz.auth.dto.LoginRequest;
import com.smartbiz.auth.dto.RegisterRequest;
import com.smartbiz.security.JwtService;
import com.smartbiz.user.Role;
import com.smartbiz.user.UserEntity;
import com.smartbiz.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
//@RequiredArgsConstructor
public class AuthService {

    @Autowired(required = false)
    private UserRepository userRepo;
    //    private final UserRepository userRepo;
    @Autowired(required = false)
    private JwtService jwtService;
    @Autowired(required = false)
    private PasswordEncoder encoder;
    @Autowired(required = false)
    private AuthenticationManager authManager;

    public void register(RegisterRequest registerReq) {
        if (userRepo.existsByEmail(registerReq.email()))
            throw new IllegalArgumentException("Email Already Registered.");

        var user = UserEntity.builder().email(registerReq.email()).password(encoder.encode(registerReq.password()))
                .role(Role.USER).build();

        userRepo.save(user);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        var auth = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        authManager.authenticate(auth);// throw exception if bad credentials

        var user = (UserEntity) authManager.authenticate(auth).getPrincipal();
        var token = jwtService.generateToken(user.getUsername(), Map.of("role", user.getRole().name()));

        return new AuthResponse(token, "Bearer", 30 * 60);
    }

}
