package com.doddlecode.mars.service;

import com.doddlecode.mars.dto.ForgetPasswordHelperDto;
import com.doddlecode.mars.entity.PasswordResetToken;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.repository.PasswordResetTokenRepository;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.service.impl.ForgetPasswordServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E003;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E004;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E005;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E006;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ForgetPasswordServiceTest {

    private final String ENCRYPTED_PASSWORD = "encrypted-password";
    private final String CHANGED_PASSWORD = "changed-password";
    private final String ENCRYPTED_CHANGED_PASSWORD = "encrypted-changed-password";
    private final String TOKEN;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private EmailService emailService;
    private ForgetPasswordService forgetPasswordService;

    {
        TOKEN = RandomStringUtils.randomAlphanumeric(60);
    }

    @Before
    public void setUp() {
        forgetPasswordService = new ForgetPasswordServiceImpl(userAccountRepository, passwordResetTokenRepository,
                bCryptPasswordEncoder, emailService);
    }

    @After
    public void destroy() {
        forgetPasswordService = null;
    }

    @Test
    public void messageWithNecessaryCredentialsToChangePassword() {
        // given
        Optional<UserAccount> userAccount = getValidUser();
        Optional<PasswordResetToken> passwordResetToken = notUsedPasswordResetTokenForUser(
                userAccount.orElseThrow(NoSuchElementException::new));

        when(userAccountRepository.findByEmail("test@test.com")).thenReturn(userAccount);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(
                passwordResetToken.orElseThrow(NoSuchElementException::new));
        when(passwordResetTokenRepository.findByToken(any(String.class))).thenReturn(Optional.empty());

        // when
        forgetPasswordService.sendEmailWithChangingPasswordCredentials("test@test.com");
    }

    @Test
    public void messageWithNecessaryCredentialsWhenUserRequestedInvalidEmailAddressShouldThrowUserAccountNotFoundByEmailAddressException() {
        // given
        Optional<UserAccount> userAccount = getValidUser();
        Optional<PasswordResetToken> passwordResetToken = notUsedPasswordResetTokenForUser(
                userAccount.orElseThrow(NoSuchElementException::new));

        when(userAccountRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(
                passwordResetToken.orElseThrow(NoSuchElementException::new));
        when(passwordResetTokenRepository.findByToken(any(String.class))).thenReturn(Optional.empty());

        // when
        try {
            forgetPasswordService.sendEmailWithChangingPasswordCredentials("test@test.com");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(E006, e.getCode());
            assertEquals(E006.getMessage(), e.getCode().getMessage());
        }
    }

    @Test
    public void changingUserPassword() {
        // given
        ForgetPasswordHelperDto forgetPasswordHelperDto = getForgetPasswordHelperDtoWithTokenAndPassword();
        UserAccount userAccount = getValidUserWithPassword();
        Optional<PasswordResetToken> passwordResetToken = notUsedPasswordResetTokenForUser(userAccount);

        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(bCryptPasswordEncoder.encode(CHANGED_PASSWORD)).thenReturn(ENCRYPTED_CHANGED_PASSWORD);

        // when
        forgetPasswordService.forgetPassword(forgetPasswordHelperDto);
    }

    @Test
    public void changingPasswordWithInvalidTokenShouldThrowPasswordResetTokenNotFoundException() {
        // given
        ForgetPasswordHelperDto forgetPasswordHelperDto = getForgetPasswordHelperDtoWithTokenAndPassword();

        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(Optional.empty());

        // when
        try {
            forgetPasswordService.forgetPassword(forgetPasswordHelperDto);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(E005, e.getCode());
            assertEquals(E005.getMessage(), e.getCode().getMessage());
        }
    }

    @Test
    public void changingPasswordWhichPasswordResetTokenWasAlreadyUsedShouldThrowPasswordResetTokenAlreadyUsedException() {
        ForgetPasswordHelperDto forgetPasswordHelperDto = getForgetPasswordHelperDtoWithTokenAndPassword();
        UserAccount userAccount = getValidUserWithPassword();
        Optional<PasswordResetToken> passwordResetToken = usedPasswordResetTokenForUser(userAccount);

        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(bCryptPasswordEncoder.encode(CHANGED_PASSWORD)).thenReturn(ENCRYPTED_CHANGED_PASSWORD);

        try {
            forgetPasswordService.forgetPassword(forgetPasswordHelperDto);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(E003, e.getCode());
            assertEquals(E003.getMessage(), e.getCode().getMessage());
        }
    }

    @Test
    public void changingPasswordWhichPasswordResetTokenDateExpiredShouldThrowPasswordResetTokenDateExpiredException() {
        ForgetPasswordHelperDto forgetPasswordHelperDto = getForgetPasswordHelperDtoWithTokenAndPassword();
        UserAccount userAccount = getValidUserWithPassword();
        Optional<PasswordResetToken> passwordResetToken = notUsedPasswordResetTokenForUserWithExpiredDate(userAccount);

        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(bCryptPasswordEncoder.encode(CHANGED_PASSWORD)).thenReturn(ENCRYPTED_CHANGED_PASSWORD);

        try {
            forgetPasswordService.forgetPassword(forgetPasswordHelperDto);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(E004, e.getCode());
            assertEquals(E004.getMessage(), e.getCode().getMessage());
        }
    }

    private ForgetPasswordHelperDto getForgetPasswordHelperDtoWithTokenAndPassword() {
        return ForgetPasswordHelperDto.builder()
                .token(TOKEN)
                .password(CHANGED_PASSWORD)
                .build();
    }

    private Optional<UserAccount> getValidUser() {
        return Optional.of(
                UserAccount.builder()
                        .fullName("Test test")
                        .email("test@test.com")
                        .enabled(true)
                        .build());
    }

    private UserAccount getValidUserWithPassword() {
        return UserAccount.builder()
                .fullName("Test test")
                .email("test@test.com")
                .enabled(true)
                .password(ENCRYPTED_PASSWORD)
                .build();
    }

    private Optional<PasswordResetToken> notUsedPasswordResetTokenForUser(UserAccount userAccount) {
        return Optional.of(
                PasswordResetToken.builder()
                        .passwordResetTokenId(RandomUtils.nextLong())
                        .token(RandomStringUtils.randomAlphanumeric(60))
                        .used(false)
                        .expiredDate(LocalDateTime.now().plusHours(24))
                        .userAccount(userAccount)
                        .build());
    }

    private Optional<PasswordResetToken> usedPasswordResetTokenForUser(UserAccount userAccount) {
        return Optional.of(
                PasswordResetToken.builder()
                        .passwordResetTokenId(RandomUtils.nextLong())
                        .token(RandomStringUtils.randomAlphanumeric(60))
                        .used(true)
                        .expiredDate(LocalDateTime.now().plusHours(24))
                        .userAccount(userAccount)
                        .build());
    }

    private Optional<PasswordResetToken> notUsedPasswordResetTokenForUserWithExpiredDate(UserAccount userAccount) {
        return Optional.of(
                PasswordResetToken.builder()
                        .passwordResetTokenId(RandomUtils.nextLong())
                        .token(RandomStringUtils.randomAlphanumeric(60))
                        .used(false)
                        .expiredDate(LocalDateTime.now().minusHours(24))
                        .userAccount(userAccount)
                        .build());
    }

}
