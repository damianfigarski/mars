package com.doddlecode.mars.service;

import com.doddlecode.mars.dto.ChangePasswordDto;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.exception.code.MarsExceptionCode;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.service.impl.ChangePasswordServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ChangePasswordServiceTest {

    private final String OLD_PASSWORD = "password";
    private final String OLD_WRONG_PASSWORD = "wrong-password";
    private final String ENCODED_OLD_PASSWORD = "encoded-password";
    private final String ENCODED_NEW_PASSWORD = "encoded-new-password";
    private final String NEW_PASSWORD = "new-password";
    private final String REQUEST_HEADER = "request-header";
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserAccountService userAccountService;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private HttpServletRequest request;
    private ChangePasswordService changePasswordService;

    @Before
    public void setUp() {
        changePasswordService = new ChangePasswordServiceImpl(userAccountService,
                bCryptPasswordEncoder, userAccountRepository);
    }

    @Test
    public void changeUserPasswordShouldPassTheTest() {
        // given
        UserAccount userToReturn = getUserWithOldPassword();
        UserAccount savedUser = getUserWithNewPassword();
        ChangePasswordDto changePasswordDto = getChangePasswordDto();

        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn(ENCODED_NEW_PASSWORD);
        when(bCryptPasswordEncoder.matches(OLD_PASSWORD, ENCODED_OLD_PASSWORD)).thenReturn(true);
        when(userAccountService.getUserByToken(REQUEST_HEADER)).thenReturn(userToReturn);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(savedUser);
        when(request.getHeader(any(String.class))).thenReturn(REQUEST_HEADER);

        // when
        changePasswordService.changePassword(changePasswordDto, request);
    }

    private ChangePasswordDto getChangePasswordDto() {
        return ChangePasswordDto.builder()
                .newPassword(NEW_PASSWORD)
                .repeatedPassword(NEW_PASSWORD)
                .oldPassword(OLD_PASSWORD)
                .build();
    }


    @Test
    public void changeUserPasswordWithWrongOldPasswordShouldThrowException() {
        // given
        UserAccount userToReturn = getUserWithOldPassword();
        UserAccount savedUser = getUserWithNewPassword();
        ChangePasswordDto changePasswordDto = getChangePasswordDtoWithInvalidOldPassword();

        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn(ENCODED_NEW_PASSWORD);
        when(bCryptPasswordEncoder.matches(OLD_WRONG_PASSWORD, ENCODED_OLD_PASSWORD)).thenReturn(false);
        when(userAccountService.getUserByToken(REQUEST_HEADER)).thenReturn(userToReturn);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(savedUser);
        when(request.getHeader(any(String.class))).thenReturn(REQUEST_HEADER);

        // when
        try {
            changePasswordService.changePassword(changePasswordDto, request);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(MarsExceptionCode.E010, e.getCode());
            assertEquals(MarsExceptionCode.E010.message(), e.getCode().message());
        }
    }

    @Test
    public void changeUserPasswordWithNotMatchingNewPasswordsShouldThrowException() {
        // given
        UserAccount userToReturn = getUserWithOldPassword();
        UserAccount savedUser = getUserWithNewPassword();
        ChangePasswordDto changePasswordDto = getChangePasswordDtoWithWrongRepeatedPassword();

        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn(ENCODED_NEW_PASSWORD);
        when(bCryptPasswordEncoder.matches(OLD_PASSWORD, ENCODED_OLD_PASSWORD)).thenReturn(true);
        when(userAccountService.getUserByToken(REQUEST_HEADER)).thenReturn(userToReturn);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(savedUser);
        when(request.getHeader(any(String.class))).thenReturn(REQUEST_HEADER);

        // when
        try {
            changePasswordService.changePassword(changePasswordDto, request);
            fail("Should throw exception");
        } catch (MarsRuntimeException e) {
            // then
            assertEquals(MarsExceptionCode.E002, e.getCode());
            assertEquals(MarsExceptionCode.E002.message(), e.getCode().message());
        }
    }

    private ChangePasswordDto getChangePasswordDtoWithInvalidOldPassword() {
        return ChangePasswordDto.builder()
                .newPassword(NEW_PASSWORD)
                .repeatedPassword(NEW_PASSWORD)
                .oldPassword(OLD_WRONG_PASSWORD)
                .build();
    }

    private ChangePasswordDto getChangePasswordDtoWithWrongRepeatedPassword() {
        return ChangePasswordDto.builder()
                .newPassword("new-password")
                .repeatedPassword("wrong-new-password")
                .oldPassword("password")
                .build();
    }

    private UserAccount getUserWithOldPassword() {
        return UserAccount.builder()
                .password(ENCODED_OLD_PASSWORD)
                .build();
    }

    private UserAccount getUserWithNewPassword() {
        return UserAccount.builder()
                .password(ENCODED_NEW_PASSWORD)
                .build();
    }

}
