package com.jmlim.signup.config.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import com.jmlim.signup.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import com.jmlim.signup.account.AccountRepository;
import com.jmlim.signup.account.AccountRoleRepository;
import com.jmlim.signup.config.security.oauth2.ClientResources;
import com.jmlim.signup.config.security.oauth2.OAuth2SuccessHandler;



@EnableWebSecurity
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRoleRepository roleRepository;
	
	/* ===========oauth2 관련============================= */
	private Filter oauth2Filter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		filters.add(oauth2Filter(facebook(), "/login/facebook", "facebook"));
		filters.add(oauth2Filter(google(), "/login/google", "google"));
		// filters.add(oauth2Filter(kakao(), "/login/kakao", SocialType.KAKAO));
		filter.setFilters(filters);
		return filter;
	}


	@Bean
	@ConfigurationProperties("facebook")
	public ClientResources facebook() {
		return new ClientResources();
	}
	
	@Bean
	@ConfigurationProperties("google")
	public ClientResources google() {
		return new ClientResources();
	}

	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	private Filter oauth2Filter(ClientResources client, String path, String socialType/*, SocialType socialType*/) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		//StringBuilder redirectUrl = new StringBuilder("/");
		//redirectUrl.append(socialType.getValue()).append("/complete");

		filter.setRestTemplate(template);
		filter.setTokenServices(
				new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId()));
		filter.setAuthenticationSuccessHandler(new OAuth2SuccessHandler(socialType, accountRepository, accountService, roleRepository));
		//filter.setAuthenticationSuccessHandler(
		//		(request, response, authentication) -> response.sendRedirect(redirectUrl.toString()));
		filter.setAuthenticationFailureHandler((request, response, exception) -> response.sendRedirect("/error"));
		return filter;
	}
	/*===========oauth2  관련 end=============================*/

	/*@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/script/**", "image/**", "/fonts/**", "lib/**");
	}
*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
			.antMatchers("/admin/**")
			.hasRole("ADMIN")
			.antMatchers("/article/**")
			.hasRole("USER")
			.antMatchers("/**")
			.permitAll()
				.and()
				//oauth 관련 필터 추가.
				.addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
			.formLogin().loginPage("/signin")
						.loginProcessingUrl("/signin")
						.defaultSuccessUrl("/")
			.failureUrl("/signin").and()
			// http://www.baeldung.com/spring-security-logout
			.logout()
				.logoutUrl("/signout")
				.logoutSuccessUrl("/");
	}

	@Bean
	public PasswordEncoder delegatingPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService).passwordEncoder(delegatingPasswordEncoder());
	}
}