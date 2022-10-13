package com.web.dto.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ResponseStatus(value = HttpStatus.OK, reason = "Item not found")
public class NotFoundException extends BaseException{
    private static final long serialVersionUID = 1L;

    protected Map<String, Object> messageResult;
    protected HashMap<String, Object[]> params;

    private String message = "Không tìm thấy đối tượng";

    public NotFoundException() {

    }

    public NotFoundException(Map<String, Object> messageResult) {
        this.messageResult = messageResult;
    }

    public NotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public NotFoundException(HashMap<String, Object[]> params) {
        this.params = params;
    }
}
