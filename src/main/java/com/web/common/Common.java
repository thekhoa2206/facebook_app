package com.web.common;

import com.web.model.Account;
import lombok.val;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;

public class Common {
    public static String genPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    // Hàm decode jwt => để lấy uuid
    public static String decodeJwt(String jwt){
        String[] chunks = jwt.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        //String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        return payload;
    }

    //Hàm check jwt đúng phiên đăng nhập
    // nếu sai => log lỗi tài khoản đã được đăng nhập nơi khác
    public static boolean checkJwt(String jwt, Account account){
        if(account != null && jwt != null){
            val uuid = decodeJwt(jwt);
            if(uuid != null && account.getUuid() != null){
                if(uuid.equals(account.getUuid())){
                    return true;
                }
            }
        }
        return false;
    }

}
