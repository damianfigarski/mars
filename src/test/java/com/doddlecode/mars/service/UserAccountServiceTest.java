package com.doddlecode.mars.service;

import com.doddlecode.mars.entity.Role;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.entity.VerificationToken;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.exception.code.MarsExceptionCode;
import com.doddlecode.mars.repository.RoleRepository;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.repository.VerificationTokenRepository;
import com.doddlecode.mars.service.impl.UserAccountServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Date;

import static com.doddlecode.mars.security.SecurityConstants.EXPIRATION_TIME;
import static com.doddlecode.mars.security.SecurityConstants.SECRET;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserAccountServiceTest {

    private final String ROLE_USER = "ROLE_USER";
    private final String SECRET_PASSWORD = "secretPassword";
    private final String ENCODED_SECRET_PASSWORD = "encoded-secretPassword";
    private final String EMAIL = "test.user@test.com";
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmailService emailService;
    private UserAccountService userService;

    @Before
    public void setUp() {
        userService = new UserAccountServiceImpl(userAccountRepository, roleRepository,
                verificationTokenRepository, emailService, bCryptPasswordEncoder);
    }

    @After
    public void destroy() {
        userService = null;
    }

    @Test
    public void findingUserInDatabaseByTokenGivenAsAMethodParameter() {
        // given
        UserAccount userAccount = getBasicUser();
        String token = generateJWTToken(userAccount);

        when(userAccountRepository.findByEmail(EMAIL)).thenReturn(userAccount);

        // when
        UserAccount userByToken = userService.getUserByToken(token);

        // then
        assertNotNull(userByToken);
        assertEquals(userAccount, userByToken);
        assertEquals(userAccount.getEmail(), userByToken.getEmail());
    }

    private UserAccount getBasicUser() {
        return UserAccount.builder()
                .email(EMAIL)
                .build();
    }

    @Test
    public void createUserTest() {
        // given
        UserAccount userAccount = getUser();
        VerificationToken verificationToken = basicVerificationToken(userAccount);
        Role role = getUserRole();

        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn(ENCODED_SECRET_PASSWORD);
        userAccount.setPassword(bCryptPasswordEncoder.encode(SECRET_PASSWORD));
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
        when(roleRepository.findByRoleName(ROLE_USER)).thenReturn(role);

        // when
        UserAccount savedUser = userService.create(userAccount);

        // then
        assertNotNull(savedUser);
        assertEquals(userAccount.getEmail(), savedUser.getEmail());
        assertEquals(ENCODED_SECRET_PASSWORD, savedUser.getPassword());
        assertEquals(userAccount.getFullName(), savedUser.getFullName());
        assertNotNull(savedUser.getCreated());
        assertFalse(savedUser.isEnabled());
        assertEquals(1, savedUser.getRoles().size());
        assertEquals(ROLE_USER, savedUser.getRoles().iterator().next().getRoleName());
    }

    @Test
    public void creatingUserWhichAlreadyExistInDatabase() {
        // given
        UserAccount userAccount = getUser();

        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn(ENCODED_SECRET_PASSWORD);
        userAccount.setPassword(bCryptPasswordEncoder.encode(SECRET_PASSWORD));
        when(userAccountRepository.save(userAccount)).thenThrow(DataIntegrityViolationException.class);

        // when
        try {
            userService.create(userAccount);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(MarsExceptionCode.E007, e.getCode());
            assertEquals(MarsExceptionCode.E007.message(), e.getCode().message());
        }
    }

    @Test
    public void updateUserAccountTest() {
        // given
        UserAccount userAccount = getUser();

        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn(ENCODED_SECRET_PASSWORD);
        userAccount.setPassword(bCryptPasswordEncoder.encode(SECRET_PASSWORD));
        when(userAccountRepository.save(userAccount)).thenThrow(DataIntegrityViolationException.class);

        // when
        try {
            userService.update(userAccount);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(MarsExceptionCode.E007, e.getCode());
            assertEquals(MarsExceptionCode.E007.message(), e.getCode().message());
        }
    }

    private UserAccount getUser() {
        return UserAccount.builder()
                .email(EMAIL)
                .fullName("test user")
                .build();
    }

    private VerificationToken basicVerificationToken(UserAccount userAccount) {
        return VerificationToken.builder()
                .expiredDate(LocalDateTime.now().plusHours(24))
                .verificationToken(RandomStringUtils.randomAlphanumeric(20))
                .userAccount(userAccount)
                .build();
    }

    private Role getUserRole() {
        return Role.builder()
                .roleName(ROLE_USER)
                .build();
    }

    private String generateJWTToken(UserAccount userAccount) {
        return Jwts.builder()
                .setSubject(userAccount.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();
    }

}