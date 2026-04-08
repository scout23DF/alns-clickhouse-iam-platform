package com.clickhouse.alnscodingexercise.domains.shared.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class GenericErrorResponseDTO {
    private int httpStatusCode;
    private String message;
    private String error;
    private Exception occurredException;

    public GenericErrorResponseDTO(String message) {
        this.message = message;
    }

    public GenericErrorResponseDTO(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public GenericErrorResponseDTO(List<ObjectError> allErrors, String error) {
        this.error = error;
        String temp = allErrors.stream().map(e -> {
            if (e instanceof FieldError) {
                return "{\"field\":\"" + ((FieldError) e).getField() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
            } else {
                return "{\"object\":\"" + e.getObjectName() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
            }
        }).collect(Collectors.joining(","));
        this.message = "[" + temp + "]";
    }

}
