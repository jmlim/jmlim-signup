package com.jmlim.signup.controller;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jmlim.signup.controller.support.ArticleDto;
import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.Article;
import com.jmlim.signup.exception.ValidCustomException;
import com.jmlim.signup.repo.AccountRepository;
import com.jmlim.signup.repo.ArticleRepository;

@Controller
@RequestMapping("/article")
public class ArticleController {

	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private ArticleRepository articleRepo;
	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/list")
	public String lists(Map<String, Object> model) {
		model.put("time", new Date());
		return "article/list";
	}

	@GetMapping("/create")
	public String create(Map<String, Object> model) {
		model.put("time", new Date());
		return "article/create";
	}

	@GetMapping("/update/{id}")
	public String update(Map<String, Object> model, @PathVariable Long id) {
		model.put("time", new Date());
		model.put("id", id);
		return "article/update";
	}

	@GetMapping("/content/{id}")
	public String content(Map<String, Object> model, @PathVariable Long id) {
		model.put("time", new Date());
		model.put("id", id);
		return "article/content";
	}

	// http://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html
	// 의 Table 1.1. 참조
	// albums?page=0&size=20&sort=username,asc$sort=name,asc
	@GetMapping(value = "/article")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Page<Article> findAll(Pageable pageable) {
		return articleRepo.findAll(pageable);
	}

	/**
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/article/{id}")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Article findOne(@PathVariable Long id) {
		return articleRepo.findOne(id);
	}

	/**
	 * @param articleDto
	 * @return
	 */
	@PostMapping(value = "/article")
	@ResponseBody
	@Transactional
	public Long articleCreate(@RequestBody @Valid ArticleDto.Create articleDto) {
		Article article = modelMapper.map(articleDto, Article.class);
		article.setCreatedDate(new Date());
		article.setUpdatedDate(new Date());

		// 현재 세션에 있는 사용자 정보
		Account writer = accountRepo.findByEmail(getUsername());
		article.setWriter(writer);
		Long id = articleRepo.save(article).getId();
		return id;
	}

	/**
	 * @param articleDto
	 * @param id
	 * @return
	 */
	@PutMapping(value = "/article/{id}")
	@ResponseBody
	@Transactional
	public Long articleUpdate(@RequestBody @Valid ArticleDto.Create articleDto, @PathVariable Long id) {
		Article article = articleRepo.findOne(id);
		String writer = article.getWriter().getEmail();

		boolean hasAdminRole = false;
		for (GrantedAuthority autority : getAuthorities()) {
			if (autority.getAuthority().equals("ROLE_ADMIN")) {
				hasAdminRole = true;
			}
		}

		// ADMIN 권한은 수정 가능.
		if (!hasAdminRole) {
			if (!getUsername().equals(writer)) {
				throw new ValidCustomException("본인이 작성한 글만 수정이 가능합니다.", "writer");
			}
		}

		article.setTitle(articleDto.getTitle());
		article.setContent(articleDto.getContent());
		article.setUpdatedDate(new Date());
		return id;
	}

	/**
	 * @param id
	 * @return
	 */
	// 더 좋은 방법이 있는지 찾아볼것.
	@DeleteMapping(value = "/article/{id}")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Long delete(@PathVariable Long id) {
		Article article = articleRepo.findOne(id);
		String writer = article.getWriter().getEmail();

		boolean hasAdminRole = false;
		for (GrantedAuthority autority : getAuthorities()) {
			if (autority.getAuthority().equals("ROLE_ADMIN")) {
				hasAdminRole = true;
			}
		}

		// ADMIN 권한은 삭제 가능.
		if (!hasAdminRole) {
			if (!getUsername().equals(writer)) {
				throw new ValidCustomException("본인이 작성한 글만 삭제가 가능합니다.", "writer");
			}
		}

		articleRepo.delete(article);
		return id;
	}

	/*** Security 관련 *********/
	protected String getUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getPrincipal() instanceof UserDetails) {
			return ((UserDetails) auth.getPrincipal()).getUsername();
		} else {
			Account account = (Account) auth.getPrincipal();
			return account.getEmail();
		}
	}

	protected Collection<? extends GrantedAuthority> getAuthorities() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return authorities;
	}
	/*** Security 관련 끝*********/
}
