package com.web.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse {
    private String id;
    private String name;
    private String token;
    private String avatar;
    private String active;
}
