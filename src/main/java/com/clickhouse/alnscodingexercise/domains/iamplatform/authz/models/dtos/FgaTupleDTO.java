package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaObjectTypeEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaRelationEnum;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public record FgaTupleDTO(FgaObjectDTO subject, FgaRelationEnum relation, FgaObjectDTO object) {
    public FgaTupleDTO {
        if (subject == null || relation == null || object == null) {
            throw new IllegalArgumentException("All fields must be non-null");
        }
    }

    public static FgaTupleDTO of(FgaObjectDTO subject, FgaRelationEnum relation, FgaObjectDTO object) {
        return new FgaTupleDTO(subject, relation, object);
    }

    public static FgaTupleDTO of(String subject, FgaRelationEnum relation, String object) {
        return new FgaTupleDTO(FgaObjectDTO.of(subject), relation, FgaObjectDTO.of(object));
    }

    public static FgaTupleDTO of(PermissionSummaryDTO permissionSummary) {
        FgaTupleDTO fgaTupleDTO = null;
        FgaObjectDTO fgaSubjectObjectDTO = null;
        FgaRelationEnum fgaRelation = null;
        FgaObjectDTO fgaResourceObjectDTO = null;

        if (permissionSummary != null) {

            if (StringUtils.isNotEmpty(permissionSummary.getSubjectType()) && StringUtils.isNotEmpty(permissionSummary.getSubjectId())) {
                fgaSubjectObjectDTO = FgaObjectDTO.of(
                        FgaObjectTypeEnum.valueOf(permissionSummary.getSubjectType().toUpperCase()),
                        permissionSummary.getSubjectId()
                );
            }

            if (StringUtils.isNotEmpty(permissionSummary.getRelationshipType()) ) {
                fgaRelation = FgaRelationEnum.valueOf(permissionSummary.getRelationshipType().toUpperCase());
            }

            if (StringUtils.isNotEmpty(permissionSummary.getResourceType()) && StringUtils.isNotEmpty(permissionSummary.getResourceId())) {
                fgaResourceObjectDTO = FgaObjectDTO.of(
                        FgaObjectTypeEnum.valueOf(permissionSummary.getResourceType().toUpperCase()),
                        permissionSummary.getResourceId(),
                        (permissionSummary.getResourceRelationshipType() != null
                                ? FgaRelationEnum.valueOf(permissionSummary.getResourceRelationshipType().toUpperCase())
                                : null)
                );
            }

            fgaTupleDTO = new FgaTupleDTO(fgaSubjectObjectDTO, fgaRelation, fgaResourceObjectDTO);

        }

        return fgaTupleDTO;

    }

    public static List<FgaTupleDTO> of(List<PermissionSummaryDTO> permissionsSummaryList) {

        return permissionsSummaryList.stream()
                .map(FgaTupleDTO::of)
                .toList();

    }

    public ClientTupleKey toClientTupleKey() {
        return new ClientTupleKey()
                .user(subject.toString())
                .relation(relation.toString())
                ._object(object.toString());
    }


}