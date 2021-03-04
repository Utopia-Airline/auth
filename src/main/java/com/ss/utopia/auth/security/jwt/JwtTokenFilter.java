package com.ss.utopia.auth.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.springframework.security.core.userdetails.User.withUsername;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ss.utopia.auth.dao.UserDao;
import com.ss.utopia.auth.security.SessionCookieProvider;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

  @Autowired
  private JwtProvider jwtProvider;
  @Autowired
  private SessionCookieProvider sessionCookieProvider;

  @Autowired
  private UserDao userDao;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    LOGGER.info("Process request to check for a JSON Web Token");
    final Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      Arrays.stream(cookies)
          // get session cookie
          .filter((cookie) -> /* cookie.isHttpOnly() && */ cookie.getPath() == null
              && cookie.getName().matches("^session$"))
          .findAny().ifPresent((sessionCookie) ->
          // parse token
          this.sessionCookieProvider.parseAuthJwt(sessionCookie).ifPresent(token ->
          // validate token
          this.jwtProvider.validateToken(token).ifPresent((claims) ->
          // get user by id
          this.userDao.findById(this.jwtProvider.getUserId(claims)).ifPresent((user) -> {
            // create user details from user
            final UserDetails userDetails = withUsername(user.getUsername()).password(user.getPassword())
                .authorities(user.getRole().getAuthority()).accountExpired(false).accountLocked(false)
                .credentialsExpired(false).disabled(false).build();
            SecurityContextHolder.getContext().setAuthentication(
                new PreAuthenticatedAuthenticationToken(userDetails, "", userDetails.getAuthorities()));
          }))));
    }
    filterChain.doFilter(request, response);
  }
}
