package com.jmlim.signup.service;

import com.jmlim.signup.controller.support.ArticleDto;
import com.jmlim.signup.domain.Account;
import com.jmlim.signup.domain.Article;
import com.jmlim.signup.domain.Comment;
import com.jmlim.signup.exception.ValidCustomException;
import com.jmlim.signup.repo.AccountRepository;
import com.jmlim.signup.repo.ArticleRepository;
import com.jmlim.signup.repo.CommentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class CommentService {
    private final AccountRepository accountRepo;
    private final ArticleRepository articleRepo;
    private final CommentRepository commentRepo;

    public CommentService(AccountRepository accountRepo, ArticleRepository articleRepo, CommentRepository commentRepo) {
        this.accountRepo = accountRepo;
        this.articleRepo = articleRepo;
        this.commentRepo = commentRepo;
    }

    @Transactional
    public Long create(Comment comment, Long articleId) {
        comment.setCreatedDate(LocalDateTime.now());

        Article parent = articleRepo.findOne(articleId);
        comment.setParent(parent);
        // 현재 세션에 있는 사용자 정보
        Account writer = accountRepo.findByEmail(getUsername());
        comment.setWriter(writer);
        Long id = commentRepo.save(comment).getId();
        return id;
    }

    @Transactional
    public Long update(Long id, ArticleDto.Create articleDto) {
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

    @Transactional
    public Long delete(Long id) {
        Comment comment = commentRepo.findOne(id);
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
    /*** Security 관련 끝 *********/
}
