package com.doddlecode.mars.security;

import com.doddlecode.mars.entity.Role;
import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.repository.UserAccountRepository;
import com.doddlecode.mars.service.UserLogService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E006;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E019;

@Service("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final UserLogService userLogService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = getUser(username);
        checkIfUserIsEnabled(user);

        List<GrantedAuthority> authorities = getGrantedAuthorities(user);
        userLogService.logUser(user);

        return new User(user.getEmail(), user.getPassword(), authorities);
    }

    private UserAccount getUser(String username) {
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(E006.getMessage()));
    }

    private void checkIfUserIsEnabled(UserAccount user) {
        Optional.ofNullable(user)
                .map(UserAccount::isEnabled)
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new UsernameNotFoundException(E019.getMessage()));
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
