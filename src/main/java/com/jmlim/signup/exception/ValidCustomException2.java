package com.jmlim.signup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidCustomException2 extends RuntimeException {
	private Error[] errors;

	public ValidCustomException2(String defaultMessage, String field) {
		this.errors = new Error[] { new Error(defaultMessage, field) };
	}

	public ValidCustomException2(Error[] errors) {
		this.errors = errors;
	}

	public Error[] getErrors() {
		return errors;
	}

	public static class Error {

		private String defaultMessage;
		private String field;

		public Error(String defaultMessage, String field) {
			this.defaultMessage = defaultMessage;
			this.field = field;
		}

		public String getDefaultMessage() {
			return defaultMessage;
		}

		public String getField() {
			return field;
		}
	}
}
