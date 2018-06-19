package com.jmlim.signup.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jmlim.signup.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

	/**
	 * @param email
	 * @return
	 */
	Account findByEmail(String email);
}
