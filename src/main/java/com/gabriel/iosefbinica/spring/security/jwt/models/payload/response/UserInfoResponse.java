package com.gabriel.iosefbinica.spring.security.jwt.models.payload.response;

import java.util.List;

public class UserInfoResponse {
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	private Integer loginAttempts;

	public UserInfoResponse(Long id, String username, String email, List<String> roles, Integer loginAttempts) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.loginAttempts = loginAttempts;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public Integer getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(Integer loginAttempts) {
		this.loginAttempts = loginAttempts;
	}
}
