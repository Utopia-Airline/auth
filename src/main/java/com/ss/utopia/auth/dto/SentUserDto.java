package com.ss.utopia.auth.dto;

import com.ss.utopia.auth.entity.User;
import com.ss.utopia.auth.entity.UserRole;

public class SentUserDto {
	
	private Long id;

	private String username;

	private String givenName;

	private String familyName;

	private String email;

	private String phone;
	
	private UserRole role;
	
	public SentUserDto(Long id, String username, String givenName, String familyName, String email, String phone, UserRole role) {
		this.id = id;
		this.username = username;
		this.givenName = givenName;
		this.familyName = familyName;
		this.email = email;
		this.phone = phone;
		this.role = role;
	}

	static public SentUserDto convertToSentUserDto(User user) {
		return new SentUserDto(user.getId(), user.getUsername(), user.getGivenName(), user.getFamilyName(), user.getEmail(), user.getPhone(), user.getRole());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
