package com.ss.utopia.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ss.utopia.auth.AuthApplication;
import com.ss.utopia.auth.entity.User;
import com.ss.utopia.auth.entity.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.core.userdetails.User.withUsername;

@SpringBootTest(classes = AuthApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceIntegrationTest {

  @Autowired
  private UserService userService;

  private User getAdminUser() throws JsonProcessingException {
    return new User(1001L, "admin", "password", new UserRole(1L, "ADMIN"),
      "Admin", "Admin", "admin@utopia.com", "56760989888");
  }

  private User getAgentUser() throws JsonProcessingException {
    return new User(332L, "aeggleton97", "password", new UserRole(3L, "AGENT"),
      "Alix", "Eggleton", "aeggleton97@ucla.edu", "9241933200");
  }

  private User getCustomerUser() throws JsonProcessingException {
    return new User(2L, "tmckernan1", "password", new UserRole(2L, "CUSTOMER"),
      "Trever", "McKernan", "tmckernan1@hibu.com", "1138845977");
  }

  private User getFakeUser() throws JsonProcessingException {
    return new User(-432L, "gretmcgrekegerrnan1", "password", new UserRole(2L, "CUSTOMER"),
      "Trever", "McKernan", "grekegernan1@hibu.com", "1137145977");
  }


  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  @DisplayName("Test load and getting userDetails by username; username is found")
  void loadUserByUsername() throws JsonProcessingException {
    User expectedUser = getAdminUser();
    UserDetails expectedUserDetails = withUsername(expectedUser.getUsername()).password(expectedUser.getPassword()).authorities(expectedUser.getRole().getName())
      .accountExpired(false).accountLocked(false).credentialsExpired(false).disabled(false).build();
    String username = expectedUser.getUsername();
    UserDetails actualUserDetails = userService.loadUserByUsername(username);
    assertEquals(expectedUserDetails, actualUserDetails);
  }

  @Test
  @DisplayName("Test load and getting userDetails by username; username is not found")
  void loadUserByUsernameNotExist() throws JsonProcessingException {
    String fakeUsername = getFakeUser().getUsername();
    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(fakeUsername));
  }


  @Test
  @DisplayName("Test whether the user that is trying to login is authenticated; authenticated")
  void loadAuthenticatedUser() throws JsonProcessingException {
    User user = getCustomerUser();
    assertEquals(user, userService.loadAuthenticatedUser(user.getUsername(), user.getPassword()).get());
  }

  @Test
  @DisplayName("Test whether the user that is trying to login is authenticated; not authenticated")
  void loadAuthenticatedUserFake() throws JsonProcessingException {
    User user = getFakeUser();
    assertFalse(userService.loadAuthenticatedUser(user.getUsername(), user.getPassword()).isPresent());
  }

  @Test
  @DisplayName("Test whether a user can be created")

  void signup() {
  }

  @Test
  @DisplayName("Test whether a user's information can be updated")

  void updateUser() {
  }

  @Test
  @DisplayName("Test whether a user can be deleted")

  void deleteUser() {
  }

  @Test
  @DisplayName("Test whether we can get a list of all users")

  void getAll() {
  }

  @Test
  @DisplayName("Test finding a user in the database by its id; the id exists")
  void getUserById() throws JsonProcessingException {
    User expectedUser = getCustomerUser();
    Long userID = expectedUser.getId();
    User actualUser = userService.getUserById(userID);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Test finding a user in the database by its id; the id does not exist")
  void getUserByIdFake() throws JsonProcessingException {
    Long fakeId = getFakeUser().getId();
    assertThrows(HttpServerErrorException.class, () -> userService.getUserById(fakeId));
  }

  @Test
  @DisplayName("Test finding a user in the database by its id; the id is invalid(null)")
  void getUserByIdInvalidId() throws JsonProcessingException {
    Long illegalId = null;
    assertThrows(InvalidDataAccessApiUsageException.class, () -> userService.getUserById(illegalId));
  }

  @Test
  @DisplayName("")
  void verifyOwnershipAndReturnOwner() {
  }

  @Test
  @DisplayName("Test removing the cookie; expect a cookie with key: session, value:null, path:'/'")
  void removeCookie() {
    Cookie actualCookie = userService.removeCookie().get();
    assertAll(
      () -> assertEquals("session", actualCookie.getName()),
      () -> assertEquals(null, actualCookie.getValue()),
      () -> assertEquals("/", actualCookie.getPath()));
  }

  @Test
  @DisplayName("Test finding a user in the database by its user name; the username exists")
  void getUserByUsername() throws JsonProcessingException {
    User expectedUser = getCustomerUser();
    String username = expectedUser.getUsername();
    User actualUser = userService.getUserByUsername(username);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Test finding a user in the database by its user name; the username does not exist")
  void getUserByUsernameFakeUser() throws JsonProcessingException {
    String fakeUsername = getFakeUser().getUsername();
    assertThrows(HttpServerErrorException.class, () -> userService.getUserByUsername(fakeUsername));
  }

  @Test
  @DisplayName("Test whether an admin user is successfully marked as an admin")
  void isUserAdmin() throws JsonProcessingException {
    User adminUser = getAdminUser();
    assertEquals(true, userService.isUserAdmin(adminUser));
  }

  @Test
  @DisplayName("Test whether an customer user is caught as non-admin")
  void isUserAdminCustomer() throws JsonProcessingException {
    User customerUser = getCustomerUser();
    assertEquals(false, userService.isUserAdmin(customerUser));
  }

  @Test
  @DisplayName("Test whether an agent user is successfully marked as an agent")
  void isUserAgent() throws JsonProcessingException {
    User agentUser = getAgentUser();
    assertEquals(true, userService.isUserAgent(agentUser));
  }

  @Test
  @DisplayName("Test whether an customer user is caught as non-agent")
  void isUserAgentCustomer() throws JsonProcessingException {
    User customerUser = getCustomerUser();
    assertEquals(false, userService.isUserAgent(customerUser));
  }
}