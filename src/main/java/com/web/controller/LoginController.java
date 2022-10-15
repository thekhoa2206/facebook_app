package com.web.controller;

import com.web.common.CheckCommon;
import com.web.common.Common;
import com.web.config.sercurity.jwt.JwtAuthTokenFilter;
import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dto.BaseResponse;
import com.web.dto.account.AccountResponse;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LoginController extends BaseController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final AccountRepo accountRepo;
    private final JwtAuthTokenFilter jwtAuthTokenFilter;
    private final JwtProvider tokenProvider;
    private final ModelMapper mapper;
    public LoginController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, AccountRepo accountRepo, JwtAuthTokenFilter jwtAuthTokenFilter, JwtProvider tokenProvider, ModelMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.accountRepo = accountRepo;
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
        this.tokenProvider = tokenProvider;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestParam(required = false) String phoneNumber, @Valid @RequestParam(required = false) String password) {
        if (phoneNumber == null) {
            throw new FormValidateException("phoneNumber", "Số điện thoại không được để trống!");
        } else {
            CheckCommon.validatePhone(phoneNumber);
        }
        Account accountReq = new Account();
        CheckCommon.checkPassword(password);
        val account  = accountRepo.findByPhoneNumber(phoneNumber);
        if(account == null)
            throw new FormValidateException("account", "Không tìm thấy tài khoản!");
        if(account != null){
            accountReq = account.get();
            if(!BCrypt.checkpw(password, account.get().getPassword()))
                throw new FormValidateException("password", "Mật khẩu sai!");

        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(phoneNumber, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        String uuid = Common.decodeJwt(jwt);
        if(accountReq.getUuid() != null)
            if(accountReq.getUuid().equals(uuid))
                throw new FormValidateException("account", "Tài khoản đã được đăng nhập!");
        accountReq.setUuid(uuid);
        accountRepo.save(accountReq);
//        Cookie cookie = new Cookie("jwt ", jwt);
//        cookie.setMaxAge(100);
//        response.addCookie(cookie);
        val response = new BaseResponse();
        if(accountReq != null && jwt != null){
            List<Object> data = new ArrayList<>();
            val accountResponse = mapper.map(accountReq, AccountResponse.class);
            accountResponse.setToken("Bear " + jwt);
            data.add(accountResponse);
            response.setData(data);
            response.setCode(HttpStatus.OK);
            response.setMessage("Đăng nhập thành công!");
        }
        return response;
    }
    @PostMapping("/logout")
    public BaseResponse logout(HttpServletRequest request) {
        String jwt = jwtAuthTokenFilter.getJwt(request); // lấy jwt từ request
        if(jwt == null)
            throw new UnauthorizedException("logout.infomation","Không có thông tin đăng nhập!");

        val response = new BaseResponse();
        if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
            String phone = tokenProvider.getUserNameFromJwtToken(jwt); // lấy username từ jwt

            if(phone == null)
                throw new UnauthorizedException("logout.phone","Thông tin số điện thoại của tài khoản sai!");
            else
            {
                val account  = accountRepo.findByPhoneNumber(phone);
                if(account == null)
                    throw new UnauthorizedException("logout.account","Không tìm thấy tài khoản!");
                if(account != null){
                    if(account.get().getUuid() == null)
                        throw new UnauthorizedException("logout.account","Tài khoản không xác định!");

                    val accountReq = account.get();
                    accountReq.setUuid(null);
                    accountRepo.save(accountReq);
                    if(accountReq != null){
                        response.setCode(HttpStatus.OK);
                        response.setMessage("Đăng xuất thành công!");
                    }
                }
            }


        }

        return response;
    }

}
