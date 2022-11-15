package com.web.config.sercurity.jwt;

import com.web.dto.account.UserPrinciple;
import com.web.dto.exception.Exception;
import com.web.dto.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpiration}")
    private int jwtExpiration;

    public String generateJwtToken(Authentication authentication) {

        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();

        return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            throw new Exception("1021","Invalid Token Code", "Mã Token không hợp lệ");
        } catch (MalformedJwtException e) {
            throw new Exception("1021","Invalid Token Code", "Mã Token không hợp lệ");
        } catch (ExpiredJwtException e) {
            throw new Exception("1021","Expired Tokens", "Phiên đăng nhập hết hạn");
        } catch (UnsupportedJwtException e) {
            throw new Exception("1021","Invalid Token Code", "Token không được hỗ trợ");
        } catch (IllegalArgumentException e) {
            throw new Exception("9999","Exception error", "Lỗi exception");
        }
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String getUuidFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getId();
    }
}
