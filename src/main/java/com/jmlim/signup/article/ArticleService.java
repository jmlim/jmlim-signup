package com.jmlim.signup.article;

import com.jmlim.signup.account.Account;
import com.jmlim.signup.common.exception.ValidCustomException;
import com.jmlim.signup.account.AccountRepository;
import com.jmlim.signup.comment.CommentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class ArticleService {
    private final AccountRepository accountRepo;
    private final ArticleRepository articleRepo;
    private final CommentRepository commentRepo;

    public ArticleService(AccountRepository accountRepo, ArticleRepository articleRepo, CommentRepository commentRepo) {
        this.accountRepo = accountRepo;
        this.articleRepo = articleRepo;
        this.commentRepo = commentRepo;
    }

    @Transactional
    public Long create(Article article) {
        article.setCreatedDate(LocalDateTime.now());
        article.setUpdatedDate(LocalDateTime.now());

        // 현재 세션에 있는 사용자 정보
        Account writer = accountRepo.findByEmail(getUsername());
        article.setWriter(writer);
        Long id = articleRepo.save(article).getId();
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
