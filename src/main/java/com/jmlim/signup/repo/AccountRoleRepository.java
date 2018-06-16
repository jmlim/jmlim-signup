package com.jmlim.signup.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.AccountRole;

public interface AccountRoleRepository extends CrudRepository<AccountRole, Long> {
	public List<AccountRole> findByParent(Account parent);
}
