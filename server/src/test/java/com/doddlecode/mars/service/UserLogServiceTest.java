package com.doddlecode.mars.service;

import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.repository.UserLogRepository;
import com.doddlecode.mars.service.impl.UserLogServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

@RunWith(MockitoJUnitRunner.class)
public class UserLogServiceTest {

    @Mock
    private UserLogRepository userLogRepository;
    @Mock
    private HttpServletRequest request;

    private UserLogService userLogService;

    @Before
    public void setUp() {
        userLogService = new UserLogServiceImpl(request, userLogRepository);
    }

    @After
    public void destroy() {
        userLogService = null;
    }

    @Test
    public void loggingUserAuthorizationAction() {
        //given
        UserAccount userAccount = getUser();

        //when
        userLogService.logUser(userAccount);
    }

    private UserAccount getUser() {
        return UserAccount.builder()
                .email("test@test.com")
                .enabled(true)
                .build();
    }

}