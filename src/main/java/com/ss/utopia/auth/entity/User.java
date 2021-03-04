package com.ss.utopia.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ss.utopia.auth.dto.UserDto;


import javax.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "USER")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  @JsonIgnore
  private String password;

  @Column(name = "given_name")
  private String givenName;

  @Column(name = "family_name")
  private String familyName;

  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "role_id", nullable = false)
  private UserRole role;

  protected User() {
  }

  public User(Long id, String username, String password, UserRole role) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
  }

  public User(Long id, String username, String password, UserRole role, String givenName, String familyName,
              String email, String phone) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
    this.givenName = givenName;
    this.familyName = familyName;
    this.email = email;
    this.phone = phone;
  }

  public User(String username, String password, UserRole role, String givenName, String familyName,
              String email, String phone) {
    this.username = username;
    this.password = password;
    this.role = role;
    this.givenName = givenName;
    this.familyName = familyName;
    this.email = email;
    this.phone = phone;
  }
  
  public User(UserDto userDto) {
	  this.username = userDto.getUsername();
	  this.password = userDto.getPassword();
	  this.role = new UserRole(userDto.getRole());
	  this.givenName = userDto.getGivenName();
	  this.familyName = userDto.getFamilyName();
	  this.email = userDto.getEmail();
	  this.phone = userDto.getPhone();

  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

    return getId().equals(user.getId()) && getUsername().equals(user.getUsername())
      && passwordEncoder.matches(getPassword(), user.getPassword()) &&
      getGivenName().equals(user.getGivenName()) && getFamilyName().equals(user.getFamilyName())
      && getEmail().equals(user.getEmail()) && getPhone().equals(user.getPhone()) && getRole().equals(user.getRole());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUsername(), getPassword(), getGivenName(), getFamilyName(), getEmail(), getPhone(), getRole());
  }
}
