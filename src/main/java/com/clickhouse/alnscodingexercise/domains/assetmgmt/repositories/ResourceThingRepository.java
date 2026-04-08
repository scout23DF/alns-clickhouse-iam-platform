package com.clickhouse.alnscodingexercise.domains.assetmgmt.repositories;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.entities.ResourceThing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceThingRepository extends JpaRepository<ResourceThing, String> {

    ResourceThing findByTitle(String searchTitle);

}
