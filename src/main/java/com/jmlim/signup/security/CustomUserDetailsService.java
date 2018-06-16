package com.jmlim.signup.security;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.AccountRole;
import com.jmlim.signup.repo.AccountRepository;
import com.jmlim.signup.repo.AccountRoleRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	AccountRepository accountRepository;
	@Autowired
	AccountRoleRepository accountRoleRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Account account = accountRepository.findByEmail(email);
		/*
		 * Map<String, String> param = new HashMap<>(); param.put("userId",
		 * username); User user = memberRepository.getUser(param);
		 */
		if (account == null) {
			throwExceptionIfNotFound();
		}
		List<AccountRole> roles = accountRoleRepository.findByParent(account);
		return new UserDetailsImpl(account, roles);
	}

	private void throwExceptionIfNotFound() {
		logger.error("사용자를 찾을 수 없습니다.");
		throw new UsernameNotFoundException("* 아이디 혹은 비밀번호가 일치하지 않습니다.");
	}

}