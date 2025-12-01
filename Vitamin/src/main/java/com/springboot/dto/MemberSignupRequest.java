package com.springboot.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequest {
	private String email;
	private String password;
	private String passwordCheck;
	private String nickname;
}