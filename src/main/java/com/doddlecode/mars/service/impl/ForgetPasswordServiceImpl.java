package com.doddlecode.mars.service.impl;

import com.doddlecode.mars.dto.ForgetPasswordHelperDto;
import com.doddlecode.mars.entity.PasswordResetToken;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.exception.code.MarsExceptionCode;
import com.doddlecode.mars.repository.PasswordResetTokenRepository;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.service.EmailService;
import com.doddlecode.mars.service.ForgetPasswordService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

@Service
public class ForgetPasswordServiceImpl implements ForgetPasswordService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    @Value("mars.data.client.host-name")
    private String hostName;

    public ForgetPasswordServiceImpl(UserAccountRepository userAccountRepository,
                                     PasswordResetTokenRepository passwordResetTokenRepository,
                                     BCryptPasswordEncoder bCryptPasswordEncoder,
                                     EmailService emailService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
    }

    @Override
    public void sendEmailWithChangingPasswordCredentials(String email) throws MarsRuntimeException {
        UserAccount userAccount = userAccountRepository.findByEmail(email);
        if (userAccount == null)
            throw new MarsRuntimeException(MarsExceptionCode.E006);

        String generatedToken = createUniqueToken();

        PasswordResetToken passwordResetToken = getPasswordResetToken(generatedToken, userAccount);
        passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);

        sendEmailToUserWithPasswordResetToken(userAccount, passwordResetToken);
    }

    private String createUniqueToken() {
        String createdToken;
        PasswordResetToken passwordResetToken;

        do {
            createdToken = RandomStringUtils.randomAlphanumeric(60);
            passwordResetToken = passwordResetTokenRepository.findByToken(createdToken);
        } while (passwordResetToken != null);

        return createdToken;
    }

    private PasswordResetToken getPasswordResetToken(String token, UserAccount userAccount) {
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
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(forgetPasswordHelperDto.getToken());
        if (passwordResetToken == null)
            throw new MarsRuntimeException(MarsExceptionCode.E005);

        if (passwordResetToken.isUsed())
            throw new MarsRuntimeException(MarsExceptionCode.E003);

        if (LocalDateTime.now().isAfter(passwordResetToken.getExpiredDate()))
            throw new MarsRuntimeException(MarsExceptionCode.E004);

        UserAccount userAccount = passwordResetToken.getUserAccount();
        String encodedPassword = bCryptPasswordEncoder.encode(forgetPasswordHelperDto.getPassword());
        userAccount.setPassword(encodedPassword);
        userAccountRepository.save(userAccount);
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }

}
