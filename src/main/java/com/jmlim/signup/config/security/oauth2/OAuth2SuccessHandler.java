package com.jmlim.signup.config.security.oauth2;

import com.jmlim.signup.account.*;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private String type;

    private AccountRepository accountRepository;

    private AccountService accountService;

    /**
     *
     * @param type
     * @param accountRepository
     * @param accountService
     */
    public OAuth2SuccessHandler(String type, AccountRepository accountRepository, AccountService accountService) {
        this.type = type;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    /**
     * 로그인 후 처리.
     *
     * @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse,
     * org.springframework.security.core.Authentication)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
            throws IOException, ServletException {
        //System.out.println(type);
        // https://stackoverflow.com/questions/48057860/accessing-data-in-principal-in-spring
        HashMap<?, ?> data = (HashMap<?, ?>) ((OAuth2Authentication) auth).getUserAuthentication().getDetails();

        String email = data.get("email").toString();
        Account account = accountRepository.findByEmail(email);
        // 연결되어 있는 계정이 있는경우.
        if (account != null) {

            Set<SimpleGrantedAuthority> authorities = account.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).collect(Collectors.toSet());

            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(new AccountAdapter(account), null, authorities));
            /*AccountDto.Response accountRes = new AccountDto.Response();
            accountRes.setEmail(account.getEmail());
            accountRes.setId(account.getId());
            accountRes.setJoinDate(account.getJoinDate());
            HttpSession httpSession = req.getSession();
            httpSession.setAttribute("LOGIN_USER", accountRes);*/
            res.sendRedirect("/");
        }
        // 연결된 계정이 없는경우
        else {
            account = Account.builder()
                    .email(email)
                    .password(type) // 패스워드가 없을 경우 시큐리티에서 에러 발생하여 임시 처리.
                    .type(type)
                    .roles(Stream.of(AccountRole.ADMIN, AccountRole.USER).collect(Collectors.toSet()))
                    .build();
            Account savedAccount = accountService.save(account);

            Set<SimpleGrantedAuthority> authorities = savedAccount.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).collect(Collectors.toSet());

            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(new AccountAdapter(account), null, authorities));
           /* AccountDto.Response accountRes = new AccountDto.Response();
            accountRes.setEmail(account.getEmail());
            accountRes.setId(account.getId());
            accountRes.setJoinDate(account.getJoinDate());
            HttpSession httpSession = req.getSession();
            httpSession.setAttribute("LOGIN_USER", accountRes);*/
            res.sendRedirect("/");
        }
    }
}
