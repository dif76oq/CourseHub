package com.dif76oq.AuthMicroservice.service;

import com.dif76oq.AuthMicroservice.exception.verificationEmail.VerificationTokenExpiredException;
import com.dif76oq.AuthMicroservice.exception.verificationEmail.VerificationTokenNotFoundException;
import com.dif76oq.AuthMicroservice.model.User;
import com.dif76oq.AuthMicroservice.model.VerificationToken;
import com.dif76oq.AuthMicroservice.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public String generateVerificationToken(User user) {

        //Если токен для этого пользователя уже существовал, то удаляем старый этот токен перед созданием нового
        Optional<VerificationToken> optionalVerificationToken = verificationTokenRepository.findByUserId(user.getId());
        optionalVerificationToken.ifPresent(verificationTokenRepository::delete);

        //создаем новый токен
        String tokenData;
        do {
            tokenData = UUID.randomUUID().toString();
        } while (verificationTokenRepository.existsById(tokenData));

        VerificationToken newVerificationToken = new VerificationToken(tokenData, user, Date.from(ZonedDateTime.now().plusSeconds(600).toInstant()));

        verificationTokenRepository.save(newVerificationToken);
        return tokenData;
    }

    @Transactional(readOnly = false)
    public int confirmRegistration(String inputVerificationToken) throws VerificationTokenNotFoundException, VerificationTokenExpiredException {
        Optional<VerificationToken> optionalVerificationToken = verificationTokenRepository.findById(inputVerificationToken);

        if (optionalVerificationToken.isPresent()) {
            VerificationToken verificationToken = optionalVerificationToken.get();

            int user_id = verificationToken.getUser().getId();

            deleteVerificationToken(verificationToken);

            if (verificationToken.getExpirationDate().after(new Date())) {
                return user_id;
            } else {
                throw new VerificationTokenExpiredException("Verification token expired.");
            }
        }

        throw new VerificationTokenNotFoundException("Verification token not found.");
    }

    private void deleteVerificationToken(VerificationToken verificationToken) {
        verificationTokenRepository.delete(verificationToken);
    }

}
