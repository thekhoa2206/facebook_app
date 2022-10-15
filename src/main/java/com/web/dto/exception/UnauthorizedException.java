package com.web.dto.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "UNAUTHORIZED")
public class UnauthorizedException extends BaseException {
    private static final long serialVersionUID = 1L;
    protected BindingResult bindingResult;
    protected Map<String, Object> messageResult;
    protected Set<Integer> errorCodes;
    protected HashMap<String, Object[]> params;

    public UnauthorizedException(String key, Object message) {
        messageResult = new HashMap<String, Object>();
        messageResult.put(key, message);
    }
}
