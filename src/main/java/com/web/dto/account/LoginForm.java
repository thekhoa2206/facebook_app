package com.web.dto.account;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LoginForm {
    @NotNull(message = "Số điện thoại không được để trống!")
    private String phoneNumber;

    @NotNull(message = "Mật khẩu không được để trống!")
    private String password;
}
