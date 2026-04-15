package com.clickhouse.alnscodingexercise.domains.assetmgmt.web.controllers.rest;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.repositories.ResourceThingRepository;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.web.tests.AbstractControllerIntegrationTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class ResourceThingMgmtRestControllerIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ResourceThingRepository resourceThingRepository;

    @BeforeEach
    public void setUp() {
        super.setUpRestAssuredPort();
        resourceThingRepository.deleteAll();
    }

    @Test
    public void shouldCreateSixResourcesThings() throws IOException {
        // Arrange
        List<CommandResourceThingDTO> resourceThingsToCreate = STATIC_OBJECT_MAPPER.readValue(
                Objects.requireNonNull(resourceLoader.getResource("classpath:fake-data/test-data-resource-things-fga-hero-master-01.json")).getInputStream(),
                new TypeReference<>() {}
        );
        CommandResourceThingDTO expectedResource = resourceThingsToCreate.getFirst();

        // Act
        Response rawResponse = given()
                .accept("application/json")
                .contentType("application/json")
                .body(resourceThingsToCreate)
                .when()
                .post(AppConstants.DEFAULT_API_ASSETSMGMT_PREFIX_PATH+ "/resources-things/batch")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();

        assertNotNull(rawResponse.getContentType());
        assertTrue(
                rawResponse.getContentType().contains("application/json"),
                () -> "Expected JSON response but received content type '" + rawResponse.getContentType() + "' with body: " + rawResponse.asString()
        );

        JsonNode response = STATIC_OBJECT_MAPPER.readTree(rawResponse.asString());

        // Assert
        assertNotNull(response);
        assertEquals(201, response.get("httpStatusCode").asInt());
        JsonNode resultBody = response.get("resultBody");
        assertNotNull(resultBody);
        assertEquals(resourceThingsToCreate.size(), resultBody.size());

        // Verify some properties of the created resources and ACLs
        JsonNode createdResource = resultBody.get(0);
        JsonNode itemProtected = createdResource.get("itemProtected");
        assertNotNull(itemProtected);
        assertNotNull(itemProtected.get("id"));
        assertEquals(expectedResource.id(), itemProtected.get("id").asText());
        assertEquals(expectedResource.title(), itemProtected.get("title").asText());

        // Verify ACL details are synchronized from the request payload
        JsonNode protectedObjectsAclsList = createdResource.get("protectedObjectsAclsList");
        assertNotNull(protectedObjectsAclsList);
        assertNotNull(protectedObjectsAclsList.get(0));
        assertEquals(
                expectedResource.assignableGrantsRequestsList().size(),
                protectedObjectsAclsList.size()
        );

        String expectedSubjectId = expectedResource.assignableGrantsRequestsList().getFirst().subjectId();
        boolean subjectFound = false;
        for (JsonNode aclNode : protectedObjectsAclsList) {
            JsonNode subjectIdNode = aclNode.get("subjectId");
            if (subjectIdNode != null && expectedSubjectId.equals(subjectIdNode.asText())) {
                subjectFound = true;
                break;
            }
        }
        assertTrue(subjectFound, "Expected ACL subjectId not found in response: " + expectedSubjectId);

        // Verify persistence in the database
        assertEquals(resourceThingsToCreate.size(), resourceThingRepository.count());
    }
}
