package com.web.controller;

import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dto.account.JwtResponse;
import com.web.dto.account.LoginForm;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/login")
public class LoginController extends BaseController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public LoginController(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }


    @GetMapping
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest, final HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);

        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setMaxAge(100);
        response.addCookie(cookie);
        System.out.println("cookie" + cookie);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}
