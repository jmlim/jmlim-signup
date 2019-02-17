package com.jmlim.signup.service;

import com.jmlim.signup.controller.support.AccountDto;
import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.AccountRole;
import com.jmlim.signup.repo.AccountRepository;
import com.jmlim.signup.repo.AccountRoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class AccountService {
    private final AccountRepository accountRepo;
    private final AccountRoleRepository accountRoleRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public AccountService(AccountRepository accountRepo, AccountRoleRepository accountRoleRepo, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.accountRepo = accountRepo;
        this.accountRoleRepo = accountRoleRepo;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Long save(AccountDto.Create accountCreateDto) {
        Account account = modelMapper.map(accountCreateDto, Account.class);
        // 패스워드 암호화
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setJoinDate(LocalDateTime.now());
        Long id = accountRepo.save(account).getId();

        // 권한 입력
        AccountRole role = AccountRole.builder()
                .parent(account)
                .role("ROLE_USER").build();
        accountRoleRepo.save(role);
        return id;
    }

    @Transactional
    public Long save(Account account) {
        account = accountRepo.save(account);
        AccountRole newRole = AccountRole.builder()
                .parent(account)
                .role("ROLE_USER").build();
        accountRoleRepo.save(newRole);
        return account.getId();
    }
}
