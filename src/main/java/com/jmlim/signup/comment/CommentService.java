package com.jmlim.signup.comment;

import com.jmlim.signup.article.ArticleDto;
import com.jmlim.signup.account.Account;
import com.jmlim.signup.article.Article;
import com.jmlim.signup.common.exception.ValidCustomException;
import com.jmlim.signup.account.AccountRepository;
import com.jmlim.signup.article.ArticleRepository;
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
        Long id = commentRepo.save(comment).getId();
        return id;
    }

    @Transactional
    public Long delete(Long id) {
        Comment comment = commentRepo.findOne(id);
        commentRepo.delete(comment);
        return id;
    }
}
