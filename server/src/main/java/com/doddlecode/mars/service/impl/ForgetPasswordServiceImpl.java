package com.doddlecode.mars.service.impl;

import com.doddlecode.mars.dto.ForgetPasswordHelperDto;
import com.doddlecode.mars.entity.PasswordResetToken;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.repository.PasswordResetTokenRepository;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.service.EmailService;
import com.doddlecode.mars.service.ForgetPasswordService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E003;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E004;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E005;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E006;

@Service
@RequiredArgsConstructor
public class ForgetPasswordServiceImpl implements ForgetPasswordService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    @Value("mars.data.client.host-name")
    private String hostName;

    @Override
    public void sendEmailWithChangingPasswordCredentials(String email) throws MarsRuntimeException {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new MarsRuntimeException(E006));

        String generatedToken = createUniqueToken();

        PasswordResetToken passwordResetToken = createPasswordResetToken(generatedToken, userAccount);
        passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);

        sendEmailToUserWithPasswordResetToken(userAccount, passwordResetToken);
    }

    private String createUniqueToken() {
        String createdToken;
        Optional<PasswordResetToken> passwordResetToken;

        do {
            createdToken = RandomStringUtils.randomAlphanumeric(60);
            passwordResetToken = passwordResetTokenRepository.findByToken(createdToken);
        } while (passwordResetToken.isPresent());

        return createdToken;
    }

    private PasswordResetToken createPasswordResetToken(String token, UserAccount userAccount) {
        return PasswordResetToken.builder()
                .expiredDate(LocalDateTime.now().plusHours(24))
                .token(token)
                .used(false)
                .userAccount(userAccount)
                .build();
    }

    private void sendEmailToUserWithPasswordResetToken(UserAccount userAccount, PasswordResetToken passwordResetToken) {
        String message = ResourceBundle.getBundle("messages/messages").getString("email.message.forget_password");
        message = MessageFormat.format(message, hostName, passwordResetToken.getToken());
        emailService.sendMessage(userAccount.getEmail(), "Account activation", message);
    }

    @Override
    public void forgetPassword(ForgetPasswordHelperDto forgetPasswordHelperDto) throws MarsRuntimeException {
        PasswordResetToken passwordResetToken = getPasswordResetToken(forgetPasswordHelperDto.getToken());
        checkIfResetTokenWasAlreadyUsed(passwordResetToken);

        if (LocalDateTime.now().isAfter(passwordResetToken.getExpiredDate())) {
            throw new MarsRuntimeException(E004);
        }

        UserAccount userAccount = passwordResetToken.getUserAccount();
        String encodedPassword = bCryptPasswordEncoder.encode(forgetPasswordHelperDto.getPassword());
        saveEditedUser(userAccount, encodedPassword);
        saveUnusedToken(passwordResetToken);
    }

    private PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new MarsRuntimeException(E005));
    }

    private void checkIfResetTokenWasAlreadyUsed(PasswordResetToken passwordResetToken) {
        Optional.of(passwordResetToken)
                .map(PasswordResetToken::isUsed)
                .filter(isUsed -> !isUsed)
                .orElseThrow(() -> new MarsRuntimeException(E003));
    }

    private void saveEditedUser(UserAccount userAccount, String encodedPassword) {
        userAccount.setPassword(encodedPassword);
        userAccountRepository.save(userAccount);
    }

    private void saveUnusedToken(PasswordResetToken passwordResetToken) {
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }

}
