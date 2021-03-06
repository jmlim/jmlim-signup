package com.jmlim.signup.article;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ArticleDto {
	@Data
	public static class Create {
		@NotBlank(message = "제목을 입력testtest해주세요.")
		@Size(max = 100, message = "100자 이내로 제목testtest을 입력하세요.")
		private String title;
		private String content;
	}
}
