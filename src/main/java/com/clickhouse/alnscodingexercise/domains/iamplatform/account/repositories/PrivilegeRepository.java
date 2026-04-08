package com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Privilege findByName(String name);

    @Override
    void delete(Privilege privilege);

}
