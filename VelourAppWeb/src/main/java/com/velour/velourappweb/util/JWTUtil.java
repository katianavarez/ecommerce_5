/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.velour.velourappweb.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 *
 * @author katia
 */
public class JWTUtil {

    private static final String SECRET = "mi_super_secreta_firma_jwt_1234567891011";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generarToken(String usuario) {
        return Jwts.builder()
                .subject(usuario)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60))  // UNA HORA
                .signWith(KEY)
                .compact();
    }

    public static String validarToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
