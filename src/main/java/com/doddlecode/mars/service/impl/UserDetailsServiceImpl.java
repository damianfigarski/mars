package com.doddlecode.mars.service.impl;

import com.doddlecode.mars.entity.Role;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.service.UserLogService;
import com.google.common.collect.Lists;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final UserLogService userLogService;

    public UserDetailsServiceImpl(UserAccountRepository userAccountRepository,
                                  UserLogService userLogService) {
        this.userAccountRepository = userAccountRepository;
        this.userLogService = userLogService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount user = userAccountRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(email);
        }

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException(email);
        }

        List<GrantedAuthority> authorities = getGrantedAuthorities(user);
        userLogService.logUser(user);

        return new User(user.getEmail(), user.getPassword(), authorities);
    }

    private List<GrantedAuthority> getGrantedAuthorities(UserAccount userAccount) {
        List<GrantedAuthority> authorities = Lists.newArrayList();
        for (Role r : userAccount.getRoles()) {
            GrantedAuthority authority = new SimpleGrantedAuthority(r.getRoleName());
            authorities.add(authority);
        }

        return authorities;
    }

}
