package com.web.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "UNAUTHORIZED")
public class UnauthorizedException extends BaseException {
}
