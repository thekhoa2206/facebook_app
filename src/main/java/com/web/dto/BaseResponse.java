package com.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Setter
@Getter
public class BaseResponse {
    private HttpStatus code;
    private String message;
    private List<Object> data;
}
