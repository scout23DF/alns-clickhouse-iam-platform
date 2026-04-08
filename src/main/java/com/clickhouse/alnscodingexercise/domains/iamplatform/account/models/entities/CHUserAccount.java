package com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.entities.ResourceThing;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "tb_user_account",
        uniqueConstraints = {@UniqueConstraint(name = "idx_useraccount_username", columnNames = {"username"})}
)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CHUserAccount {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private String email;

    @Column(length = 60)
    private String password;

    private boolean enabled = false;

    @OneToMany(mappedBy = "userCreator", fetch = FetchType.EAGER)
    private List<ResourceThing> createdResourcesThings;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

}