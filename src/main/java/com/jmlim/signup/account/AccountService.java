package com.jmlim.signup.account;

import com.jmlim.signup.common.exception.SocialAccountTypeException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Account save(Account account) {
        String password = account.getPassword();
        // 패스워드 암호화 (소셜로그인의 경우 패스워드 받지 않음)
        if (password != null) {
            account.setPassword(passwordEncoder.encode(password));
        }
        account.setJoinDate(LocalDateTime.now());
        return accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        String type = account.getType();
        if(type != null) {
            throw new SocialAccountTypeException(type);
        }
        return new AccountAdapter(account);
    }
}