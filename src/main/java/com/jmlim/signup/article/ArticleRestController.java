package com.jmlim.signup.article;

import com.jmlim.signup.account.Account;
import com.jmlim.signup.account.AccountRole;
import com.jmlim.signup.account.CurrentUser;
import com.jmlim.signup.common.exception.ValidCustomException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/article")
public class ArticleRestController {

    private final ArticleRepository articleRepository;
    private final ArticleService articleService;
    private final ModelMapper modelMapper;

    public ArticleRestController(ArticleRepository articleRepository, ArticleService articleService, ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.articleService = articleService;
        this.modelMapper = modelMapper;
    }

    // http://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html
    // 의 Table 1.1. 참조
    // albums?page=0&size=20&sort=username,asc$sort=name,asc
    @GetMapping(value = "/article")
    @ResponseStatus(HttpStatus.OK)
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    /**
     * @param id
     * @return
     */
    @GetMapping(value = "/article/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Article findOne(@PathVariable Long id) {
        return articleRepository.findOne(id);
    }

    /**
     * @param articleDto
     * @return
     */
    @PostMapping(value = "/article")
    public ResponseEntity articleCreate(@RequestBody @Valid ArticleDto.Create articleDto,
                                        @CurrentUser Account account) {
        Article article = modelMapper.map(articleDto, Article.class);
        article.setWriter(account);
        Long id = articleService.create(article);
        URI createdUrl = linkTo(ArticleController.class).slash(id).toUri();
        return ResponseEntity.created(createdUrl).body(article);
    }

    /**
     * @param articleDto
     * @param id
     * @return
     */
    @PutMapping(value = "/article/{id}")
    public ResponseEntity articleUpdate(@RequestBody @Valid ArticleDto.Create articleDto,
                                        @PathVariable Long id,
                                        @CurrentUser Account account) {
        Article existingArticle = articleRepository.findOne(id);
        this.modelMapper.map(articleDto, existingArticle);
        // Article newarticle = modelMapper.map(articleDto, Article.class);

        boolean hasAdminRole = false;
        for (AccountRole role : account.getRoles()) {
            if (role == AccountRole.ADMIN) {
                hasAdminRole = true;
            }
        }

        // ADMIN 권한은 수정 가능.
        if (!hasAdminRole) {
            if (!account.getEmail().equals(existingArticle.getWriter())) {
                throw new ValidCustomException("본인이 작성한 글만 수정이 가능합니다.", "writer");
            }
        }

        articleService.update(existingArticle);
        URI createdUrl = linkTo(ArticleController.class).slash(id).toUri();
        return ResponseEntity.created(createdUrl).body(id);
    }

    /**
     * @param id
     * @return
     */
    // 더 좋은 방법이 있는지 찾아볼것.
    @DeleteMapping(value = "/article/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Long delete(@PathVariable Long id, @CurrentUser Account account) {
        Article existingArticle = articleRepository.findOne(id);
        boolean hasAdminRole = false;
        for (AccountRole role : account.getRoles()) {
            if (role == AccountRole.ADMIN) {
                hasAdminRole = true;
            }
        }

        // ADMIN 권한은 삭제 가능.
        if (!hasAdminRole) {
            if (!account.getEmail().equals(existingArticle.getWriter())) {
                throw new ValidCustomException("본인이 작성한 글만 삭제가 가능합니다.", "writer");
            }
        }

        articleService.delete(id);
        return id;
    }
}