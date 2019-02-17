package com.jmlim.signup.security.oauth2;

import com.jmlim.signup.controller.support.AccountDto;
import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.AccountRole;
import com.jmlim.signup.repo.AccountRepository;
import com.jmlim.signup.repo.AccountRoleRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private String type;

	private AccountRepository accountRepository;

	private AccountRoleRepository roleRepository;

	/**
	 * @param type
	 * @param accountRepository
	 * @param roleRepository
	 */
	public OAuth2SuccessHandler(String type, AccountRepository accountRepository,
			AccountRoleRepository roleRepository) {
		this.type = type;
		this.accountRepository = accountRepository;
		this.roleRepository = roleRepository;
	}

	/**
	 * 로그인 후 처리.
	 * 
	 * @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse,
	 *      org.springframework.security.core.Authentication)
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
			throws IOException, ServletException {
		System.out.println(type);
		// https://stackoverflow.com/questions/48057860/accessing-data-in-principal-in-spring
		HashMap<?, ?> data = (HashMap<?, ?>) ((OAuth2Authentication) auth).getUserAuthentication().getDetails();

		String email = data.get("email").toString();
		Account account = accountRepository.findByEmail(email);

		// 연결되어 있는 계정이 있는경우.
		if (account != null) {
			// 기존 인증을 바꿉니다.
			// 이전 강의의 일반 로그인과 동일한 방식으로 로그인한 것으로 간주하여 처리합니다.
			// 기존 인증이 날아가기 때문에 OAUTH ROLE은 증발하며, USER ROLE 이 적용됩니다.
			List<AccountRole> roles = roleRepository.findByParent(account);
			List<GrantedAuthority> authorities = new ArrayList<>();
			// TODO 회원을 관리할 수 있는 슈퍼관리자 권한. ROLE_ADMIN,
			// TODO 일번 계정 권한 ROLE_USER
			for (AccountRole role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.getRole()));
			}

			SecurityContextHolder.getContext()
					.setAuthentication(new UsernamePasswordAuthenticationToken(account, null, authorities));

			AccountDto.Response accountRes = new AccountDto.Response();
			accountRes.setEmail(account.getEmail());
			accountRes.setId(account.getId());
			accountRes.setJoinDate(account.getJoinDate());
			HttpSession httpSession = req.getSession();
			httpSession.setAttribute("LOGIN_USER", accountRes);
			res.sendRedirect("/");
		}
		// 연결된 계정이 없는경우
		else {

			// TODO : 계정생성하는 부분 transaction 처리 필요.
			account = new Account();
			account.setEmail(email);
			account.setType(type);
			account.setJoinDate(LocalDateTime.now());

			accountRepository.save(account);
			AccountRole newRole = new AccountRole();
			newRole.setParent(account);
			newRole.setRole("ROLE_USER");
			roleRepository.save(newRole);

			List<AccountRole> roles = roleRepository.findByParent(account);
			List<GrantedAuthority> authorities = new ArrayList<>();
			// TODO 회원을 관리할 수 있는 슈퍼관리자 권한. ROLE_ADMIN,
			// TODO 일번 계정 권한 ROLE_USER
			for (AccountRole role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.getRole()));
			}

			SecurityContextHolder.getContext()
					.setAuthentication(new UsernamePasswordAuthenticationToken(account, null, authorities));
			AccountDto.Response accountRes = new AccountDto.Response();
			accountRes.setEmail(account.getEmail());
			accountRes.setId(account.getId());
			accountRes.setJoinDate(account.getJoinDate());
			HttpSession httpSession = req.getSession();
			httpSession.setAttribute("LOGIN_USER", accountRes);
			res.sendRedirect("/");
		}
	}
}
