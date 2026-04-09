package com.clickhouse.alnscodingexercise.domains.iamplatform.account.services;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.PasswordResetToken;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.VerificationToken;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests.CreateUserAccountRequestDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests.PasswordRequestDTO;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface IUserAccountMgmtService {

    CHUserAccount registerNewUserAccount(CreateUserAccountRequestDTO accountDto, Locale locale, String appURL);

    Optional<CHUserAccount> getUserById(long id);

    CHUserAccount getUserByEmail(String email);

    CHUserAccount getUserByUsername(String searchUsername);

    CHUserAccount getUserByVerificationToken(String verificationToken);

    void saveRegisteredUser(CHUserAccount CHUserAccount);

    void deleteUser(CHUserAccount CHUserAccount);

    void createVerificationTokenForUser(CHUserAccount CHUserAccount, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(CHUserAccount CHUserAccount, String token);

    PasswordResetToken getPasswordResetToken(String token);

    Optional<CHUserAccount> getUserByPasswordResetToken(String token);

    String validatePasswordResetToken(String token);

    void changeUserPassword(CHUserAccount CHUserAccount, PasswordRequestDTO passwordRequestDTO);

    boolean checkIfValidOldPassword(CHUserAccount CHUserAccount, String password);

    String validateVerificationToken(String token);

    List<String> getUsersFromSessionRegistry();
}
