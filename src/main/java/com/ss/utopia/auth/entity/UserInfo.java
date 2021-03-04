package com.ss.utopia.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_info")
public class UserInfo {
  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "given_name")
  private String givenName;
  @Column(name = "family_name")
  private String familyName;
  @Column(name = "email")
  private String email;
  @Column(name = "phone")
  private String phone;
  
  public UserInfo() {}
  
  public UserInfo(Long id, String givenName, String familyName, String email, String phone) {
	  this.id = id;
	  this.givenName = givenName;
	  this.familyName = familyName;
	  this.email = email;
	  this.phone = phone;
  }
  
  public UserInfo(String givenName, String familyName, String email, String phone) {
	  this.givenName = givenName;
	  this.familyName = familyName;
	  this.email = email;
	  this.phone = phone;
  }
  
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
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
  
}
