package com.doddlecode.mars.service.impl;

import com.doddlecode.mars.dto.ChangePasswordDto;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.service.ChangePasswordService;
import com.doddlecode.mars.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E002;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E010;
import static com.doddlecode.mars.security.SecurityConstants.HEADER_STRING;

@Service
@RequiredArgsConstructor
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private final UserAccountService userAccountService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserAccountRepository userAccountRepository;
    private final HttpServletRequest request;

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) throws MarsRuntimeException {
        UserAccount userAccount = userAccountService.getUserByToken(request.getHeader(HEADER_STRING));
        checkIfOldPasswordIsCorrect(changePasswordDto, userAccount);
        checkIfBothNewPasswordsAreTheSame(changePasswordDto);

        String encodedPassword = bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword());
        userAccount.setPassword(encodedPassword);
        userAccountRepository.save(userAccount);
    }

    private void checkIfOldPasswordIsCorrect(ChangePasswordDto changePasswordDto, UserAccount userAccount) {
        boolean isOldPasswordCorrect
                = bCryptPasswordEncoder.matches(changePasswordDto.getOldPassword(), userAccount.getPassword());

        Optional.of(isOldPasswordCorrect)
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new MarsRuntimeException(E010));
    }

    private void checkIfBothNewPasswordsAreTheSame(ChangePasswordDto changePasswordDto) {
        boolean bothNewPasswordsAreTheSame
                = changePasswordDto.getNewPassword().equals(changePasswordDto.getRepeatedPassword());

        Optional.of(bothNewPasswordsAreTheSame)
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new MarsRuntimeException(E002));
    }

}
