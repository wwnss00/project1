package com.example.marketproject.util;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${cookie.secure}")
    private boolean secure;

    @Value("${cookie.max-age}")
    private int maxAge;

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);  // ← yml에서 가져옴
        return cookie;
    }

    public Cookie deleteRefreshTokenCookie() {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}
