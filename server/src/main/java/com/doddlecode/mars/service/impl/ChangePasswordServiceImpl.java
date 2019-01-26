package com.doddlecode.mars.service.impl;

import com.doddlecode.mars.dto.ChangePasswordDto;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.exception.code.MarsExceptionCode;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.service.ChangePasswordService;
import com.doddlecode.mars.service.UserAccountService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.doddlecode.mars.security.SecurityConstants.HEADER_STRING;

@Service
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private final UserAccountService userAccountService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserAccountRepository userAccountRepository;

    public ChangePasswordServiceImpl(UserAccountService userAccountService,
                                     BCryptPasswordEncoder bCryptPasswordEncoder,
                                     UserAccountRepository userAccountRepository) {
        this.userAccountService = userAccountService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto, HttpServletRequest request)
            throws MarsRuntimeException {
        UserAccount userAccount = userAccountService.getUserByToken(request.getHeader(HEADER_STRING));
        checkIfOldPasswordIsCorrect(changePasswordDto, userAccount);
        checkIfBothNewPasswordsAreTheSame(changePasswordDto);

        String encodedPassword = bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword());
        userAccount.setPassword(encodedPassword);
        userAccountRepository.save(userAccount);
    }

    private void checkIfOldPasswordIsCorrect(final ChangePasswordDto changePasswordDto,
                                             final UserAccount userAccount) throws MarsRuntimeException {
        boolean isOldPasswordCorrect
                = bCryptPasswordEncoder.matches(changePasswordDto.getOldPassword(), userAccount.getPassword());

        if (!isOldPasswordCorrect)
            throw new MarsRuntimeException(MarsExceptionCode.E010);
    }

    private void checkIfBothNewPasswordsAreTheSame(final ChangePasswordDto changePasswordDto)
            throws MarsRuntimeException {
        boolean bothNewPasswordsAreTheSame
                = changePasswordDto.getNewPassword().equals(changePasswordDto.getRepeatedPassword());

        if (!bothNewPasswordsAreTheSame)
            throw new MarsRuntimeException(MarsExceptionCode.E002);
    }

}
