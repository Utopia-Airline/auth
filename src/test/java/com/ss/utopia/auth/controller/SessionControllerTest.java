package com.ss.utopia.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.auth.dao.UserDao;
import com.ss.utopia.auth.dto.LoginDto;
import com.ss.utopia.auth.entity.User;
import com.ss.utopia.auth.entity.UserRole;
import com.ss.utopia.auth.security.SessionCookieProvider;
import com.ss.utopia.auth.security.jwt.JwtProvider;
import com.ss.utopia.auth.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import javax.servlet.http.Cookie;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {
  private final static String URI = "/api/session";
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;
  @MockBean
  private SessionCookieProvider sessionCookieProvider;
  @MockBean
  private JwtProvider jwtProvider;
  @MockBean
  private UserDao userDao;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("Test login")
  void login() throws Exception {
    String cookieString = "eyJqd3QiOiJleUpoYkdjaU9pSlNVekkxTmlKOS5leUp6ZFdJaU9pSXpaVGtpTENKcFlYUWlPakUyTVRJeU1Ua3dOREFzSW1WNGNDSTZNVFl4TXpReU9EWTBNSDAucV9DYk41UnFDTk10MXZOdDdBQWktczE2NXhhUE5PR2h1MDllY0xzV0tZdHk0bkhIWUZpcW55cWkzdXhUVk9SQm1DLVlIbE12YmlqS2t2V1lVemR1SWFqT3paU1o1NVd1QkVOdnRFOXFEQkdtajJjOS15NHBIMUVZR3VNSVNoWHppV1dXbG1oWFdwOWktaUZpUkpmYjVZN1pkN1YxUkJydHpsVWRsNlg5am5VLThTUUNibVZsZlNxZ0czRk9mcWl3ZFZDOVljaTdjMWFBLXRHQlRBRFIxRkFRcE1UOVdDM3k2dGJCYm01emplQ0tfa3NfT1VSVV9rYUVEamU3NW0tTXNIaExqZ0JKRTJkUzBjLVlsLWgzY3hLZUdRR05kT2NzdGdwTlJpLTNIR3hGUkdzWW45WlZmNGNoZEVrOG5LTngtQkFtZmJlcDFaVG52NEJnanpqU0hRIn0%3D";
    Cookie cookie = new Cookie("session", cookieString);
    cookie.setPath("/");
    User userToReturn =
      new User(32434L, "customer32434", "password", new UserRole(2L, "CUSTOMER"),
        "fname", "lname", "flcust@gmail.com", "2028769868");
    when(userService.loadAuthenticatedUser(any(), any())).thenReturn(Optional.of(userToReturn));
    when(sessionCookieProvider.createSessionCookie(any())).thenReturn(Optional.of(cookie));
    ObjectMapper objectMapper = new ObjectMapper();
    LoginDto loginDto = new LoginDto("customer32434", "password");
    // request body with username and password
    String loginDtoJson = objectMapper.writeValueAsString(loginDto);


    this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI)
        .contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andExpect(status().isCreated())
      .andDo(result -> System.out.println("user cookie: " + result.getResponse().getCookie("session")))
      .andExpect(MockMvcResultMatchers.cookie().value("session", cookieString));
  }

  @Test
  @DisplayName("Test login as an admin; Post HttpStatus.CREATED")
  void loginAdmin() throws Exception {
    String cookieString = "eyJqd3QiOiJleUpoYkdjaU9pSlNVekkxTmlKOS5leUp6ZFdJaU9pSXpaVGtpTENKcFlYUWlPakUyTVRJeU1Ua3dOREFzSW1WNGNDSTZNVFl4TXpReU9EWTBNSDAucV9DYk41UnFDTk10MXZOdDdBQWktczE2NXhhUE5PR2h1MDllY0xzV0tZdHk0bkhIWUZpcW55cWkzdXhUVk9SQm1DLVlIbE12YmlqS2t2V1lVemR1SWFqT3paU1o1NVd1QkVOdnRFOXFEQkdtajJjOS15NHBIMUVZR3VNSVNoWHppV1dXbG1oWFdwOWktaUZpUkpmYjVZN1pkN1YxUkJydHpsVWRsNlg5am5VLThTUUNibVZsZlNxZ0czRk9mcWl3ZFZDOVljaTdjMWFBLXRHQlRBRFIxRkFRcE1UOVdDM3k2dGJCYm01emplQ0tfa3NfT1VSVV9rYUVEamU3NW0tTXNIaExqZ0JKRTJkUzBjLVlsLWgzY3hLZUdRR05kT2NzdGdwTlJpLTNIR3hGUkdzWW45WlZmNGNoZEVrOG5LTngtQkFtZmJlcDFaVG52NEJnanpqU0hRIn0%3D";
    Cookie cookie = new Cookie("session", cookieString);
    cookie.setPath("/");
    User userToReturn =
      new User(32434L, "admin32434", "password", new UserRole(2L, "ADMIN"),
        "fname", "lname", "fladm@gmail.com", "2028769868");
    when(userService.loadAuthenticatedUser(any(), any())).thenReturn(Optional.of(userToReturn));
    when(sessionCookieProvider.createSessionCookie(any())).thenReturn(Optional.of(cookie));
    when(userService.isUserAdmin(any())).thenReturn(true);
    ObjectMapper objectMapper = new ObjectMapper();
    LoginDto loginDto = new LoginDto("admin32434", "password");
    // request body with username and password
    String loginDtoJson = objectMapper.writeValueAsString(loginDto);


    this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI + "/admin")
        .contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andExpect(status().isCreated())
      .andDo(result -> System.out.println("user cookie: " + result.getResponse().getCookie("session")))
      .andExpect(MockMvcResultMatchers.cookie().value("session", cookieString));
  }

  @Test
  @DisplayName("Test login as an agent; Post HttpStatus.CREATED")
  void loginAgent() throws Exception {
    String cookieString = "eyJqd3QiOiJleUpoYkdjaU9pSlNVekkxTmlKOS5leUp6ZFdJaU9pSXpaVGtpTENKcFlYUWlPakUyTVRJeU1Ua3dOREFzSW1WNGNDSTZNVFl4TXpReU9EWTBNSDAucV9DYk41UnFDTk10MXZOdDdBQWktczE2NXhhUE5PR2h1MDllY0xzV0tZdHk0bkhIWUZpcW55cWkzdXhUVk9SQm1DLVlIbE12YmlqS2t2V1lVemR1SWFqT3paU1o1NVd1QkVOdnRFOXFEQkdtajJjOS15NHBIMUVZR3VNSVNoWHppV1dXbG1oWFdwOWktaUZpUkpmYjVZN1pkN1YxUkJydHpsVWRsNlg5am5VLThTUUNibVZsZlNxZ0czRk9mcWl3ZFZDOVljaTdjMWFBLXRHQlRBRFIxRkFRcE1UOVdDM3k2dGJCYm01emplQ0tfa3NfT1VSVV9rYUVEamU3NW0tTXNIaExqZ0JKRTJkUzBjLVlsLWgzY3hLZUdRR05kT2NzdGdwTlJpLTNIR3hGUkdzWW45WlZmNGNoZEVrOG5LTngtQkFtZmJlcDFaVG52NEJnanpqU0hRIn0%3D";
    Cookie cookie = new Cookie("session", cookieString);
    cookie.setPath("/");
    User userToReturn =
      new User(32434L, "agent32434", "password", new UserRole(2L, "AGENT"),
        "fname", "lname", "flagt@gmail.com", "2028769868");
    when(userService.loadAuthenticatedUser(any(), any())).thenReturn(Optional.of(userToReturn));
    when(sessionCookieProvider.createSessionCookie(any())).thenReturn(Optional.of(cookie));
    when(userService.isUserAgent(any())).thenReturn(true);
    ObjectMapper objectMapper = new ObjectMapper();
    LoginDto loginDto = new LoginDto("agent32434", "password");
    // request body with username and password
    String loginDtoJson = objectMapper.writeValueAsString(loginDto);


    this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI + "/agent")
        .contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andExpect(status().isCreated())
      .andDo(result -> System.out.println("user cookie: " + result.getResponse().getCookie("session")))
      .andExpect(MockMvcResultMatchers.cookie().value("session", cookieString));
  }

  @Test
  @DisplayName("Test get session information; Get")
  @WithMockUser("customer32434")
  void getSession() throws Exception {
    String currentUsername = "customer32434";
    User userToReturn =
      new User(32434L, currentUsername, "password", new UserRole(2L, "CUSTOMER"),
        "fname", "lname", "flcust@gmail.com", "2028769868");
    when(userService.getUserByUsername(any())).thenReturn(userToReturn);
    ObjectMapper objectMapper = new ObjectMapper();
    String userInfoJsonString = objectMapper.writeValueAsString(userToReturn);
    this.mockMvc
      .perform(MockMvcRequestBuilders.get(URI).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andDo(result -> System.out.println("user info: " + result.getResponse().getContentAsString()))
      .andExpect(MockMvcResultMatchers.content().json(userInfoJsonString));
  }

  @Test
  @DisplayName("Test logout; delete; HttpStatus.NO_CONTENT")
  void logout() throws Exception {
    String cookieString = "eyJqd3QiOiJleUpoYkdjaU9pSlNVekkxTmlKOS5leUp6ZFdJaU9pSXpaVGtpTENKcFlYUWlPakUyTVRJeU1Ua3dOREFzSW1WNGNDSTZNVFl4TXpReU9EWTBNSDAucV9DYk41UnFDTk10MXZOdDdBQWktczE2NXhhUE5PR2h1MDllY0xzV0tZdHk0bkhIWUZpcW55cWkzdXhUVk9SQm1DLVlIbE12YmlqS2t2V1lVemR1SWFqT3paU1o1NVd1QkVOdnRFOXFEQkdtajJjOS15NHBIMUVZR3VNSVNoWHppV1dXbG1oWFdwOWktaUZpUkpmYjVZN1pkN1YxUkJydHpsVWRsNlg5am5VLThTUUNibVZsZlNxZ0czRk9mcWl3ZFZDOVljaTdjMWFBLXRHQlRBRFIxRkFRcE1UOVdDM3k2dGJCYm01emplQ0tfa3NfT1VSVV9rYUVEamU3NW0tTXNIaExqZ0JKRTJkUzBjLVlsLWgzY3hLZUdRR05kT2NzdGdwTlJpLTNIR3hGUkdzWW45WlZmNGNoZEVrOG5LTngtQkFtZmJlcDFaVG52NEJnanpqU0hRIn0%3D";
    Cookie cookie = new Cookie("session", cookieString);
    cookie.setPath("/");
    when(userService.removeCookie()).thenReturn(Optional.of(cookie));
    this.mockMvc
      .perform(MockMvcRequestBuilders.delete(URI).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent())
      .andDo(result -> System.out.println("cookie: " + result.getResponse().getCookie("session").getValue()))
      .andExpect(MockMvcResultMatchers.cookie().value("session", cookieString));
  }
}