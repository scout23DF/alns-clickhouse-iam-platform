package com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_role")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    private List<CHUserAccount> users;

    @ManyToMany
    @JoinTable(name = "tb_roles_privileges", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    @ToString.Exclude
    private List<Privilege> privileges;


    public Role(final String name) {
        this.name = name;
    }

}