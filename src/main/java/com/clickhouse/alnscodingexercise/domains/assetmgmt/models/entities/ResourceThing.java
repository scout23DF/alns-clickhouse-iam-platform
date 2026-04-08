package com.clickhouse.alnscodingexercise.domains.assetmgmt.models.entities;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        name = "tb_resource_thing",
        uniqueConstraints = {@UniqueConstraint(name = "idx_resourcething_title", columnNames = {"title"})}
)
@Getter
@Setter
@NoArgsConstructor
public class ResourceThing {

    @Id
    private String id;
    private String title;
    private String metadata;
    private String summaryContent;
    private String fullContent;
    private Instant createdAt;
    private Instant updatedAt;

    @ManyToOne
    private CHUserAccount userCreator;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ResourceThing that = (ResourceThing) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
