package com.jmlim.signup.controller.support;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

public class CommentDto {
	@Data
	public static class Create {
		@NotBlank(message = "내용을 입력해주세요.")
		private String content;
	}
}
