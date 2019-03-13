package com.jmlim.signup.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jmlim.signup.common.converter.LocalDateTimePersistenceConverter;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Convert;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 원하는 데이터만 주고받고 하기위해 Dto 생성. Rest 방식으로 개발할때 필요. 
 * 기존방식은 dto를 사용하지 않더라도 자기가 원하는 내용만 반환할 수 있음.
 * 
 * 회원 정보를 나타내기 위해 Account클래스만 사용하지 않은 이유는, 
 * Entity 클래스를 파라미터 혹은 View 데이터로 사용하게 되면 변화에 대응하기가 힘들기 때문
 */
// TODO validation 처리
public class AccountDto {

	@Data
	public static class Create {

		@NotBlank(message = "인증할 아이디를 입력해주세요.testtesttest")
		@Email(message = "메일의 양식을 지켜주세요.testtesttest")
		private String email;

		@NotBlank
		@Size(min = 5, max = 20)
		private String password;

		@NotBlank
		@Size(min = 1, max = 20)
		private String repeatPassword;
	}

	@Data
	public static class Response implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Long id;
		private String email;

		// http://stackoverflow.com/questions/29027475/date-format-in-the-json-output-using-spring-boot
		// json으로 데이터 내보낼 시 포멧이 원하는 대로 나오지 않아 처리한 부분
		@JsonFormat(pattern = "yyyy-MM-dd")
		@Convert(converter = LocalDateTimePersistenceConverter.class)
		private LocalDateTime joinDate;
	}
}
