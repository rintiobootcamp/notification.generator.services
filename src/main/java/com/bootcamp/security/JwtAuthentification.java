/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootcamp.security;

import com.bootcamp.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 *
 * @author edwigegédéon
 */
public class JwtAuthentification {

    static final long EXPIRATIONTIME = 864_000_000; // 10 days
    static final String SECRET = "ThisIsASecret";
    static final String TOKEN_PREFIX = "PAG";
    static final String HEADER_STRING = "Authorization";

    public static String addAuthentication(/*HttpServletResponse res,*/User user) {
        String subject = user.getLogin() + user.getPassword();
        String JWT = Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        //res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
        String token = TOKEN_PREFIX + " " + JWT;

        return token;
    }

    public static /*Authentication*/ void getAuthentication(/*HttpServletRequest request*/String token) {
        //String token = request.getHeader(HEADER_STRING);
        //if (token != null) {
            // parse the token.
            String userString = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

//      return userString != null ?
//          new UsernamePasswordAuthenticationToken(userString, null, emptyList()) :
//          null;
        //}
        //return null;
    }
}

