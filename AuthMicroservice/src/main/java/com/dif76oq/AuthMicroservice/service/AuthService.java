package com.dif76oq.AuthMicroservice.service;

import com.dif76oq.AuthMicroservice.dto.JwtDTO;
import com.dif76oq.AuthMicroservice.dto.LoginDTO;
import com.dif76oq.AuthMicroservice.exception.verificationEmail.VerificationTokenExpiredException;
import com.dif76oq.AuthMicroservice.exception.verificationEmail.VerificationTokenNotFoundException;
import com.dif76oq.AuthMicroservice.model.Role;
import com.dif76oq.AuthMicroservice.model.User;
import com.dif76oq.AuthMicroservice.repository.RoleRepository;
import com.dif76oq.AuthMicroservice.repository.UserRepository;
import com.dif76oq.AuthMicroservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, VerificationTokenService verificationTokenService, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }


    public String signup(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(user.getCreatedAt());
        user.setBalance(0);
        user.setEnabled(false);
        user.setRoles(List.of(roleRepository.findByName("USER")));
        User savedUser = userRepository.save(user);

        String verificationToken = verificationTokenService.generateVerificationToken(user);

        //Send verificationToken
        System.out.println(verificationToken);
        try {
            emailService.sendEmail(user.getEmail(),
                    "CourseHub - Email verification.",
                    "To complete registration, confirm your email by clicking on the link: http://localhost:8080/auth/verify?token=" + verificationToken);
        } catch (Exception e) {
            return e.getMessage();
        }

        //JWT generation
        JwtDTO jwtDTO = new JwtDTO(savedUser.getId(), savedUser.getUsername(),
                savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        String jwtToken = jwtUtil.generateToken(jwtDTO);
        return jwtToken;
    }

    public String signin(LoginDTO loginDTO) throws AccountNotFoundException, BadCredentialsException {
        String identifier = loginDTO.getIdentifier();
        String password = loginDTO.getPassword();

        Optional<User> user = userRepository.findByUsername(identifier);

        if (user.isEmpty()) {
            user = userRepository.findByEmail(identifier);
        }

        if (user.isEmpty()) {
            throw new AccountNotFoundException("User with this identifier not found!");
        }

        if (passwordEncoder.matches(password, user.get().getPassword())) {

            JwtDTO jwtDTO = new JwtDTO(user.get().getId(), user.get().getUsername(),
                    user.get().getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList()));
            String token = jwtUtil.generateToken(jwtDTO);
            return token;
        } else {
            throw new BadCredentialsException("Wrong password!");
        }
    }

    public String verifyEmail(String token) {
        try {
            int id = verificationTokenService.confirmRegistration(token);
            User user = userRepository.findById(id).get();
            user.setEnabled(true);
            userRepository.save(user);
            return "Email confirmed successfully!";
        } catch (VerificationTokenNotFoundException | VerificationTokenExpiredException e) {
            return e.getMessage();
        }
    }

    public String resendEmail(String username) {

        User user = userRepository.findByUsername(username).get();

        if (!user.getEnabled()) {
            String verificationToken = verificationTokenService.generateVerificationToken(user);

            //Send verificationToken
            System.out.println(verificationToken);
            try {
                emailService.sendEmail(user.getEmail(),
                        "CourseHub - New verification link.",
                        "Use this new link to complete registration: http://localhost:8080/auth/verify?token=" + verificationToken);
            } catch (Exception e) {
                return e.getMessage();
            }

            return "New link sent to your mail!";
        } else {
            return "Email already confirmed!";
        }
    }
}
