package com.jmlim.signup.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.AccountRole;

public class UserDetailsImpl extends User {

	private static final long serialVersionUID = 1L;

	private Long id;

	public UserDetailsImpl(Account member, List<AccountRole> roles) {
		super(member.getEmail(), member.getPassword(), authorities(roles));
		this.id = member.getId();
	}

	private static Collection<? extends GrantedAuthority> authorities(List<AccountRole> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		// TODO 회원을 관리할 수 있는 슈퍼관리자 권한. ROLE_ADMIN,
		// TODO 일번 계정 권한 ROLE_USER
		for (AccountRole role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getRole()));
		}
		return authorities;
	}

	public Long getId() {
		return id;
	}
}
