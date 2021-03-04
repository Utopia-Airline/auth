package com.ss.utopia.auth.service;

import com.ss.utopia.auth.dao.UserDao;
import com.ss.utopia.auth.entity.User;
import com.ss.utopia.auth.entity.UserRole;
import com.ss.utopia.auth.security.jwt.JwtConfiguration;
import com.ss.utopia.auth.security.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.Cookie;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {
  @InjectMocks
  private UserService userService;
  @Mock
  private UserDao userDao;
  @MockBean
  private JwtProvider jwtProvider;
  @MockBean
  private JwtConfiguration jwtConfiguration;
  @Mock
  private AuthenticationManager authenticationManager;

  @Test
  void loadUserByUsername() {
    User expectedUser = new User(1L, "admin32434", "password", new UserRole(1L, "ADMIN"), "fname", "lname",
        "flcust@gmail.com", "2028769868");
    Mockito.when(userDao.findByUsername("admin32434")).thenReturn(Optional.of(expectedUser));
    Mockito.when(userDao.findByUsername("tim")).thenReturn(Optional.empty());
    assertEquals(userService.loadUserByUsername("admin32434").getUsername(), expectedUser.getUsername());
    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("tim"));
  }

  @Test
  @DisplayName("Test loading Authenticated User")
  void loadAuthenticatedUser() throws Exception {
    User expectedUser = new User(32434L, "adming32434", "password", new UserRole(1L, "ADMIN"), "fname", "lname",
        "flcust@gmail.com", "2028769868");
    Mockito.when(userDao.findByUsername(Mockito.any())).thenReturn(Optional.of(expectedUser));
    User actualUser = userService.loadAuthenticatedUser("adming32434", "password").orElseThrow(Exception::new);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void signup() {
  }

  @Test
  void updateUser() {
  }

  @Test
  void deleteUser() {
  }

  @Test
  void getUserById() {
    final User expectedUser = new User(1l, "admin32434", "password", new UserRole(1L, "ADMIN"), "fname", "lname",
        "flcust@gmail.com", "2028769868");
    Mockito.when(userDao.findById(1l)).thenReturn(Optional.of(expectedUser));
    Mockito.when(userDao.findById(2l)).thenReturn(Optional.empty());
    assertEquals(userService.getUserById(1l), expectedUser);
    assertThrows(HttpServerErrorException.class, () -> userService.getUserById(2l));
  }

  @Test
  void verifyOwnershipAndReturnOwner() {
    final User expectedUser = new User(1l, "admin32434", "password", new UserRole(1L, "ADMIN"), "fname", "lname",
        "flcust@gmail.com", "2028769868");
    final UserDetails invalidUserDetails = new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return null;
      }

      @Override
      public String getUsername() {
        return null;
      }

      @Override
      public boolean isAccountNonExpired() {
        return false;
      }

      @Override
      public boolean isAccountNonLocked() {
        return false;
      }

      @Override
      public boolean isCredentialsNonExpired() {
        return false;
      }

      @Override
      public boolean isEnabled() {
        return false;
      }
    };
    final UserDetails validUserDetails = new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return null;
      }

      @Override
      public String getUsername() {
        return "admin32434";
      }

      @Override
      public boolean isAccountNonExpired() {
        return false;
      }

      @Override
      public boolean isAccountNonLocked() {
        return false;
      }

      @Override
      public boolean isCredentialsNonExpired() {
        return false;
      }

      @Override
      public boolean isEnabled() {
        return false;
      }
    };
    assertThrows(HttpServerErrorException.class, () -> userService.verifyOwnershipAndReturnOwner(null, null));
    assertThrows(HttpServerErrorException.class,
        () -> userService.verifyOwnershipAndReturnOwner(expectedUser, invalidUserDetails));
    assertEquals(userService.verifyOwnershipAndReturnOwner(expectedUser, validUserDetails), expectedUser);
  }

  @Test
  @DisplayName("Test remove cookie")
  void removeCookie() {
    Cookie expectedCookie = new Cookie("session", null);
    expectedCookie.setPath("/");
    Cookie actualCookie = userService.removeCookie().get();
    assertEquals(expectedCookie.getName(), actualCookie.getName());
    assertEquals(expectedCookie.getValue(), actualCookie.getValue());
    assertEquals(expectedCookie.getPath(), actualCookie.getPath());
  }

  @Test
  @DisplayName("Mock Test getting a user by username from DB")
  void getUserByUsername() {
    User expectedUser = new User(32434L, "adming32434", "password", new UserRole(1L, "ADMIN"), "fname", "lname",
        "flcust@gmail.com", "2028769868");
    Mockito.when(userDao.findByUsername(Mockito.any())).thenReturn(Optional.of(expectedUser));
    User actualUser = userService.getUserByUsername("adming32434");
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Test whether is a user is an admin. should return true")
  void isUserAdmin() {
    User user = new User(32434L, "adming32434", "password", new UserRole(1L, "ADMIN"), "fname", "lname",
        "flcust@gmail.com", "2028769868");
    boolean actualIsAdmin = userService.isUserAdmin(user);
    assertEquals(true, actualIsAdmin);
  }

  @Test
  @DisplayName("Test whether is a user is an agent. should return true")
  void isUserAgent() {
    User user = new User(32434L, "agent32434", "password", new UserRole(3L, "AGENT"), "fname", "lname",
        "flcust@gmail.com", "2028769868");
    boolean actualIsAgent = userService.isUserAgent(user);
    assertEquals(true, actualIsAgent);
  }
}