package com.web.dto.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class FormValidateException extends BaseException {
    private static final long serialVersionUID = 1L;
    protected BindingResult bindingResult;
    protected Map<String, Object> messageResult;
    protected Set<Integer> errorCodes;
    protected HashMap<String, Object[]> params;

    public FormValidateException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public FormValidateException(Map<String, Object> messageResult) {
        this.messageResult = messageResult;
    }

    public FormValidateException(BindingResult bindingResult,
                                 Map<String, Object> messageResult) {
        this.bindingResult = bindingResult;
        this.messageResult = messageResult;
    }

    public FormValidateException(String key, Object message) {
        messageResult = new HashMap<String, Object>();
        messageResult.put(key, message);
    }

    public FormValidateException(Map<String, Object> messageResult, Set<Integer> errorCodes) {
        this.messageResult = messageResult;
        this.errorCodes = errorCodes;
    }

    public FormValidateException(HashMap<String, Object[]> params) {
        this.params = params;
    }
}
