package com.doddlecode.mars.service.impl;

import com.doddlecode.mars.entity.Role;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.entity.VerificationToken;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.repository.RoleRepository;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.repository.VerificationTokenRepository;
import com.doddlecode.mars.service.EmailService;
import com.doddlecode.mars.service.UserAccountService;
import com.doddlecode.mars.util.JwtUtil;
import com.doddlecode.mars.util.MessageBundleUtil;
import com.google.common.collect.Sets;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E007;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E011;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E015;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E016;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("mars.data.application-name")
    private String applicationName;
    @Value("mars.data.client.host-name")
    private String clientHostName;

    @Override
    public UserAccount getUserByToken(String token) throws MarsRuntimeException {
        String validToken = Optional.ofNullable(token)
                .orElseThrow(() -> new MarsRuntimeException(E011));
        String email = getEmailFromToken(validToken);

        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new MarsRuntimeException(E015));
    }

    private String getEmailFromToken(String token) {
        return Optional.ofNullable(token)
                .map(JwtUtil::parseToken)
                .map(Claims::getSubject)
                .orElseThrow(() -> new MarsRuntimeException(E016));
    }

    @Override
    public UserAccount getById(Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new MarsRuntimeException(E015));
    }

    @Override
    public UserAccount create(UserAccount userAccount) throws MarsRuntimeException {
        try {
            UserAccount savedUser = saveUserAccount(userAccount);
            VerificationToken verificationToken = saveVerificationToken(savedUser);
            sendEmailToUserWithVerificationToken(verificationToken);

            return userAccount;
        } catch (DataIntegrityViolationException e) {
            throw new MarsRuntimeException(E007);
        }
    }

    private UserAccount saveUserAccount(UserAccount userAccount) {
        String password = userAccount.getPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        userAccount.setPassword(encodedPassword);
        userAccount.setCreated(LocalDateTime.now());
        userAccount.setEnabled(false);

        userAccount.setRoles(getUserRoles());
        return userAccountRepository.save(userAccount);
    }

    private Set<Role> getUserRoles() {
        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new MarsRuntimeException(E015));
        return Sets.newHashSet(role);
    }

    private VerificationToken saveVerificationToken(UserAccount userAccount) {
        String token = generateToken();
        VerificationToken verificationToken = buildVerificationToken(token, userAccount);
        return verificationTokenRepository.save(verificationToken);
    }

    private String generateToken() {
        String token;
        Optional<VerificationToken> verificationToken;
        do {
            token = RandomStringUtils.randomAlphanumeric(20);
            verificationToken = verificationTokenRepository.findByVerificationToken(token);
        } while (verificationToken.isPresent());
        return token;
    }

    private VerificationToken buildVerificationToken(String token, UserAccount userAccount) {
        return VerificationToken.builder()
                .verificationToken(token)
                .expiredDate(LocalDateTime.now().plusHours(24))
                .userAccount(userAccount)
                .build();
    }

    private void sendEmailToUserWithVerificationToken(VerificationToken verificationToken) {
        UserAccount userAccount = verificationToken.getUserAccount();
        String message = MessageBundleUtil.getMessage("email.message");
        message = MessageFormat.format(message, applicationName, clientHostName, verificationToken.getVerificationToken());
        emailService.sendMessage(userAccount.getEmail(), "Account activation", message);
    }

    @Override
    public UserAccount update(UserAccount userAccount) throws MarsRuntimeException {
        try {
            return userAccountRepository.save(userAccount);
        } catch (DataIntegrityViolationException e) {
            throw new MarsRuntimeException(E007);
        }
    }

}
