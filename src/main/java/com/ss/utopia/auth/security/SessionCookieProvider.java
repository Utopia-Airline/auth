package com.ss.utopia.auth.security;

import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.Cookie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.auth.dto.SessionCookieDto;
import com.ss.utopia.auth.entity.User;
import com.ss.utopia.auth.security.jwt.JwtProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionCookieProvider {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private JwtProvider jwtProvider;

  public Optional<Cookie> createSessionCookie(final User user) {
    try {
      final String token = jwtProvider.createToken(jwtProvider.createUserClaims(user));
      final SessionCookieDto sessionCookieDto = new SessionCookieDto();
      sessionCookieDto.setJwt(token);
      final Cookie sessionCookie = new Cookie("session", encode(objectMapper.writeValueAsString(sessionCookieDto)));
      sessionCookie.setPath("/");
      // sessionCookie.setHttpOnly(true);
      return Optional.of(sessionCookie);
    } catch (JsonProcessingException e) {
      return Optional.empty();
    }
  }

  public Optional<String> parseAuthJwt(final Cookie sessionCookie) {
    try {
      final String decoded = decode(sessionCookie.getValue());
      final SessionCookieDto sessionCookieDto = objectMapper.readValue(decoded, SessionCookieDto.class);
      return Optional.of(sessionCookieDto.getJwt());
    } catch (JsonProcessingException e) {
      return Optional.empty();
    }
  }

  private String encode(final String str) {
    return Base64.getEncoder().encodeToString(str.getBytes());
  }

  private String decode(final String str) {
    return new String(Base64.getDecoder().decode(str));
  }
}
