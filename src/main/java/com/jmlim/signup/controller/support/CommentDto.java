package com.jmlim.signup.controller.support;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

public class CommentDto {
	@Data
	public static class Create {
		@NotBlank(message = "내용을 입력해주세요.")
		private String content;
	}
}
