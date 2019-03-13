package com.jmlim.signup.account;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jmlim.signup.account.Account;
import com.jmlim.signup.account.AccountRole;

public interface AccountRoleRepository extends CrudRepository<AccountRole, Long> {
	List<AccountRole> findByParent(Account parent);
}
