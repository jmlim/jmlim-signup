package com.jmlim.signup.account;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jmlim.signup.account.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

	/**
	 * @param email
	 * @return
	 */
	Account findByEmail(String email);
}
