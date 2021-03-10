package com.ss.utopia.auth.controller;

import com.ss.utopia.auth.dto.SentUserDto;
import com.ss.utopia.auth.dto.ReceivedUserDto;
import com.ss.utopia.auth.entity.User;
import com.ss.utopia.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User signup(@RequestBody @Valid ReceivedUserDto receivedUserDto) {
    return userService.signup(receivedUserDto);
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public int deleteUser(@PathVariable("userId") Long userId) {
    User user = userService.getUserById(userId);
    return userService.deleteUser(user.getId());
  }

  @PutMapping("/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public User updateUser(@PathVariable Long userId, @RequestBody @Valid ReceivedUserDto receivedUserDto) {
    User user = userService.getUserById(userId);
    return userService.updateUser(user, receivedUserDto);
  }

  @GetMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public Page<SentUserDto> getAllUsers(@RequestParam Map<String,String> params) {
    return userService.getAll(params);
  }
  
  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public User getUserById(@PathVariable("userId") Long userId) {
    return userService.getUserById(userId);
  }

}
