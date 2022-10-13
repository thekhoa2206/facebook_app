package com.web.dto.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.util.Map;
import java.util.Set;
@Data
@JsonRootName("data_error")
@JsonTypeName("data_error")
public class ValidateMessage {
    private int status;
    private Map<String, Object> errors;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Integer> errorCodes;
}
