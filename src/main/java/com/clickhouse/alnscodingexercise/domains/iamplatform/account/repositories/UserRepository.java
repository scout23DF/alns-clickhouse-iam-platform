package com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<CHUserAccount, Long> {

    CHUserAccount findByEmail(String email);
    CHUserAccount findByUsername(String searchUsernameOrEmail);
    void delete(@NonNull CHUserAccount chUserAccount);

}
