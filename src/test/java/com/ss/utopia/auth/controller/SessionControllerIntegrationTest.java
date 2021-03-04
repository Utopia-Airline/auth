package com.ss.utopia.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.auth.AuthApplication;
import com.ss.utopia.auth.dto.LoginDto;
import com.ss.utopia.auth.entity.User;
import com.ss.utopia.auth.entity.UserRole;
import com.ss.utopia.auth.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = AuthApplication.class
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class SessionControllerIntegrationTest {
  private final static String URI = "/api/session";
  @Autowired
  private MockMvc mockMvc;

  @SpyBean
  UserService userService;


  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  /**
   * login an admin user to the auth service
   *
   * @return
   * @throws Exception
   */
  private String login() throws Exception {
    // create request body with username and password
    ObjectMapper objectMapper = new ObjectMapper();
    LoginDto loginDto = new LoginDto("admin", "password");
    // create JSON string of loginDto
    String loginDtoJson = objectMapper.writeValueAsString(loginDto);

//    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
//      .apply(sharedHttpSession()).build();
    // mock api call
    return this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI).contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andReturn().getResponse().getCookie("session").getValue();
  }

  private String buildAdminUser() throws JsonProcessingException {
    User admin = new User(1001L, "admin", "password", new UserRole(1L, "ADMIN"),
      "Admin", "Admin", "admin@utopia.com", "56760989888");
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(admin);
  }

  private String buildAgentUser() throws JsonProcessingException {
    User admin = new User(332L, "aeggleton97", "password", new UserRole(3L, "AGENT"),
      "Alix", "Eggleton", "aeggleton97@ucla.edu", "9241933200");
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(admin);
  }

  private String buildCustomerUser() throws JsonProcessingException {
    User admin = new User(2L, "tmckernan1", "password", new UserRole(2L, "CUSTOMER"),
      "Trever", "McKernan", "tmckernan1@hibu.com", "1138845977");
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(admin);
  }

  /**
   * Build a json string of Login Dto using username and password
   *
   * @param username
   * @param password
   * @return
   * @throws JsonProcessingException
   */
  private String getLoginDto(String username, String password) throws JsonProcessingException {
    // create request body with username and password
    ObjectMapper objectMapper = new ObjectMapper();
    LoginDto loginDto = new LoginDto(username, password);
    // create JSON string of loginDto
    return objectMapper.writeValueAsString(loginDto);
  }

  @Test
  @DisplayName("Test login with wrong username and password")
  void loginUnauthorized() throws Exception {
    // create request body with username and password
    // create JSON string of loginDto
    String loginDtoJson = getLoginDto("invalidUsername2423r3rfgr", "password");
    // mock api call
    this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI).contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andExpect(status().isUnauthorized())
      .andDo(result -> System.out.println("user cookie: " + result.getResponse().getCookie("session")))
      .andExpect(MockMvcResultMatchers.cookie().doesNotExist("session"));
  }

  @Test
  @DisplayName("Test login with right username and password")
  void loginAuthorized() throws Exception {
    // create request body with username and password
    // create JSON string of loginDto
    String loginDtoJson = getLoginDto("admin", "password");
    // mock api call
    this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI).contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andExpect(status().isCreated())
      .andDo(result -> System.out.println("user cookie: " + result.getResponse().getCookie("session").getValue()))
      .andExpect(MockMvcResultMatchers.cookie().exists("session"));
  }

  @Test
  @DisplayName("Test an admin is logging in as an authenticated admin")
  void loginAdmin() throws Exception {
    // create request body with username and password
    // create JSON string of loginDto
    String loginDtoJson = getLoginDto("admin", "password");
    // mock api call
    this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI + "/admin").contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andExpect(status().isCreated())
      .andDo(result -> System.out.println("admin cookie: " + result.getResponse().getCookie("session").getValue()))
      .andExpect(MockMvcResultMatchers.cookie().exists("session"));
  }

  @Test
  @DisplayName("Test a customer is logging in as an admin")
  void loginAdminCustomer() throws Exception {
    // create request body with username and password
    // create JSON string of loginDto
    String loginDtoJson = getLoginDto("acrackettl7", "password");

    // mock api call
    this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI + "/admin").contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andExpect(status().isUnauthorized())
      .andExpect(MockMvcResultMatchers.cookie().doesNotExist("session"));
  }

  @Test
  @DisplayName("Test an agent is logging in as an authenticated agent")
  void loginAgent() throws Exception {
    // create request body with username and password
    // create JSON string of loginDto
    String loginDtoJson = getLoginDto("hfortey72", "password");
    // mock api call
    this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI + "/agent").contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andExpect(status().isCreated())
      .andDo(result -> System.out.println("agent cookie: " + result.getResponse().getCookie("session").getValue()))
      .andExpect(MockMvcResultMatchers.cookie().exists("session"));
  }

  @Test
  @DisplayName("Test an admin is logging in as an agent")
  void loginAgentAsAdmin() throws Exception {
    // create request body with username and password
    // create JSON string of loginDto
    String loginDtoJson = getLoginDto("admin", "password");
    // mock api call
    this.mockMvc
      .perform(MockMvcRequestBuilders.post(URI + "/agent").contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
      .andExpect(status().isUnauthorized())
      .andExpect(MockMvcResultMatchers.cookie().doesNotExist("session"));
  }

  @Test
  @DisplayName("Test get session info for an already logged in user")
  void getSessionLoggedIn() throws Exception {
    String userToReturnJson = buildAdminUser();
    // mock api calls
    String sessionCookie = login();
    System.out.println("session cookie: " + sessionCookie);
    this.mockMvc
      .perform(MockMvcRequestBuilders.get(URI).cookie(new Cookie("session", sessionCookie)))
      .andExpect(status().isOk())
      .andDo(result -> System.out.println("user content: " + result.getResponse().getContentAsString()))
      .andExpect(MockMvcResultMatchers.content().json(userToReturnJson));
  }

  @Test
  @DisplayName("Test get session info for a not logged in user with no cookie")
  void getSessionNotLoggedIn() throws Exception {
    // mock api calls
    this.mockMvc
      .perform(MockMvcRequestBuilders.get(URI))
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.content().string(""));
  }

  @Test
  @DisplayName("Test get session info for a not logged in user with invalid cookie")
  void getSessionInvalidCookie() throws Exception {
    String invalidSessionCookie = "eyJqd3QiOiJleUpoYkdjaU9pSlNVekkxTmlKOS5leUp6ZFdJaU9pSXpaVGtpTENgrregKcFlYUWlPakUyTVRJM01qYzJNVGNzSW1WNGNDSTZNVFl4TXprek56SXhOMzAuSlhYWkc2NThtbjdKgrecHRVVjl3dk1YRUpyODROcHJUSmFlUjVTTlRpRjZVbjV1YU1UWWExR01Rb0ZaZ0kwdDRXcW9NZUpYSGFnOVZvRmJjWFBVRThtME4xdjNXT1ktQk9YSnZvNDc2R2JHT0NYZi1sZEpZOFNiVUcxSDdBWVJvRG0wWlN2dW0yM2lVVldhZmdYdkVkSzFYZ0pmWXJuVjBkUEN2ZUI4RkRXc0tlUnRpX1g0V3pMMU9teTJwcXNDaXZodTBSaF9Ya1FQN2RlR0ZIdkhfUVJDLXVwUldYUWphUm5RSFExNHFnRHQzeXZ5X2NLY0dESFl4Q0xBdEZGRVJ5WEJObXEwSmUtZzZaMk43eUZtMWJaOVZxeFRkbjZncXM5RHhkclN6Q2tFQ19vZ2JMUzlydDYyMFQtRm15a0U1VTZ2ak1mdUJXc0VSNUYtSGp0YnFmbHNRIn0=";
    // mock api calls
    this.mockMvc
      .perform(MockMvcRequestBuilders.get(URI).cookie(new Cookie("session", invalidSessionCookie)))
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.content().string(""));
  }

  @Test
  @DisplayName("Test logging out an already logged in user")
  void logoutUserLoggedIn() throws Exception {
    // mock api calls
    String sessionCookie = login();
    this.mockMvc
      .perform(MockMvcRequestBuilders.delete(URI).cookie(new Cookie("session", sessionCookie)))
      .andExpect(status().isNoContent())
      .andExpect(MockMvcResultMatchers.cookie().value("session", (String) null))
      .andExpect(MockMvcResultMatchers.content().string(""));
  }

  @Test
  @DisplayName("Test logging out a not logged in user")
  void logoutUserNotLoggedIn() throws Exception {
    this.mockMvc
      .perform(MockMvcRequestBuilders.delete(URI))
      .andExpect(status().isNoContent())
      .andExpect(MockMvcResultMatchers.cookie().value("session", (String) null))
      .andExpect(MockMvcResultMatchers.content().string(""));
  }

  @Test
  @DisplayName("Test logging out someone while removing cookie failed")
  void logoutExceptionRemovingSessionCookie() throws Exception {
    //Mock userService removeCookie to return null
    Mockito.when(userService.removeCookie()).thenReturn(Optional.empty());
    System.out.println("remove Cookie:" + userService.removeCookie());
    // mock api calls
    String sessionCookie = login();
    this.mockMvc
      .perform(MockMvcRequestBuilders.delete(URI).cookie(new Cookie("session", sessionCookie)))
      .andExpect(status().isInternalServerError())
      .andExpect(MockMvcResultMatchers.content().string(""));
  }
}