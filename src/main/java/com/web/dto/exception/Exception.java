package com.web.dto.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Exception extends BaseException{
    private String code;
    private String message;
    private String note;

    public Exception(String code, String message, String note) {
        this.code = code;
        this.message = message;
        this.note = note;
    }
}
