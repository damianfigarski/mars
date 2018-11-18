package com.doddlecode.mars.service.impl;

import com.doddlecode.mars.entity.Role;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.entity.VerificationToken;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.exception.code.MarsExceptionCode;
import com.doddlecode.mars.repository.RoleRepository;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.repository.VerificationTokenRepository;
import com.doddlecode.mars.service.EmailService;
import com.doddlecode.mars.service.UserAccountService;
import com.google.common.collect.Sets;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import static com.doddlecode.mars.security.SecurityConstants.SECRET;
import static com.doddlecode.mars.security.SecurityConstants.TOKEN_PREFIX;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Value("mars.data.application-name")
    private String applicationName;
    @Value("mars.data.client.host-name")
    private String clientHostName;

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository,
                                  RoleRepository roleRepository,
                                  VerificationTokenRepository verificationTokenRepository,
                                  EmailService emailService,
                                  BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserAccount getUserByToken(String token) throws MarsRuntimeException {
        if (token == null) {
            throw new MarsRuntimeException(MarsExceptionCode.E011);
        }

        String email = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();

        return userAccountRepository.findByEmail(email);
    }

    @Override
    public UserAccount getById(Long id) {
        Optional<UserAccount> userAccountOptional = userAccountRepository.findById(id);
        if (!userAccountOptional.isPresent()) {
            throw new MarsRuntimeException(MarsExceptionCode.E012);
        }
        return userAccountOptional.get();
    }

    @Override
    public UserAccount create(UserAccount userAccount) throws MarsRuntimeException {
        try {
            UserAccount savedUser = saveUserAccount(userAccount);
            VerificationToken verificationToken = saveVerificationToken(savedUser);
            sendEmailToUserWithVerificationToken(verificationToken);

            return userAccount;
        } catch (DataIntegrityViolationException e) {
            throw new MarsRuntimeException(MarsExceptionCode.E007);
        }
    }

    private UserAccount saveUserAccount(UserAccount userAccount) {
        String password = userAccount.getPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        userAccount.setPassword(encodedPassword);
        userAccount.setCreated(LocalDateTime.now());
        userAccount.setEnabled(false);

        Role role = roleRepository.findByRoleName("ROLE_USER");
        Set<Role> rolesList = Sets.newHashSet(role);
        userAccount.setRoles(rolesList);
        return userAccountRepository.save(userAccount);
    }

    private VerificationToken saveVerificationToken(UserAccount userAccount) {
        String token = generateToken();
        VerificationToken verificationToken = buildVerificationToken(token, userAccount);
        return verificationTokenRepository.save(verificationToken);
    }

    private String generateToken() {
        String token;
        VerificationToken verificationToken;
        do {
            token = RandomStringUtils.randomAlphanumeric(20);
            verificationToken = verificationTokenRepository.findByVerificationToken(token);
        } while (verificationToken != null);

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
        String message = ResourceBundle.getBundle("messages/messages").getString("email.message");
        message = MessageFormat.format(message, applicationName, clientHostName, verificationToken.getVerificationToken());
        emailService.sendMessage(userAccount.getEmail(), "Account activation", message);
    }

    @Override
    public UserAccount update(UserAccount userAccount) throws MarsRuntimeException {
        try {
            return userAccountRepository.save(userAccount);
        } catch (DataIntegrityViolationException e) {
            throw new MarsRuntimeException(MarsExceptionCode.E007);
        }
    }

}
