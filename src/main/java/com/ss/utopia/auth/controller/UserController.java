package com.ss.utopia.auth.controller;

import com.ss.utopia.auth.dto.UpdateUserDto;
import com.ss.utopia.auth.dto.UserDto;
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
  public User signup(@RequestBody @Valid UserDto userDto) {
    return userService.signup(userDto);
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public int deleteUser(@PathVariable("userId") Long userId) {
    User user = userService.getUserById(userId);
    return userService.deleteUser(user.getId());
  }

  @PutMapping("/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public User updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserDto userDto) {
    User user = userService.getUserById(userId);
    return userService.updateUser(user, userDto);
  }

  @GetMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public Page<User> getAllUsers(@RequestParam Map<String,String> params) {
    return userService.getAll(params);
  }
  
  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public User getUserById(@PathVariable("userId") Long userId) {
    return userService.getUserById(userId);
  }

}
