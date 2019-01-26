package com.doddlecode.mars.service;

import com.doddlecode.mars.entity.PasswordResetToken;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.exception.code.MarsExceptionCode;
import com.doddlecode.mars.dto.ForgetPasswordHelperDto;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ForgetPasswordServiceTest {

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private EmailService emailService;

    private ForgetPasswordService forgetPasswordService;

    private final String ENCRYPTED_PASSWORD = "encrypted-password";
    private final String CHANGED_PASSWORD = "changed-password";
    private final String ENCRYPTED_CHANGED_PASSWORD = "encrypted-changed-password";
    private final String TOKEN;

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
        UserAccount userAccount = getValidUser();
        PasswordResetToken passwordResetToken = notUsedPasswordResetTokenForUser(userAccount);

        when(userAccountRepository.findByEmail("test@test.com")).thenReturn(userAccount);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        when(passwordResetTokenRepository.findByToken(any(String.class))).thenReturn(null);

        // when
        forgetPasswordService.sendEmailWithChangingPasswordCredentials("test@test.com");
    }

    @Test
    public void messageWithNecessaryCredentialsWhenUserRequestedInvalidEmailAddressShouldThrowUserAccountNotFoundByEmailAddressException() {
        // given
        UserAccount userAccount = getValidUser();
        PasswordResetToken passwordResetToken = notUsedPasswordResetTokenForUser(userAccount);

        when(userAccountRepository.findByEmail("test@test.com")).thenReturn(null);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        when(passwordResetTokenRepository.findByToken(any(String.class))).thenReturn(null);

        // when
        try {
            forgetPasswordService.sendEmailWithChangingPasswordCredentials("test@test.com");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(MarsExceptionCode.E006, e.getCode());
            assertEquals(MarsExceptionCode.E006.message(), e.getCode().message());
        }
    }

    @Test
    public void changingUserPassword() {
        // given
        ForgetPasswordHelperDto forgetPasswordHelperDto = getForgetPasswordHelperDtoWithTokenAndPassword();
        UserAccount userAccount = getValidUserWithPassword();
        PasswordResetToken passwordResetToken = notUsedPasswordResetTokenForUser(userAccount);

        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(bCryptPasswordEncoder.encode(CHANGED_PASSWORD)).thenReturn(ENCRYPTED_CHANGED_PASSWORD);

        // when
        forgetPasswordService.forgetPassword(forgetPasswordHelperDto);
    }

    @Test
    public void changingPasswordWithInvalidTokenShouldThrowPasswordResetTokenNotFoundException() {
        // given
        ForgetPasswordHelperDto forgetPasswordHelperDto = getForgetPasswordHelperDtoWithTokenAndPassword();

        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(null);

        // when
        try {
            forgetPasswordService.forgetPassword(forgetPasswordHelperDto);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(MarsExceptionCode.E005, e.getCode());
            assertEquals(MarsExceptionCode.E005.message(), e.getCode().message());
        }
    }

    @Test
    public void changingPasswordWhichPasswordResetTokenWasAlreadyUsedShouldThrowPasswordResetTokenAlreadyUsedException() {
        ForgetPasswordHelperDto forgetPasswordHelperDto = getForgetPasswordHelperDtoWithTokenAndPassword();
        UserAccount userAccount = getValidUserWithPassword();
        PasswordResetToken passwordResetToken = usedPasswordResetTokenForUser(userAccount);

        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(bCryptPasswordEncoder.encode(CHANGED_PASSWORD)).thenReturn(ENCRYPTED_CHANGED_PASSWORD);

        try {
            forgetPasswordService.forgetPassword(forgetPasswordHelperDto);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(MarsExceptionCode.E003, e.getCode());
            assertEquals(MarsExceptionCode.E003.message(), e.getCode().message());
        }
    }

    @Test//(expected = PasswordResetTokenDateExpiredException.class)
    public void changingPasswordWhichPasswordResetTokenDateExpiredShouldThrowPasswordResetTokenDateExpiredException() {
        ForgetPasswordHelperDto forgetPasswordHelperDto = getForgetPasswordHelperDtoWithTokenAndPassword();
        UserAccount userAccount = getValidUserWithPassword();
        PasswordResetToken passwordResetToken = notUsedPasswordResetTokenForUserWithExpiredDate(userAccount);

        when(passwordResetTokenRepository.findByToken(TOKEN)).thenReturn(passwordResetToken);
        when(bCryptPasswordEncoder.encode(CHANGED_PASSWORD)).thenReturn(ENCRYPTED_CHANGED_PASSWORD);

        try {
            forgetPasswordService.forgetPassword(forgetPasswordHelperDto);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(MarsExceptionCode.E004, e.getCode());
            assertEquals(MarsExceptionCode.E004.message(), e.getCode().message());
        }
    }

    private ForgetPasswordHelperDto getForgetPasswordHelperDtoWithTokenAndPassword() {
        return ForgetPasswordHelperDto.builder()
                .token(TOKEN)
                .password(CHANGED_PASSWORD)
                .build();
    }

    private UserAccount getValidUser() {
        return UserAccount.builder()
                .fullName("Test test")
                .email("test@test.com")
                .enabled(true)
                .build();
    }

    private UserAccount getValidUserWithPassword() {
        return UserAccount.builder()
                .fullName("Test test")
                .email("test@test.com")
                .enabled(true)
                .password(ENCRYPTED_PASSWORD)
                .build();
    }

    private PasswordResetToken notUsedPasswordResetTokenForUser(final UserAccount userAccount) {
        return PasswordResetToken.builder()
                .passwordResetTokenId(RandomUtils.nextLong())
                .token(RandomStringUtils.randomAlphanumeric(60))
                .used(false)
                .expiredDate(LocalDateTime.now().plusHours(24))
                .userAccount(userAccount)
                .build();
    }

    private PasswordResetToken usedPasswordResetTokenForUser(final UserAccount userAccount) {
        return PasswordResetToken.builder()
                .passwordResetTokenId(RandomUtils.nextLong())
                .token(RandomStringUtils.randomAlphanumeric(60))
                .used(true)
                .expiredDate(LocalDateTime.now().plusHours(24))
                .userAccount(userAccount)
                .build();
    }

    private PasswordResetToken notUsedPasswordResetTokenForUserWithExpiredDate(final UserAccount userAccount) {
        return PasswordResetToken.builder()
                .passwordResetTokenId(RandomUtils.nextLong())
                .token(RandomStringUtils.randomAlphanumeric(60))
                .used(false)
                .expiredDate(LocalDateTime.now().minusHours(24))
                .userAccount(userAccount)
                .build();
    }

}
