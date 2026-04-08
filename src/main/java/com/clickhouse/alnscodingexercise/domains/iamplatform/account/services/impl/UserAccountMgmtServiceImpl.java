package com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.impl;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.PasswordResetToken;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.VerificationToken;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.PasswordResetTokenRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.RoleRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.UserRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.VerificationTokenRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.IUserAccountMgmtService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests.CreateUserAccountRequestDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests.PasswordRequestDTO;
import com.clickhouse.alnscodingexercise.domains.shared.exceptions.UserAlreadyExistException;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnRegistrationCompletedEvent;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnRegistrationSubmittedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAccountMgmtServiceImpl implements IUserAccountMgmtService {

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";
    public static String APP_NAME = "ALNS-ClickHouse-IAM-Platform";

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository passwordTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SessionRegistry sessionRegistry;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public CHUserAccount registerNewUserAccount(final CreateUserAccountRequestDTO accountDto, Locale locale, String appURL) {
        if (emailExists(accountDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + accountDto.getEmail());
        }
        final CHUserAccount CHUserAccount = new CHUserAccount();

        CHUserAccount.setFirstName(accountDto.getFirstName());
        CHUserAccount.setLastName(accountDto.getLastName());
        CHUserAccount.setUsername(accountDto.getUsername());
        CHUserAccount.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        CHUserAccount.setEmail(accountDto.getEmail());
        CHUserAccount.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));

        CHUserAccount newCHUserAccount = userRepository.save(CHUserAccount);

        eventPublisher.publishEvent(
                OnRegistrationSubmittedEvent.builder()
                        .chUserAccount(newCHUserAccount)
                        .locale(locale)
                        .appUrl(appURL)
                        .build()
        );

        return newCHUserAccount;
    }

    @Override
    public CHUserAccount getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    @Override
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(CHUserAccount newCHUserAccount) {
        userRepository.save(newCHUserAccount);
        eventPublisher.publishEvent(
                OnRegistrationCompletedEvent.builder()
                        .chUserAccount(newCHUserAccount)
                        .build()
        );

    }

    @Override
    public void deleteUser(final CHUserAccount CHUserAccount) {
        final VerificationToken verificationToken = tokenRepository.findByUser(CHUserAccount);

        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }

        final PasswordResetToken passwordToken = passwordTokenRepository.findByUser(CHUserAccount);

        if (passwordToken != null) {
            passwordTokenRepository.delete(passwordToken);
        }

        userRepository.delete(CHUserAccount);
    }

    @Override
    public void createVerificationTokenForUser(final CHUserAccount CHUserAccount, final String token) {
        final VerificationToken myToken = new VerificationToken(token, CHUserAccount);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID()
            .toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public void createPasswordResetTokenForUser(final CHUserAccount CHUserAccount, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, CHUserAccount);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                  : null;
    }

    @Override
    public CHUserAccount findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }

    @Override
    public Optional<CHUserAccount> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token) .getUser());
    }

    @Override
    public Optional<CHUserAccount> getUserByID(final long id) {
        return userRepository.findById(id);
    }

    @Override
    public void changeUserPassword(final CHUserAccount CHUserAccount, final PasswordRequestDTO passwordRequestDTO) {
        CHUserAccount.setPassword(passwordEncoder.encode(passwordRequestDTO.getNewPassword()));
        userRepository.save(CHUserAccount);
        passwordTokenRepository.deleteByToken(passwordRequestDTO.getToken());
    }

    @Override
    public boolean checkIfValidOldPassword(final CHUserAccount CHUserAccount, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, CHUserAccount.getPassword());
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final CHUserAccount CHUserAccount = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
            .getTime() - cal.getTime()
            .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        CHUserAccount.setEnabled(true);
        // tokenRepository.delete(verificationToken);
        userRepository.save(CHUserAccount);
        return TOKEN_VALID;
    }

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public List<String> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals()
            .stream()
            .filter((u) -> !sessionRegistry.getAllSessions(u, false)
                .isEmpty())
            .map(o -> {
                if (o instanceof CHUserAccount) {
                    return ((CHUserAccount) o).getEmail();
                } else {
                    return o.toString()
            ;
                }
            }).collect(Collectors.toList());
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

}
