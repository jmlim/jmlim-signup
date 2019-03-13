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
        Long id = articleRepo.save(article).getId();
        return id;
    }

    @Transactional
    public Long update(Article article) {
        article.setUpdatedDate(LocalDateTime.now());
        return articleRepo.save(article).getId();
    }

    @Transactional
    public Long delete(Long id) {
        Article article = articleRepo.findOne(id);
        //댓글삭제
        commentRepo.deleteByParent(article);
        articleRepo.delete(article);
        return id;
    }
}
