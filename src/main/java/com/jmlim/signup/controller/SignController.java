package com.jmlim.signup.controller;

import java.util.Date;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.AccountRole;
import com.jmlim.signup.repo.AccountRepository;
import com.jmlim.signup.repo.AccountRoleRepository;

@Controller
public class SignController {

	@Autowired
	AccountRepository accountRepo;
	@Autowired
	AccountRoleRepository accountRoleRepo;
	@Autowired
	PasswordEncoder passwordEncoder;

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
	public Account postSignup(Account newAccount) {

		// TODO 아이디 중복 확인..
		// exception 시 badRequest
		// 가입 후 리턴.
		if (newAccount == null) {
			throw new RuntimeException();
		}

		String email = newAccount.getEmail();
		String password = newAccount.getPassword();
		String repeatPassword = newAccount.getRepeatPassword();
		if (accountRepo.findByEmail(email) != null) {
			// 이미 계정이 존재
			return null;
		}
		if (!password.equals(repeatPassword)) {
			// 패스워드 불일치
			return null;
		}
		newAccount.setPassword(passwordEncoder.encode(password));
		newAccount.setJoinDate(new Date());

		accountRepo.save(newAccount);
		AccountRole role = new AccountRole();
		role.setParent(newAccount);
		role.setRole("ROLE_USER");
		accountRoleRepo.save(role);

		return newAccount;
	}

	/**
	 * @param newAccount
	 * @return
	 */
	@PostMapping(value = "/ajax_signup")
	@ResponseBody
	@Transactional
	public Account postAjaxSignup(@RequestBody Account newAccount) {

		// TODO 아이디 중복 확인..
		// exception 시 badRequest
		// 가입 후 리턴.
		if (newAccount == null) {
			throw new RuntimeException();
		}

		String email = newAccount.getEmail();
		String password = newAccount.getPassword();
		String repeatPassword = newAccount.getRepeatPassword();
		if (accountRepo.findByEmail(email) != null) {
			// 이미 계정이 존재
			return null;
		}
		if (!password.equals(repeatPassword)) {
			// 패스워드 불일치
			return null;
		}
		newAccount.setPassword(passwordEncoder.encode(password));
		newAccount.setJoinDate(new Date());

		accountRepo.save(newAccount);
		AccountRole role = new AccountRole();
		role.setParent(newAccount);
		role.setRole("ROLE_USER");
		accountRoleRepo.save(role);

		return newAccount;
	}
}
