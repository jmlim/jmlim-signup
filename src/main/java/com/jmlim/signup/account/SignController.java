package com.jmlim.signup.account;

import com.jmlim.signup.common.exception.ValidCustomException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class SignController {

	private final AccountRepository accountRepo;
	private final AccountService accountService;
	private final ModelMapper modelMapper;

	public SignController(AccountRepository accountRepo, AccountService accountService, ModelMapper modelMapper) {
		this.accountRepo = accountRepo;
		this.accountService = accountService;
		this.modelMapper = modelMapper;
	}

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
	public Account postAjaxSignup(@RequestBody @Valid AccountDto.Create newAccount) {
		String email = newAccount.getEmail();
		String password = newAccount.getPassword();
		String repeatPassword = newAccount.getRepeatPassword();

		if (accountRepo.findByEmail(email) != null) {
			throw new ValidCustomException("이미 사용중인 이메일주소입니다.", "email");
		}

		if (!password.equals(repeatPassword)) {
			throw new ValidCustomException("패스워드가 일치하지 않습니다.", "password");
		}
		Account account = modelMapper.map(newAccount, Account.class);
		account.setRoles(Stream.of(AccountRole.ADMIN, AccountRole.USER).collect(Collectors.toSet()));
		return accountService.save(account);
	}
}
