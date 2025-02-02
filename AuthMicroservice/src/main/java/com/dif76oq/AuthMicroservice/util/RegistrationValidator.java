package com.dif76oq.AuthMicroservice.util;

import com.dif76oq.AuthMicroservice.dto.RegistrationDTO;
import com.dif76oq.AuthMicroservice.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RegistrationValidator implements Validator {

    private final UserService userService;

    public RegistrationValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationDTO.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            RegistrationDTO registrationDTO = (RegistrationDTO) target;

            String email = registrationDTO.getEmail();
            String username = registrationDTO.getUsername();

            if (userService.findByEmail(email).isPresent()) {
                errors.rejectValue("email", "email.alreadyExists", "User with such email already exists");
            }

            if (userService.findByUsername(username).isPresent()) {
                errors.rejectValue("username", "username.alreadyExists", "Username is already taken");
            }
        }
    }

}
