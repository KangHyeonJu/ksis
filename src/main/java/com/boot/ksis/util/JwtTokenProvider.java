package com.boot.ksis.util;

import com.boot.ksis.dto.login.JwtTokenDTO;
import com.boot.ksis.service.AccountService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final UserDetailsService userDetailsService;

    // secret값 가져와서 저장 ( 생성자 주입 )
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            UserDetailsService userDetailsService
    ) {
        this.userDetailsService = userDetailsService;
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = new SecretKeySpec(decodedKey, "HmacSHA256");
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtTokenDTO generateToken(Authentication authentication) {
         // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 15 * 60 * 1000); // 15분
//        Date accessTokenExpiresIn = new Date(now +  10 * 1000); // 10초
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 7L * 24 * 60 * 60 * 1000)) // 7일
//                .setExpiration(new Date(now +  10 * 1000)) // 10초
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtTokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e); // 서명 오류 또는 형식이 잘못된 토큰 처리
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e); // 토큰 만료 시 처리
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e); // 지원하지 않는 JWT 토큰 형식 처리
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e); // 토큰이 비어 있거나 잘못된 경우 처리
        }
        return false;
    }

    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료된 토큰의 경우에도 클레임 정보를 가져올 수 있음
        }
    }

    // Access Token을 사용해 accountId를 추출하는 메서드
    public String getAccountIdFromToken(String accessToken) {
        Claims claims = parseClaims(accessToken);
        return claims.getSubject();  // subject를 accountId로 사용하고 있으므로 이를 반환
    }

    public String generateAccessToken(String accountId) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(accountId); // 유저 디테일로 권한정보 갖고오는 과정
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(accountId)
                .claim("auth", authorities) // 액세스 토큰의 경우 auth, 자격증명에 대한 부분이 필요
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15분
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
