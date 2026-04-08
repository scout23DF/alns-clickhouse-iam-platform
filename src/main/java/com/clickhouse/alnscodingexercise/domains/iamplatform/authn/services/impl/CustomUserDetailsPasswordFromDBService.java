package com.clickhouse.alnscodingexercise.domains.iamplatform.authn.services.impl;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.UserRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.Privilege;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.Role;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.validation.EmailValidator;
import com.clickhouse.alnscodingexercise.domains.shared.validation.ValidatorUtils;
import dev.openfga.sdk.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("userDetailsPasswordService")
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsPasswordFromDBService implements UserDetailsService, UserDetailsPasswordService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String searchUsernameOrEmail) throws UsernameNotFoundException {
        if (loginAttemptService.isBlocked()) {
            throw new RuntimeException("blocked");
        }

        try {
            CHUserAccount chUserAccount = null;
            if (ValidatorUtils.isValidEmail(searchUsernameOrEmail)) {
                chUserAccount = userRepository.findByEmail(searchUsernameOrEmail);
            } else {
                chUserAccount = userRepository.findByUsername(searchUsernameOrEmail);
            }

            if (chUserAccount == null) {
                throw new UsernameNotFoundException("No user found with username: " + searchUsernameOrEmail);
            }

            return new User(
                    chUserAccount.getUsername(),
                    chUserAccount.getPassword(),
                    chUserAccount.isEnabled(),
                    true,
                    true,
                    true,
                    getAuthorities(chUserAccount.getRoles())
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    // UTIL

    private Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(final Collection<Role> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<Privilege> collection = new ArrayList<>();
        for (final Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (final Privilege item : collection) {
            privileges.add(item.getName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    @Override
    public UserDetails updatePassword(UserDetails user, @Nullable String newPassword) {
        return null;
    }
}
