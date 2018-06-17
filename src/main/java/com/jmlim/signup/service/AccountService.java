package com.jmlim.signup.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jmlim.signup.controller.support.AccountDto;
import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.AccountRole;
import com.jmlim.signup.repo.AccountRepository;
import com.jmlim.signup.repo.AccountRoleRepository;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private AccountRoleRepository accountRoleRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ModelMapper modelMapper;

	@Transactional
	public Long save(AccountDto.Create accountCreateDto) {
		Account account = modelMapper.map(accountCreateDto, Account.class);
		// 패스워드 암호화
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		account.setJoinDate(new Date());
		Long id = accountRepo.save(account).getId();

		// 권한 입력
		AccountRole role = new AccountRole();
		role.setParent(account);
		role.setRole("ROLE_USER");
		accountRoleRepo.save(role);
		return id;
	}
}
