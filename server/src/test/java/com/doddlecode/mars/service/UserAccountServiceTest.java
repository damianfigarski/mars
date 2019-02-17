package com.doddlecode.mars.service;

import com.doddlecode.mars.entity.Role;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.entity.VerificationToken;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.repository.RoleRepository;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.repository.VerificationTokenRepository;
import com.doddlecode.mars.service.impl.UserAccountServiceImpl;
import com.doddlecode.mars.util.JwtUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E007;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
        Optional<UserAccount> userAccountOpt = getBasicUser();
        UserAccount userAccount = userAccountOpt.orElseThrow(NoSuchElementException::new);

        String token = JwtUtil.buildToken(userAccount.getEmail(), Lists.emptyList());

        when(userAccountRepository.findByEmail(EMAIL)).thenReturn(userAccountOpt);

        // when
        UserAccount userByToken = userService.getUserByToken(token);

        // then
        assertNotNull(userByToken);
        assertEquals(userAccount, userByToken);
        assertEquals(EMAIL, userByToken.getEmail());
    }

    private Optional<UserAccount> getBasicUser() {
        return Optional.of(
                UserAccount.builder()
                        .email(EMAIL)
                        .build());
    }

    @Test
    public void createUserTest() {
        // given
        UserAccount userAccount = getUser();
        VerificationToken verificationToken = basicVerificationToken(userAccount);
        Optional<Role> role = getUserRole();

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
        assertNotNull(savedUser.getCreated());
        assertFalse(savedUser.isEnabled());
        assertEquals(1, savedUser.getRoles().size());
        assertEquals(ROLE_USER, savedUser.getRoles().iterator().next().getRoleName());
    }

    @Test
    public void creatingUserWhichAlreadyExistInDatabase() {
        // given
        UserAccount userAccount = getUser();
        Optional<Role> role = getUserRole();

        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn(ENCODED_SECRET_PASSWORD);
        when(roleRepository.findByRoleName(ROLE_USER)).thenReturn(role);
        userAccount.setPassword(bCryptPasswordEncoder.encode(SECRET_PASSWORD));
        when(userAccountRepository.save(userAccount)).thenThrow(DataIntegrityViolationException.class);

        // when
        try {
            userService.create(userAccount);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(E007, e.getCode());
            assertEquals(E007.getMessage(), e.getCode().getMessage());
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
            assertEquals(E007, e.getCode());
            assertEquals(E007.getMessage(), e.getCode().getMessage());
        }
    }

    private UserAccount getUser() {
        return UserAccount.builder()
                .email(EMAIL)
                .build();
    }

    private VerificationToken basicVerificationToken(UserAccount userAccount) {
        return VerificationToken.builder()
                .expiredDate(LocalDateTime.now().plusHours(24))
                .verificationToken(RandomStringUtils.randomAlphanumeric(20))
                .userAccount(userAccount)
                .build();
    }

    private Optional<Role> getUserRole() {
        return Optional.of(
                Role.builder()
                        .roleName(ROLE_USER)
                        .build());
    }

}
