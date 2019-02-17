package com.jmlim.signup.controller;

import com.jmlim.signup.controller.support.ArticleDto;
import com.jmlim.signup.controller.support.CommentDto;
import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.Article;
import com.jmlim.signup.domain.Comment;
import com.jmlim.signup.exception.ValidCustomException;
import com.jmlim.signup.repo.AccountRepository;
import com.jmlim.signup.repo.ArticleRepository;
import com.jmlim.signup.repo.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/article")
public class ArticleController {
    private final AccountRepository accountRepo;
    private final ArticleRepository articleRepo;
    private final CommentRepository commentRepo;
    private final ModelMapper modelMapper;

    public ArticleController(AccountRepository accountRepo, ArticleRepository articleRepo, CommentRepository commentRepo, ModelMapper modelMapper) {
        this.accountRepo = accountRepo;
        this.articleRepo = articleRepo;
        this.commentRepo = commentRepo;
        this.modelMapper = modelMapper;
    }

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
       article.setCreatedDate(LocalDateTime.now());
        article.setUpdatedDate(LocalDateTime.now());

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
        article.setUpdatedDate(LocalDateTime.now());
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
    @Transactional
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
        //댓글삭제
        commentRepo.deleteByParent(article);
        articleRepo.delete(article);
        return id;
    }

    /**
     * comment
     */
    // http://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html
    // 의 Table 1.1. 참조
    // albums?page=0&size=20&sort=username,asc$sort=name,asc
    @GetMapping(value = "/article/{id}/comment")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Comment> findCommentAll(Pageable pageable, @PathVariable Long id) {
        Article article = articleRepo.findOne(id);
        return commentRepo.findByParent(article, pageable);
    }

    /**
     * @param commentDto
     * @param id
     * @return
     */
    @PostMapping(value = "/article/{id}/comment")
    @ResponseBody
    @Transactional
    public Long commentCreate(@RequestBody @Valid CommentDto.Create commentDto, @PathVariable Long id) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setCreatedDate(LocalDateTime.now());

        Article parent = articleRepo.findOne(id);
        comment.setParent(parent);
        // 현재 세션에 있는 사용자 정보
        Account writer = accountRepo.findByEmail(getUsername());
        comment.setWriter(writer);
        id = commentRepo.save(comment).getId();
        return id;
    }

    /** comment end */

    /**
     * @param articleId
     * @param commentId
     * @return
     */
    // 더 좋은 방법이 있는지 찾아볼것.
    @DeleteMapping(value = "/article/{articleId}/comment/{commentId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Long delete(@PathVariable Long articleId, @PathVariable Long commentId) {
        Comment comment = commentRepo.findOne(commentId);
        String writer = comment.getWriter().getEmail();

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

        commentRepo.delete(comment);
        return commentId;
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
    /*** Security 관련 끝 *********/
}
