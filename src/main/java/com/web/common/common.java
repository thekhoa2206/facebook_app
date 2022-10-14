package com.web.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class common {
    public static String genPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
