package com.jmlim.signup.comment;

import com.jmlim.signup.account.Account;
import com.jmlim.signup.account.AccountRole;
import com.jmlim.signup.account.CurrentUser;
import com.jmlim.signup.article.Article;
import com.jmlim.signup.article.ArticleRepository;
import com.jmlim.signup.common.exception.ValidCustomException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/article")
public class CommentRestController {
    private final ArticleRepository articleRepo;
    private final CommentRepository commentRepo;
    private final CommentService commentService;
    private final ModelMapper modelMapper;

    public CommentRestController(ArticleRepository articleRepo, CommentRepository commentRepo, CommentService commentService, ModelMapper modelMapper) {
        this.articleRepo = articleRepo;
        this.commentRepo = commentRepo;
        this.commentService = commentService;
        this.modelMapper = modelMapper;
    }

    /**
     * comment
     */
    // http://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html
    // 의 Table 1.1. 참조
    // albums?page=0&size=20&sort=username,asc$sort=name,asc
    @GetMapping(value = "/article/{id}/comment")
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
    public Long commentCreate(@RequestBody @Valid CommentDto.Create commentDto,
                              @PathVariable Long id,
                              @CurrentUser Account account) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setWriter(account);
        return commentService.create(comment, id);
    }

    /** comment end */

    /**
     * @param articleId
     * @param commentId
     * @return
     */
    // 더 좋은 방법이 있는지 찾아볼것.
    @DeleteMapping(value = "/article/{articleId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public Long delete(@PathVariable Long articleId,
                       @PathVariable Long commentId,
                       @CurrentUser Account account) {
        Comment existingComment = commentRepo.findOne(commentId);
        boolean hasAdminRole = false;
        for (AccountRole role : account.getRoles()) {
            if (role == AccountRole.ADMIN) {
                hasAdminRole = true;
            }
        }

        // ADMIN 권한은 삭제 가능.
        if (!hasAdminRole) {
            if (!account.getEmail().equals(existingComment.getWriter())) {
                throw new ValidCustomException("본인이 작성한 글만 삭제가 가능합니다.", "writer");
            }
        }

        return commentService.delete(commentId);
    }
}
