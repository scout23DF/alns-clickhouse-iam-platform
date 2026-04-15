package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.AllowedActionsDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.GenericResultOpenFgaDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.PermissionSummaryDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.ResultOperationOpenFgaDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl.AuthorizationServiceImpl;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl.adapters.OpenFGAAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AuthorizationServiceImpl.class)
public class AuthorizationServiceComponentTest {

    @Autowired
    private IAuthorizationService authorizationService;

    @MockitoBean
    private OpenFGAAdapter openFGAAdapter;

    @Test
    public void shouldReturnTrueWhenFgaCheckIsSuccessfulAndAllowed() {
        // Arrange
        String objectId = "doc-123";
        String objectType = "document";
        String relation = "viewer";
        String userType = "user";
        String userId = "alice";

        ResultOperationOpenFgaDTO mockResult = ResultOperationOpenFgaDTO.builder()
                .checkingResult(true)
                .statusCode(200)
                .build();

        when(openFGAAdapter.check(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockResult);

        // Act
        boolean result = authorizationService.checkFGAPermission(objectId, objectType, relation, userType, userId);

        // Assert
        assertTrue(result);
        verify(openFGAAdapter).check(objectId, objectType, relation, userType, userId);
    }

    @Test
    public void shouldThrowAuthorizationServiceExceptionWhenFgaAdapterReturnsAnError() {
        // Arrange
        ResultOperationOpenFgaDTO mockResult = ResultOperationOpenFgaDTO.builder()
                .occurredException(new RuntimeException("FGA Connection Failed"))
                .build();

        when(openFGAAdapter.check(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockResult);

        // Act / Assert
        assertThrows(
                AuthorizationServiceException.class,
                () -> authorizationService.checkFGAPermission("doc-1", "doc", "owner", "user", "u1")
        );
    }

    @Test
    public void shouldCallAdapterToAddPermissionsInBatch() {
        // Arrange
        PermissionSummaryDTO permission = PermissionSummaryDTO.builder()
                .resourceId("res1")
                .resourceType("document")
                .relationshipType("owner")
                .subjectId("user1")
                .subjectType("user")
                .build();

        List<PermissionSummaryDTO> permissions = Collections.singletonList(permission);
        
        ResultOperationOpenFgaDTO mockResult = ResultOperationOpenFgaDTO.builder()
                .statusCode(200)
                .build();

        when(openFGAAdapter.createRelationshipsInBatch(anyList())).thenReturn(mockResult);

        // Act
        authorizationService.addFGAPermissionsInBatch(permissions);

        // Assert
        verify(openFGAAdapter).createRelationshipsInBatch(anyList());
    }

    @Test
    public void shouldGetAllowedActionsForASpecificResourceAndUser() {
        // Arrange
        PermissionSummaryDTO summary = PermissionSummaryDTO.builder()
                .resourceId("res1")
                .resourceType("document")
                .subjectId("user1")
                .subjectType("user")
                .build();

        AllowedActionsDTO mockActions = AllowedActionsDTO.builder()
                .canRead(true)
                .canWrite(false)
                .build();

        GenericResultOpenFgaDTO<AllowedActionsDTO> mockResult = GenericResultOpenFgaDTO.<AllowedActionsDTO>builder()
                .responseFromFga(mockActions)
                .build();

        when(openFGAAdapter.getAllowedActionsOf(any(PermissionSummaryDTO.class))).thenReturn(mockResult);

        // Act
        AllowedActionsDTO result = authorizationService.getAllowedActionsOnObjectForUser(summary);

        // Assert
        assertNotNull(result);
        assertTrue(result.canRead());
        assertFalse(result.canWrite());
        verify(openFGAAdapter).getAllowedActionsOf(summary);
    }
}
