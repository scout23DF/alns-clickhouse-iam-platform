package com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    @Override
    void delete(Role role);

}
