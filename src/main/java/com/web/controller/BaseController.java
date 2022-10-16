package com.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.web.dto.exception.FormValidateException;
import com.web.dto.exception.NotFoundException;
import com.web.dto.exception.UnauthorizedException;
import com.web.dto.exception.ValidateMessage;
import com.web.model.Account;
import lombok.extern.apachecommons.CommonsLog;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommonsLog
public class BaseController {
    @Autowired
    public ObjectMapper json;
    @Autowired
    protected MessageSource messageSource;

    @ExceptionHandler(FormValidateException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidateMessage handleFormValidateException(FormValidateException ex, HttpServletRequest request) {

        val exceptionMessage = new ValidateMessage();
        Map<String, Object> errors = new HashMap<String, Object>();
        exceptionMessage.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        if (ex.getBindingResult() != null) {
            for (val error : ex.getBindingResult().getFieldErrors()) {
                putFieldError(errors, error);
            }
        }
        if (ex.getMessageResult() != null) {
            for (val error : ex.getMessageResult().entrySet()) {
                val key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, error.getKey());
                if (!errors.containsKey(key)) {
                    try {
                        val keyLanguage = String.format("%s.%s", ex.getStackTrace()[0].getClassName(), key);
                        errors.put(key, messageSource.getMessage(keyLanguage, null, LocaleContextHolder.getLocale()));
                    } catch (NoSuchMessageException ns) {
                        val value = error.getValue();
                        errors.put(key, value);
                    }
                }
            }
        }
        if (ex.getParams() != null) {
            for (val param : ex.getParams().entrySet()) {
                val key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, param.getKey());
                val keyLanguage = String.format("%s.%s", ex.getStackTrace()[0].getClassName(), key);
                if (!errors.containsKey(key)) {
                    errors.put(key, messageSource.getMessage(keyLanguage, param.getValue(), LocaleContextHolder.getLocale()));
                }
            }
        }
        exceptionMessage.setErrorCodes(ex.getErrorCodes());

        exceptionMessage.setErrors(errors);
        return exceptionMessage;
    }
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Error handleNotFoundException(HttpServletResponse response, NotFoundException e) {
        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        // response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        if (e.getMessageResult() != null) {
            for (val error : e.getMessageResult().entrySet()) {
                val key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, error.getKey());
                val keyLanguage = String.format("%s.%s", e.getStackTrace()[0].getClassName(), key);
                return new Error(messageSource.getMessage(keyLanguage,null, LocaleContextHolder.getLocale()));
            }
        } else {
            val key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getMessage());
            val keyLanguage = String.format("%s.%s", e.getStackTrace()[0].getClassName(), key);
            try {
                return new Error(messageSource.getMessage(keyLanguage, null, LocaleContextHolder.getLocale()));
            } catch (NoSuchMessageException ns) {
                return new Error(e.getMessage());
            }
        }
        return new Error(e.getMessage());
    }

    @ExceptionHandler(ServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Error handleServerErrorException(HttpServletResponse response, ServerErrorException e) {
        response.setContentType("application/json");
        val key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getMessage());
        val keyLanguage = String.format("%s.%s", e.getStackTrace()[0].getClassName(), key);
        try {
            return new Error(messageSource.getMessage(keyLanguage, null, LocaleContextHolder.getLocale()));
        } catch (NoSuchMessageException ns) {
            return new Error(e.getMessage());
        }
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ValidateMessage handleUnauthorizedException(HttpServletResponse response, UnauthorizedException ex) {
        response.setContentType("application/json");
        val exceptionMessage = new ValidateMessage();
        Map<String, Object> errors = new HashMap<String, Object>();
        exceptionMessage.setStatus(HttpStatus.UNAUTHORIZED.value());
        if (ex.getBindingResult() != null) {
            for (val error : ex.getBindingResult().getFieldErrors()) {
                putFieldError(errors, error);
            }
        }
        if (ex.getMessageResult() != null) {
            for (val error : ex.getMessageResult().entrySet()) {
                val key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, error.getKey());
                if (!errors.containsKey(key)) {
                    try {
                        val keyLanguage = String.format("%s.%s", ex.getStackTrace()[0].getClassName(), key);
                        errors.put(key, messageSource.getMessage(keyLanguage, null, LocaleContextHolder.getLocale()));
                    } catch (NoSuchMessageException ns) {
                        val value = error.getValue();
                        errors.put(key, value);
                    }
                }
            }
        }
        if (ex.getParams() != null) {
            for (val param : ex.getParams().entrySet()) {
                val key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, param.getKey());
                val keyLanguage = String.format("%s.%s", ex.getStackTrace()[0].getClassName(), key);
                if (!errors.containsKey(key)) {
                    errors.put(key, messageSource.getMessage(keyLanguage, param.getValue(), LocaleContextHolder.getLocale()));
                }
            }
        }
        exceptionMessage.setErrorCodes(ex.getErrorCodes());
        exceptionMessage.setErrors(errors);
        return exceptionMessage;
        // return "{\"error\": \"[API] Invalid API key or access token
        // (unrecognized login or wrong password)\"}";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(HttpServletRequest req, HttpServletResponse response, Principal currentUser,
                              Exception exception) {
        response.setContentType("application/json; charset=utf-8");
        String message = StringUtils.EMPTY;
        StringWriter errors = new StringWriter();
//		}
        exception.printStackTrace(new PrintWriter(errors));
        String host = req.getHeader("X-Forwarded-Host");
        message = "Có lỗi xảy ra";
        log.error(String.format("%s%s?%s", host, req.getRequestURI(), req.getQueryString())+" exception: " + exception.getMessage(), exception);
        return "{\"error\": \"" + message + "\"}";
    }

    private void putFieldError(Map<String, Object> errors, FieldError error) {
        try {
            val key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, error.getDefaultMessage());
            val keyLanguage = String.format("%s.%s", error.getObjectName(), key);
            errors.put(key, messageSource.getMessage(keyLanguage, null, LocaleContextHolder.getLocale()));
        } catch (NoSuchMessageException | NullPointerException ex) {
            val key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, error.getField());
            val value = error.getDefaultMessage();
            if (!errors.containsKey(key)) {
                errors.put(key, value);
            }
        }
    }

    private void checkJwt(Account account) {

    }
}
