package com.doddlecode.mars.service.impl;

import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.entity.UserLog;
import com.doddlecode.mars.repository.UserLogRepository;
import com.doddlecode.mars.service.UserLogService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class UserLogServiceImpl implements UserLogService {

    private static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

    private final HttpServletRequest request;
    private final UserLogRepository userLogRepository;

    public UserLogServiceImpl(HttpServletRequest request,
                              UserLogRepository userLogRepository) {
        this.request = request;
        this.userLogRepository = userLogRepository;
    }

    @Override
    public void logUser(UserAccount userAccount) {
        UserLog userLog = buildUserLog(userAccount);
        String ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        userLog.setIpAddress(ipAddress);
        userLogRepository.save(userLog);
    }

    private UserLog buildUserLog(UserAccount userAccount) {
        return new UserLog().builder()
                .userAccount(userAccount)
                .logDate(LocalDateTime.now())
                .build();
    }

}
