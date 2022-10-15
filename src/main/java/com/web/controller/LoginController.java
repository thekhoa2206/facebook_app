package com.web.controller;

import com.web.common.CheckCommon;
import com.web.common.Common;
import com.web.config.sercurity.jwt.JwtAuthTokenFilter;
import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dto.exception.FormValidateException;
import com.web.dto.exception.UnauthorizedException;
import com.web.model.Account;
import com.web.repositories.AccountRepo;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class LoginController extends BaseController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final AccountRepo accountRepo;
    private final JwtAuthTokenFilter jwtAuthTokenFilter;
    private final JwtProvider tokenProvider;

    public LoginController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, AccountRepo accountRepo, JwtAuthTokenFilter jwtAuthTokenFilter, JwtProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.accountRepo = accountRepo;
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
        this.tokenProvider = tokenProvider;
    }


    @GetMapping
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<?> authenticateUser(@Valid @RequestParam(required = false) String phoneNumber, @Valid @RequestParam(required = false) String password, final HttpServletResponse response) {
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
        return ResponseEntity
                .ok().contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .build();
    }



    @GetMapping
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String jwt = jwtAuthTokenFilter.getJwt(request); // lấy jwt từ request
        if(jwt == null)
            throw new UnauthorizedException("Không có thông tin đăng nhập!");

        if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
            String phone = tokenProvider.getUserNameFromJwtToken(jwt); // lấy username từ jwt

            if(phone == null)
                throw new UnauthorizedException("Thông tin số điện thoại của tài khoản sai!");
            else
            {
                val account  = accountRepo.findByPhoneNumber(phone);
                if(account == null)
                    throw new UnauthorizedException("Không tìm thấy tài khoản!");
                if(account != null){
                    val accountReq = account.get();
                    accountReq.setUuid(null);
                    accountRepo.save(accountReq);
                }
            }

        }

        return ResponseEntity
                .ok().contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
                .build();
    }
}
