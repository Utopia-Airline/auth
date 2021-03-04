package com.ss.utopia.auth.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import com.ss.utopia.auth.dto.LoginDto;
import com.ss.utopia.auth.entity.User;
import com.ss.utopia.auth.security.SessionCookieProvider;
import com.ss.utopia.auth.service.UserService;

import java.util.Optional;


@RestController
@RequestMapping("/api/session")
public class SessionController {
  @Autowired
  private UserService userService;
  @Autowired
  private SessionCookieProvider sessionCookieProvider;

  // TODO: 2/6/2021 split HttpServerErrorException to 2 types of error  
  // Todo: create login

  /**
   * Login a user using loginDto
   *
   * @param loginDto : { username, password }
   * @return
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {
    final User user = userService.loadAuthenticatedUser(loginDto.getUsername(), loginDto.getPassword())
      .orElseThrow(() -> new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Login Failed"));
    final Cookie sessionCookie = sessionCookieProvider.createSessionCookie(user)
      .orElseThrow(() -> new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Session cookie creation failed"));
    response.addCookie(sessionCookie);
  }

  /**
   * Login only an admin user using loginDto
   *
   * @param loginDto : { username, password }
   * @return
   */
  @PostMapping("/admin")
  @ResponseStatus(HttpStatus.CREATED)
  public void loginAdmin(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {
    final User user = userService.loadAuthenticatedUser(loginDto.getUsername(), loginDto.getPassword())
      .orElseThrow(() -> new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Login Failed"));
    // if user is not admin
    if (!userService.isUserAdmin(user))
      throw new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Login Failed");
    final Cookie sessionCookie = sessionCookieProvider.createSessionCookie(user)
      .orElseThrow(() -> new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Login Failed"));
    response.addCookie(sessionCookie);
  }

  // TODO: 2/6/2021 take this out 

  /**
   * Login only an agent user using loginDto
   *
   * @param loginDto : { username, password }
   * @return
   */
  @PostMapping("/agent")
  @ResponseStatus(HttpStatus.CREATED)
  public void loginAgent(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {
    final User user = userService.loadAuthenticatedUser(loginDto.getUsername(), loginDto.getPassword())
      .orElseThrow(() -> new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Login Failed"));
    // if user is not agent
    if (!userService.isUserAgent(user))
      throw new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Login Failed");
    final Cookie sessionCookie = sessionCookieProvider.createSessionCookie(user)
      .orElseThrow(() -> new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Login Failed"));
    response.addCookie(sessionCookie);
  }

  // TODO: 2/6/2021 /api/sessions/users/user/getsession
  @GetMapping
  public User getSession(@AuthenticationPrincipal UserDetails currentUser) {
    if (currentUser == null)
      return null;
    else
      return userService.getUserByUsername(currentUser.getUsername());
  }

  // TODO: 2/6/2021 POST /api/sessions/users/user/logout
  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(HttpServletResponse response) {
    final Cookie sessionCookie = userService.removeCookie()
      .orElseThrow(() -> new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Session cookie invalidation failed"));
    response.addCookie(sessionCookie);
  }
}
