package com.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BaseResponse {
    private String code;
    private String message;
    private List<Object> data;
}
