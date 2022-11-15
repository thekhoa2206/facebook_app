package com.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dao.jpa.AccountDao;
import com.web.dto.exception.NotFoundException;
import com.web.dto.exception.ValidateMessage;
import com.web.model.Account;
import lombok.extern.apachecommons.CommonsLog;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

@CommonsLog
public class BaseController {
    @Autowired
    public ObjectMapper json;
    @Autowired
    protected MessageSource messageSource;
    private final JwtProvider tokenProvider;
    private final AccountDao accountDao;

    public BaseController(JwtProvider tokenProvider, AccountDao accountDao) {
        this.tokenProvider = tokenProvider;
        this.accountDao = accountDao;
    }


    @ExceptionHandler(com.web.dto.exception.Exception.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidateMessage handleFormValidateException(com.web.dto.exception.Exception ex, HttpServletRequest request) {

        val exceptionMessage = new ValidateMessage();
        exceptionMessage.setMessage(ex.getMessage());
        exceptionMessage.setCode(ex.getCode());
        exceptionMessage.setNote(ex.getNote());
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



    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ValidateMessage handleError(HttpServletRequest req, HttpServletResponse response, Principal currentUser,
                                                       Exception exception) {
        ValidateMessage exception1 = new ValidateMessage();
        exception1.setNote("Lỗi exception");
        exception1.setCode("9999");
        exception1.setMessage("Exception error");
        return exception1;
    }

    public Account checkJwt(String token) {
        Account account = null;
        if(token == null || token.isEmpty()){
            throw new com.web.dto.exception.Exception("1013", "Token not empty", "Token không được để trống!");
        }
        if (token != null && tokenProvider.validateJwtToken(token)) {
            String phone = tokenProvider.getUserNameFromJwtToken(token);
            if(phone != null){
                account = accountDao.findAccountByPhone(phone);
                if(account == null){
                    throw new com.web.dto.exception.Exception("9994","No Data or end of list data", "Không có dữ liệu hoặc không còn dữ liệu account");
                }else{
                    if(!account.getToken().equals(token)){
                        throw new com.web.dto.exception.Exception("9998","Token is invalid", "Sai token");
                    }
                }
            }
        }
        return  account;
    }
}
