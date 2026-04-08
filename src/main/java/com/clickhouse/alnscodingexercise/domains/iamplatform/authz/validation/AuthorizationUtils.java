package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.validation;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.AllowedActionsDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.PermissionSummaryDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.ProtectedObjectAclDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaObjectTypeEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaRelationEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IAuthorizationService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.validation.annotations.FgaCheck;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthorizationUtils {

    private final Logger logger = LoggerFactory.getLogger(AuthorizationUtils.class);

    private final IAuthorizationService authorizationService;

    public boolean checkFGAPermission(String resourceId,
                                      String resourceType,
                                      String relationType,
                                      String subjectType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No user provided, and no Authentication could be found in the security context");
        }
        return checkFGAPermission(resourceId, resourceType, relationType, subjectType, authentication.getName());
    }

    public boolean checkFGAPermission(String resourceId,
                                      String resourceType,
                                      String relationType,
                                      String subjectType,
                                      String subjectId) {

        logger.debug("**** AuthorizationUtils :: checkFGAPermission Not Throwing Exception *****");

        try {

            return authorizationService.checkFGAPermission(
                    PermissionSummaryDTO.builder()
                            .resourceId(resourceId)
                            .resourceType(resourceType)
                            .relationshipType(relationType)
                            .subjectType(subjectType)
                            .subjectId(subjectId)
                            .build()
            );

        } catch (Exception e) {
            logger.warn("**** Error when checking Permission on FGA, but not throwing Exception  ****", e);
            return false;
        }

    }

    public boolean checkFGAPermissionFromAspect(JoinPoint jointPoint) {
        logger.debug("**** AuthorizationUtils :: checkFGAPermissionFromAspect Not Throwing Exception *****");

        try {
            PermissionSummaryDTO permissionSummaryDTO = buildPermissionSummaryFromJointPointAOP(jointPoint);
            return authorizationService.checkFGAPermission(permissionSummaryDTO);
        } catch (Exception e) {
            logger.warn("**** Error when checking Permission on FGA, but not throwing Exception  ****", e);
            return false;
        }

    }

    private PermissionSummaryDTO buildPermissionSummaryFromJointPointAOP(final JoinPoint jointPoint) {
        MethodSignature signature = (MethodSignature) jointPoint.getSignature();
        Method method = signature.getMethod();
        FgaCheck fgaCheckAnnotation = method.getAnnotation(FgaCheck.class);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < signature.getParameterNames().length; i++) {
            context.setVariable(signature.getParameterNames()[i], jointPoint.getArgs()[i]);
        }

        String targetResourceId = parser.parseExpression(fgaCheckAnnotation.object()).getValue(context, String.class);
        String targetResourceType = fgaCheckAnnotation.objectType();
        String targetRelation = parser.parseExpression(fgaCheckAnnotation.relation()).getValue(context, String.class);
        String targetSubjectType = fgaCheckAnnotation.userType();
        String targetSubjectId = parser.parseExpression(fgaCheckAnnotation.userId()).getValue(context, String.class);
        // String targetSubjectId = (!ObjectUtils.isEmpty(fgaCheckAnnotation.userId()) ? fgaCheckAnnotation.userId() : AppConstants.DEFAULT_SUPER_USERID );

        return PermissionSummaryDTO.builder()
                .resourceId(targetResourceId)
                .resourceType(targetResourceType)
                .relationshipType(targetRelation)
                .subjectType(targetSubjectType)
                .subjectId(targetSubjectId)
                .build();

    }

    public List<ProtectedObjectAclDTO> buildAccessControlListFrom(ResourceThingDTO resourceThingDTO,
                                                                   List<ProtectedObjectAclDTO> sourcedProtectedObjecstAclsList,
                                                                   List<String> filteredUsernamesList) {

        List<ProtectedObjectAclDTO> fgaProtectedObjectAclDTOList = new ArrayList<>();

        sourcedProtectedObjecstAclsList.forEach(oneProtectedObjectAcl -> {

            if (CollectionUtils.isEmpty(filteredUsernamesList) || filteredUsernamesList.contains(oneProtectedObjectAcl.subjectId())) {

                fgaProtectedObjectAclDTOList.add(
                        ProtectedObjectAclDTO.builder()
                                .refenceObjectType(FgaObjectTypeEnum.DOCUMENT.toString())
                                .resourceId(resourceThingDTO.id())
                                .subjectType(oneProtectedObjectAcl.subjectType())
                                .subjectId(oneProtectedObjectAcl.subjectId())
                                .rolesNamesRelationsList(oneProtectedObjectAcl.rolesNamesRelationsList())
                                .allowedActions(
                                        AllowedActionsDTO.builder()
                                                .canChangeOwner(hasPermissionFor(FgaRelationEnum.CAN_CHANGE_OWNER, resourceThingDTO, oneProtectedObjectAcl))
                                                .canWrite(hasPermissionFor(FgaRelationEnum.CAN_WRITE, resourceThingDTO, oneProtectedObjectAcl))
                                                .canRead(hasPermissionFor(FgaRelationEnum.CAN_READ, resourceThingDTO, oneProtectedObjectAcl))
                                                .canShare(hasPermissionFor(FgaRelationEnum.CAN_SHARE, resourceThingDTO, oneProtectedObjectAcl))
                                                .canDelete(hasPermissionFor(FgaRelationEnum.CAN_DELETE, resourceThingDTO, oneProtectedObjectAcl))
                                                .build()
                                )
                                .build()
                );

            }

        });

        return fgaProtectedObjectAclDTOList;
    }

    private Boolean hasPermissionFor(FgaRelationEnum fgaRelationEnum,
                                     ResourceThingDTO resourceThingDTO,
                                     ProtectedObjectAclDTO oneProtectedObjectAcl) {

        return this.checkFGAPermission(
                resourceThingDTO.id(),
                FgaObjectTypeEnum.DOCUMENT.toString(),
                fgaRelationEnum.toString(),
                oneProtectedObjectAcl.subjectType(),
                oneProtectedObjectAcl.subjectId()
        );

    }

}
