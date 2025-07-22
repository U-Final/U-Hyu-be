package com.ureca.uhyu.domain.auth.jwt;

import com.ureca.uhyu.domain.user.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.access-token-expiration-time}")
    private long ACCESS_TOKEN_EXP;

    @Value("${jwt.refresh-token-expiration-days}")
    private int REFRESH_TOKEN_EXP;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // JWT token 생성
    public String generateToken(String userId, UserRole userRole) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", userRole.getUserRole()) // todo
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT로부터 subject를 꺼내서 사용자 식별자 Id 확인
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    // JWT로부터 role claim(토큰안의 데이터 조각) 추출
    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody().get("role", String.class);
    }

    public String getUserIdFromExpiredToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();  // 만료된 토큰에서 subject(userId) 추출
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.info("토큰 Validate 하네요~");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT 만료: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT 형식 오류: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT 서명 오류: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT 파라미터 오류 (null 또는 공백): {}", e.getMessage());
        } catch (Exception e) {
            log.warn("기타 오류: {}", e.getMessage());
        }
        return false;
    }

    public long getAccessTokenExp() {
        return ACCESS_TOKEN_EXP;
    }
}