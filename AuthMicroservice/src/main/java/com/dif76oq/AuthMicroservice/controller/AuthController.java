package com.dif76oq.AuthMicroservice.controller;

import com.dif76oq.AuthMicroservice.dto.LoginDTO;
import com.dif76oq.AuthMicroservice.dto.RegistrationDTO;
import com.dif76oq.AuthMicroservice.model.User;
import com.dif76oq.AuthMicroservice.service.AuthService;
import com.dif76oq.AuthMicroservice.util.RegistrationValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final RegistrationValidator registrationValidator;

    @Autowired
    public AuthController (AuthService authService, ModelMapper modelMapper, RegistrationValidator registrationValidator) {
        this.authService = authService;
        this.modelMapper = modelMapper;
        this.registrationValidator = registrationValidator;
    }



    @PostMapping("/signup")
    public ResponseEntity<List<String>> signup(@RequestBody @Valid RegistrationDTO registrationDTO,
                                         BindingResult bindingResult) {
        registrationValidator.validate(registrationDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();

            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            }
            for (ObjectError globalError : bindingResult.getGlobalErrors()) {
                errors.add(globalError.getObjectName() + ": " + globalError.getDefaultMessage());
            }

           return ResponseEntity.badRequest().body(errors);
        }

        User user = convertToUser(registrationDTO);
        String token = authService.signup(user);

        return new ResponseEntity<>(Collections.singletonList(token), HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<List<String>> signin(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();

            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            }
            for (ObjectError globalError : bindingResult.getGlobalErrors()) {
                errors.add(globalError.getObjectName() + ": " + globalError.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errors);
        }

        try {
            String token = authService.signin(loginDTO);
            return new ResponseEntity<>(Collections.singletonList(token), HttpStatus.OK);
        } catch (AccountNotFoundException | BadCredentialsException e) {
            return new ResponseEntity<>(Collections.singletonList(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam(name = "token") String token){
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @GetMapping("/resend")
    public ResponseEntity<String> resendToken(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals("anonymousUser")) {
            return ResponseEntity.badRequest().body("User not found");
        } else {
            return ResponseEntity.ok(authService.resendEmail(authentication.getName()));
        }
    }

    public User convertToUser(RegistrationDTO registrationDTO) {
        return this.modelMapper.map(registrationDTO, User.class);
    }
}
