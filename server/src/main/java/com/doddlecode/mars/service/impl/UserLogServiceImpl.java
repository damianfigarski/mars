package com.doddlecode.mars.service.impl;

import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.entity.UserLog;
import com.doddlecode.mars.repository.UserLogRepository;
import com.doddlecode.mars.service.UserLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLogServiceImpl implements UserLogService {

    private static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

    private final HttpServletRequest request;
    private final UserLogRepository userLogRepository;

    @Override
    public void logUser(UserAccount userAccount) {
        UserLog userLog = buildUserLog(userAccount);
        String ipAddress = getIpAddress();
        userLog.setIpAddress(ipAddress);
        userLogRepository.save(userLog);
    }

    private UserLog buildUserLog(UserAccount userAccount) {
        return UserLog.builder()
                .userAccount(userAccount)
                .logDate(LocalDateTime.now())
                .build();
    }

    private String getIpAddress() {
        return Optional.ofNullable(request.getHeader(X_FORWARDED_FOR_HEADER))
                .orElseGet(request::getRemoteAddr);
    }

}
