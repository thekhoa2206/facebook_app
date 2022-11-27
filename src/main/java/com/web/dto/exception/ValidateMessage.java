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
    private String code;
    private String message;
    //private String note;
}
