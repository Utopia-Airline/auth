package com.ss.utopia.auth.service;

import com.ss.utopia.auth.dao.UserDao;
import com.ss.utopia.auth.dto.UpdateUserDto;
import com.ss.utopia.auth.dto.UserDto;
import com.ss.utopia.auth.entity.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.security.core.AuthenticationException;

import static org.springframework.security.core.userdetails.User.withUsername;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.Cookie;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
  @Autowired
  private UserDao userDao;
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private PasswordEncoder passwordEncoder;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userDao.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException(String.format("User with name %s does not exist", username)));
    return withUsername(user.getUsername()).password(user.getPassword()).authorities(user.getRole().getName())
      .accountExpired(false).accountLocked(false).credentialsExpired(false).disabled(false).build();
  }

  /**
   * Sign in a user into the application, with JWT-enabled authentication
   *
   * @param username username
   * @param password password
   * @return Optional of the Java Web Token, empty otherwise
   */
  public Optional<User> loadAuthenticatedUser(final String username, final String password) {
    LOGGER.info("New user attempting to sign in");
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (AuthenticationException e) {
      LOGGER.error("Log in authentication failed for user {}", username);
      return Optional.empty();
    }
    return userDao.findByUsername(username);
  }

  /**
   * @param userDto
   * @return
   */
  public User signup(UserDto userDto) {
    LOGGER.info("New user attempting to sign up");
    userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    User user = new User(userDto);
    
    if(user.getRole().getId().equals(null))
    	throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Unauthorized Role");
    
    return submitUser(user);
  }

  /**
   * check whether the user is agent
   *
   * @param user
   * @return
   */
  public User updateUser(User user, UpdateUserDto userDto) {
    LOGGER.info("User attempting to update info");
    return submitUser(updateUserInfo(user, userDto));

  }

  /**
   * Try to delete the User from the database
   *
   * @param user
   * @return
   */
  public int deleteUser(Long userId) {
    LOGGER.info("User attempting to delete profile");
    userDao.deleteById(userId);
    return 1;
  }
  
  
  /**
   * Get a list of users
   *
   * @param 
   * @return Page<User>
   */
  public Page<User> getAll(Map<String,String> params) {
	  try {
		  String username = params.get("username");
		  String email = params.get("email");
		  String sortString = params.get("sort");
		  if(sortString == null) {
			  sortString = "username";
		  }
		  String orderSort = params.get("order");
		  Sort sort;
		  if(orderSort == null || orderSort.equals("asc")) {
			  sort = Sort.by(sortString).ascending();
		  }
		  else {
			  sort = Sort.by(sortString).descending();
		  }
//		  For non-paginated list of users
		  if(params.get("offset") == null || params.get("limit") == null) {
			  if(params.get("role") != null) {
				  Page<User> users = new PageImpl<>(userDao.findAll(username, email, Integer.parseInt(params.get("role")), sort));
				  return users;
			  }
			  return new PageImpl<>(userDao.findAll(username, email, sort));
		  }
		  
//		  For paginated list of users
		  Pageable paging = PageRequest.of(Integer.parseInt(params.get("offset")), Integer.parseInt(params.get("limit")), sort);
		  if(params.get("role") != null) {
			  return userDao.findAll(username, email, Integer.parseInt(params.get("role")), paging);
		  }
		  return userDao.findAll(username, email, paging);
	  }
	  catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Bad search input");
	  }
  }


  /**
   * Get a user by its id and return the user
   *
   * @param id
   * @return User
   */
  public User getUserById(Long id) {
	return userDao.findById(id).orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND, "Unable to find User"));

  }

  /**
   * verify that if a user is the same user with the JWT token
   *
   * @param user
   * @param currentUser parsed from the JWT token
   * @return the user if true. throw exception otherwise
   */
  public User verifyOwnershipAndReturnOwner(User user, UserDetails currentUser) {
    if (user == null)
      throw new HttpServerErrorException(HttpStatus.FORBIDDEN, "Unauthorized User");
    if (!user.getUsername().equals(currentUser.getUsername()))
      throw new HttpServerErrorException(HttpStatus.FORBIDDEN, "Unauthorized User");
    return user;

  }

  /**
   * Remove a jwt token from the session cookie and return an empty string
   *
   * @return
   */
  public Optional<Cookie> removeCookie() {
    try {
      final Cookie sessionCookie = new Cookie("session", null);
      sessionCookie.setPath("/");
      return Optional.of(sessionCookie);
    } catch (IllegalArgumentException e) {
      LOGGER.error("Cookie Illegal Argument Exception", e.getMessage());
      return Optional.empty();
    }
  }

  public User getUserByUsername(String username) {
    return userDao.findByUsername(username)
      .orElseThrow(() -> new HttpServerErrorException(HttpStatus.FORBIDDEN, "Unauthorized User"));
  }

  /**
   * check whether the user is admin
   *
   * @param user
   * @return
   */
  public boolean isUserAdmin(User user) {
    return user.getRole().getId() == 1;
  }

  /**
   * check whether the user is agent
   *
   * @param user
   * @return
   */
  public boolean isUserAgent(User user) {
    return user.getRole().getId() == 3;
  }
  
  /**
   * check whether the user can be saved to the database
   *
   * @param user
   * @return
   */
  private User submitUser(User user) {
	    try {
	    	return userDao.save(user);
	    } catch (DataIntegrityViolationException e) {
	        String error = e.getMessage();
	        if (error.contains("user.email_UNIQUE")) {
	          throw new HttpServerErrorException(HttpStatus.CONFLICT, "Email is taken");
	        } else if (error.contains("user.username_UNIQUE")) {
	          throw new HttpServerErrorException(HttpStatus.CONFLICT, "Username is taken");
	        } else if (error.contains("user.phone_UNIQUE")) {
	          throw new HttpServerErrorException(HttpStatus.CONFLICT, "Phone number is taken");
	        }
	    }
	    return user;
  }
  
  
//  TODO: Research Strings class to simplify strings
  private User updateUserInfo(User user, UpdateUserDto userDto) {
	  if(userDto.getUsername() != null && userDto.getUsername() != "")
	      user.setUsername(userDto.getUsername());
	  if(userDto.getPassword() != null && userDto.getPassword() != "")
	      user.setPassword(passwordEncoder.encode(userDto.getPassword()));
	  if(userDto.getGivenName() != null && userDto.getGivenName() != "")
	      user.setGivenName(userDto.getGivenName());
	  if(userDto.getFamilyName() != null && userDto.getFamilyName() != "")
		  user.setFamilyName(userDto.getFamilyName());
	  if(userDto.getEmail() != null && userDto.getEmail() != "")
		  user.setEmail(userDto.getEmail());
	  if(userDto.getPhone() != null && userDto.getPhone() != "")
		  user.setPhone(userDto.getPhone());
	  return user;
  }
  
}
