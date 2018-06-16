package com.jmlim.signup.controller.support;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

/**
 * 원하는 데이터만 주고받고 하기위해 Dto 생성. Rest 방식으로 개발할때 필요. 기존방식은 dto를 사용하지 않더라도 자기가 원하는
 * 내용만 반환할 수 있음.
 */
// TODO validation 처리
public class AccountDto {

	@Data
	public static class Create {

		@NotBlank(message = "인증할 아이디를 입력해주세요.")
		private String email;

		@NotBlank
		@Size(min = 5, max = 20)
		private String password;

		@NotBlank
		@Size(min = 1, max = 20)
		private String repeatPassword;
	}

}
