package com.jmlim.signup.controller;

import java.util.Date;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jmlim.signup.controller.support.AccountDto;
import com.jmlim.signup.exception.ValidCustomException;
import com.jmlim.signup.repo.AccountRepository;
import com.jmlim.signup.service.AccountService;

@Controller
public class SignController {

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private AccountService accountService;

	@GetMapping("/")
	public String index(Map<String, Object> model) {
		model.put("time", new Date());
		return "index";
	}

	@GetMapping("/signin")
	public String siginin(Map<String, Object> model) {
		model.put("time", new Date());
		return "signin";
	}

	@GetMapping("/signup")
	public String siginup(Map<String, Object> model) {
		model.put("time", new Date());
		return "signup";
	}

	/**
	 * @param newAccount
	 * @return
	 */
	@PostMapping(value = "/signup")
	@ResponseBody
	@Transactional
	public Long postAjaxSignup(@RequestBody @Valid AccountDto.Create newAccount) {
		String email = newAccount.getEmail();
		String password = newAccount.getPassword();
		String repeatPassword = newAccount.getRepeatPassword();

		if (accountRepo.findByEmail(email) != null) {
			throw new ValidCustomException("이미 사용중인 이메일주소입니다.", "email");
		}

		if (!password.equals(repeatPassword)) {
			throw new ValidCustomException("패스워드가 일치하지 않습니다.", "password");
		}
		return accountService.save(newAccount);
	}
}
