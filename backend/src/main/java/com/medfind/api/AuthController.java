package com.medfind.api;

import com.medfind.api.ApiDtos.AuthResponse;
import com.medfind.api.ApiDtos.LoginRequest;
import com.medfind.api.ApiDtos.RegisterRequest;
import com.medfind.api.ApiDtos.UserResponse;
import com.medfind.model.AppUser;
import com.medfind.repository.UserRepository;
import com.medfind.security.JwtService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }
        AppUser user = users.save(new AppUser(request.name().trim(), email,
                passwordEncoder.encode(request.password()), "PATIENT", null));
        return response(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        AppUser user = users.findByEmailIgnoreCase(request.email().trim())
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect."));
        return response(user);
    }

    private AuthResponse response(AppUser user) {
        return new AuthResponse(jwtService.createToken(user),
                new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getPharmacyId()));
    }
}
