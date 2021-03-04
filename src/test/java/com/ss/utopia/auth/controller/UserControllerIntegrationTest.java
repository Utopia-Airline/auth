package com.ss.utopia.auth.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.net.ssl.SSLEngineResult.Status;
import javax.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.auth.AuthApplication;
import com.ss.utopia.auth.dto.LoginDto;
import com.ss.utopia.auth.dto.UpdateUserDto;
import com.ss.utopia.auth.dto.UserDto;
import com.ss.utopia.auth.entity.User;
import com.ss.utopia.auth.entity.UserRole;
import com.ss.utopia.auth.service.UserService;

@SpringBootTest(
		  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		  classes = AuthApplication.class
		)
		@AutoConfigureMockMvc
		@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerIntegrationTest {
	private final static String URI = "/api/users";
	  @Autowired
	  private MockMvc mockMvc;
	  
	  @Autowired
	  private PasswordEncoder passwordEncoder;

	  @SpyBean
	  UserService userService;
	  

	  @BeforeEach
	  void setUp() {
	  }

	  @AfterEach
	  void tearDown() {
	  }
	  
	  private String login() throws Exception {
		    // create request body with username and password
		    ObjectMapper objectMapper = new ObjectMapper();
		    LoginDto loginDto = new LoginDto("admin", "password");
		    // create JSON string of loginDto
		    String loginDtoJson = objectMapper.writeValueAsString(loginDto);

		    // mock api call
		    return this.mockMvc
		      .perform(MockMvcRequestBuilders.post("/api/session").contentType(MediaType.APPLICATION_JSON).content(loginDtoJson))
		      .andReturn().getResponse().getCookie("session").getValue();
	  }
	  
	  private String getUserDto(String username, String password, String givenName, String familyName, String email,
			  String phone, String role) throws JsonProcessingException {
		    ObjectMapper objectMapper = new ObjectMapper();
		    UserDto userDto = new UserDto( username, password, givenName, familyName, email, phone, role);
		    return objectMapper.writeValueAsString(userDto);
	  }
	  
	  private String buildAdminUser() throws JsonProcessingException {
		    User admin = new User(1001L, "admin", "password", new UserRole(1L, "ADMIN"),
		      "Admin", "Admin", "admin@utopia.com", "56760989888");
		    ObjectMapper objectMapper = new ObjectMapper();
		    return objectMapper.writeValueAsString(admin);
	  }
	  
	  private String buildUser() throws JsonProcessingException {
		    User admin = new User(1000L, "ytearerr12", "password", new UserRole(3L, "AGENT"),
		      "Yankee", "Teare", "ytearerr@google.pl", "5035073878");
		    ObjectMapper objectMapper = new ObjectMapper();
		    return objectMapper.writeValueAsString(admin);
	  }
	  
	  private String getUpdateUserDto(String username, String password, String givenName, String familyName, String email,
			  String phone) throws JsonProcessingException {
		    ObjectMapper objectMapper = new ObjectMapper();
		    UpdateUserDto userDto = new UpdateUserDto( username, password, givenName, familyName, email, phone);
		    return objectMapper.writeValueAsString(userDto);
		  }
	  
	  @Test
	  @DisplayName("Test to sign up a user")
	  void signup() throws Exception {
		  String userDtoJson = getUserDto("WSS", "password", "Sam", "Johnson", "email@gmail.com", "0123456789", "Admin");
		  String expectedUserDtoJson = getUserDto("WSS", passwordEncoder.encode("password"), "Sam", "Johnson", "email@gmail.com", "0123456789", "Admin");
		  this.mockMvc
		  .perform(MockMvcRequestBuilders.post(URI).contentType(MediaType.APPLICATION_JSON).content(userDtoJson))
		  .andExpect(status().isCreated());	  }
	  
	  @Test
	  @DisplayName("Test to update a user")
	  void updateUser() throws Exception {
		  String updateUserDtoJson = getUpdateUserDto("ytearerr12", "password", "Yankee", "Teare", "ytearerr@google.pl", "5035073878");
		  String expectedUserJson = buildUser();
		  String sessionCookie = login();
		  this.mockMvc
	      .perform(MockMvcRequestBuilders.put(URI+"/1000").cookie(new Cookie("session", sessionCookie))
	      .contentType(MediaType.APPLICATION_JSON).content(updateUserDtoJson)).andExpect(status().isOk())
	      .andExpect(MockMvcResultMatchers.content().json(expectedUserJson));
	  }
	  
	  @Test
	  @DisplayName("Test to get all users")
	  void getAllUsers() throws Exception {
		  String sessionCookie = login();
		  this.mockMvc
	      .perform(MockMvcRequestBuilders.get(URI).cookie(new Cookie("session", sessionCookie)))
	      .andExpect(status().isOk());
	  }
	  
	  @Test
	  @DisplayName("Test to get a user")
	  void getUserById() throws Exception {
		  String expectedUserJson = buildAdminUser();
		  String sessionCookie = login();
		  this.mockMvc
	      .perform(MockMvcRequestBuilders.get(URI+"/1001").cookie(new Cookie("session", sessionCookie)))
	      .andExpect(status().isOk())
	      .andExpect(MockMvcResultMatchers.content().json(expectedUserJson));
	  }
	  
	  @Test
	  @DisplayName("Test to delete a user")
	  void deleteUser() throws Exception {
		  String sessionCookie = login();
		  this.mockMvc
	      .perform(MockMvcRequestBuilders.delete(URI+"/1000").cookie(new Cookie("session", sessionCookie)))
	      .andExpect(status().isOk())
	      .andExpect(MockMvcResultMatchers.content().string("1"));
	  }
	  
}
