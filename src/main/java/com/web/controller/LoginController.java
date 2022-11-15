package com.web.controller;

import com.web.common.CheckCommon;
import com.web.config.sercurity.jwt.JwtAuthTokenFilter;
import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dao.jpa.AccountDao;
import com.web.dto.BaseResponse;
import com.web.dto.account.AccountResponse;
import com.web.dto.exception.Exception;
import com.web.dto.exception.FormValidateException;
import com.web.dto.exception.UnauthorizedException;
import com.web.model.Account;
import com.web.repositories.AccountRepo;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class LoginController extends BaseController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final AccountRepo accountRepo;
    private final JwtAuthTokenFilter jwtAuthTokenFilter;
    private final JwtProvider tokenProvider;
    private final ModelMapper mapper;
    private final AccountDao accountDao;
    public LoginController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, AccountRepo accountRepo, JwtAuthTokenFilter jwtAuthTokenFilter, JwtProvider tokenProvider, ModelMapper mapper, AccountDao accountDao) {
        super(tokenProvider, accountDao);
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.accountRepo = accountRepo;
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
        this.tokenProvider = tokenProvider;
        this.mapper = mapper;
        this.accountDao = accountDao;
    }

    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestParam(required = false) String phoneNumber, @Valid @RequestParam(required = false) String password,
                              @Valid @RequestParam(required = false) String uuid) {
        if (phoneNumber == null) {
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");
        } else {
            CheckCommon.validatePhone(phoneNumber);
        }
        if(uuid == null){
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        if(phoneNumber.equals(password)){
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        Account accountReq = new Account();
        CheckCommon.checkPassword(password);
        val account  = accountRepo.findByPhoneNumber(phoneNumber);
        if(account == null)
            throw new Exception("9995","User is not validated", "Không có người dùng này");
        if(account != null){
            accountReq = account.get();
            if(!BCrypt.checkpw(password, account.get().getPassword()))
                throw new Exception("1014","Password wrong", "Mật khẩu sai");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(phoneNumber, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        accountReq.setUuid(uuid);
        accountReq.setToken(jwt);
        accountRepo.save(accountReq);
        val response = new BaseResponse();
        if(accountReq != null && jwt != null){
            List<Object> data = new ArrayList<>();
            val accountResponse = mapper.map(accountReq, AccountResponse.class);
            accountResponse.setToken(jwt);
            data.add(accountResponse);
            response.setData(data);
            response.setCode("1000");
            response.setMessage("OK");
        }
        return response;
    }
    @PostMapping("/logout")
    public BaseResponse logout(@RequestParam(required = false) String token) {
        String jwt = token; // lấy jwt từ request
        if(jwt == null)
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");

        val response = new BaseResponse();
        if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
            String phone = tokenProvider.getUserNameFromJwtToken(jwt); // lấy username từ jwt

            if(phone == null)
                throw new Exception("9995","User is not validated", "Không có người dùng này");
            else
            {
                val account  = accountRepo.findByPhoneNumber(phone);
                if(account == null)
                    throw new Exception("9995","User is not validated", "Không có người dùng này");
                if(account != null){
                    if(account.get().getToken() == null){
                        throw new Exception("9995","User is not validated", "Không có người dùng này");
                    } else if(!account.get().getToken().equals(jwt)){
                        throw new Exception("9995","User is not validated", "Không có người dùng này");
                    }else{
                        val accountReq = account.get();
                        accountReq.setToken(null);
                        accountRepo.save(accountReq);
                        if(accountReq != null){
                            response.setCode("1000");
                            response.setMessage("OK");
                        }
                    }
                }
            }
        }

        return response;
    }

}
