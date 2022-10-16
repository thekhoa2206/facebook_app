package com.web.controller;

import com.web.common.CheckCommon;
import com.web.common.Common;
import com.web.config.sercurity.jwt.JwtAuthTokenFilter;
import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dao.jpa.AccountDao;
import com.web.dto.BaseResponse;
import com.web.dto.account.AccountResponse;
import com.web.dto.exception.FormValidateException;
import com.web.model.Account;
import com.web.repositories.AccountRepo;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SignupController extends BaseController{

    private final AccountRepo accountRepo;
    private final JwtAuthTokenFilter jwtAuthTokenFilter;
    private final JwtProvider tokenProvider;
    private final AccountDao accountDao;
    private final ModelMapper mapper;
    public SignupController(AccountRepo accountRepo, JwtAuthTokenFilter jwtAuthTokenFilter, JwtProvider tokenProvider, AccountDao accountDao, ModelMapper mapper) {
        super(tokenProvider, accountDao);
        this.accountRepo = accountRepo;
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
        this.tokenProvider = tokenProvider;
        this.accountDao = accountDao;
        this.mapper = mapper;
    }

    @PostMapping("/signup")
    public BaseResponse signup (@RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String password) {
        Account account = null;
        if (phoneNumber == null) {
            throw new FormValidateException("phoneNumber", "Số điện thoại không được để trống!");
        } else {
            CheckCommon.validatePhone(phoneNumber);
            account = accountDao.findAccountByPhone(phoneNumber);
            if(account != null)
                throw new FormValidateException("phoneNumber", "Số điện thoại đã được sử dụng!");
        }
        UUID uuid = UUID.randomUUID();
        CheckCommon.checkPassword(password);
        Account accountReq = new Account();
        accountReq.setPhoneNumber(phoneNumber);
        accountReq.setPassword(Common.genPassword(password));
        accountReq.setCreatedOn();
        val accountx = accountRepo.save(accountReq);
        val response = new BaseResponse();
        response.setCode(HttpStatus.OK);
        val accountResponse = mapper.map(accountx, AccountResponse.class);
        List<Object> data = new ArrayList<>();
        data.add(accountResponse);
        response.setMessage("Đăng kí thành công!");
        return response;
    }


}
