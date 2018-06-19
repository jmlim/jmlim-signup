<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="ko-KR">
<head>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
</head>
<body>
         글쓰기 테스트<br/>
	<br> time: ${time}<br/>
	<a href="/article/update/${id}">수정</a>
	<a href="#" id="article-delete">삭제</a>
	<a href="/article/list">목록</a>
   <!-- Content -->
   <section id="content-section">
	<h2 id="title"></h2>
	<h4 id="updatedDate"></h4>
	<h4 id="writer"></h4>
	<p id="content" style="white-space: pre-line"></p>
   </section>
   <input type="hidden" id="_csrf" name="_csrf" value="${_csrf.token}"></input>

	<h4>댓글목록</h4>
	<div id="comment-list-wrap">
	</div>
   <div id="edit-form-wrap">
        <h3 id="edit-title">댓글 등록</h3>
        <form id="comment-upload" method="POST">
            <input type="hidden" id="_csrf" name="_csrf" value="${_csrf.token}"></input>
            <textarea class="form-control" id="content" name="content" rows="3"></textarea>
            <input id="comment-upload-btn" type="button" value="올리기" />
        </form>
    </div>
    
    <template id="comment-template">
		<dl id="comment">
			<dt id="writer">Item1</dt>
			<dt id="createdDate">2018-06-01 11:22:33</dt>
			<dd>
				<p id="content" style="white-space: pre-line">Lorem ipsum dolor vestibulum ante ipsum primis in faucibus vestibulum. Blandit adipiscing eu felis iaculis volutpat ac adipiscing accumsan eu faucibus. Integer ac pellentesque praesent. Lorem ipsum dolor.</p>
				<a href="#" class="comment-delete">삭제</a>
				<input type="hidden" id="comment-id"/>
			</dd>
		</dl>
	</template>
	
	<%@ include file="/WEB-INF/jspf/footer.jspf"%>
	<script type="text/javascript">
		$(document).ready(function() {
			coAjax({
				 url: "/article/article/" + ${id},
				 success: contentRenderer
			});
			coAjax({
				 url: "/article/article/" + ${id} +"/comment",
				 success: commentRenderer
			});

			$("#article-delete").on("click", function(e) {
				var _csrf = $("#_csrf").val();
				coAjax({
					 url: "/article/article/" + ${id},
					 type: 'DELETE', 
					 beforeSend : function(request) {
						request.setRequestHeader("X-CSRF-TOKEN", _csrf);
					 },
					 success: function(data) {
						console.log(data);
						alert("삭제되었습니다.")
						location.href = "/article/list";
					 },
					 error : function(xhr, status, err) {
						var errors = xhr.responseJSON.errors;
						$('.error-message').remove();
						$.each(errors, function(ind, obj) {
							var $field = $("#content-section").find('#'+obj.field);
					        if($field && $field.length > 0){
					            $field.siblings('.error-message').remove();
					            $field.after('<div class="error-message">'+obj.defaultMessage+'</div>');
					        }
						});
					}
				});
			});

			//=====
			$("#comment-upload-btn").on("click", function(e) {
				console.log(e);
				var $form = $("#comment-upload");
				var url = "/article/article/" + ${id} + "/comment";
				var type = 'POST';
				var _csrf = $('#_csrf').val();
				if (console) {
					console.log(JSON.stringify($form.serializeObject()));
				}
		
				$.ajax({
					url : url,
					dataType : 'json',
					type : type,
					data : JSON.stringify($form.serializeObject()),
					cache : false,
					contentType : 'application/json',
					beforeSend : function(request) {
						request.setRequestHeader("X-CSRF-TOKEN", _csrf);
					},
					success : function(data) {
						if (data) {
							console.log(data);
							//redirect or ajax render
							location.href = "/article/content/"+${id};
						}
					}.bind(this),
					error : function(xhr, status, err) {
						var errors = xhr.responseJSON.errors;
						$('.error-message').remove();
						$.each(errors, function(ind, obj) {
							var $field = $form.find('#'+obj.field);
					        if($field && $field.length > 0){
					            $field.siblings('.error-message').remove();
					            $field.after('<div class="error-message">'+obj.defaultMessage+'</div>');
					        }
						});
						//<div class="error-message">이메일 에러</span>
					}.bind(this)
				});
			});

			//코멘트 삭제
			$( document ).on( "click", "#comment-list-wrap .comment-delete", function( e ) {
				var commentId = $("#comment-id").val();
				var url = "/article/article/" + ${id} + "/comment/" + commentId;
				var type = 'DELETE';
				var _csrf = $('#_csrf').val();
				coAjax({
					 url: url,
					 type: type, 
					 beforeSend : function(request) {
						request.setRequestHeader("X-CSRF-TOKEN", _csrf);
					 },
					 success: function(data) {
						console.log(data);
						alert("삭제되었습니다.")
						//redirect or ajax render
						location.href = "/article/content/"+${id};
					 },
					 error : function(xhr, status, err) {
						var errors = xhr.responseJSON.errors;
						$('.error-message').remove();
						$.each(errors, function(ind, obj) {
							var $field = $("#comment-list-wrap").find('#'+obj.field);
					        if($field && $field.length > 0){
					            $field.siblings('.error-message').remove();
					            $field.after('<div class="error-message">'+obj.defaultMessage+'</div>');
					        }
						});
					}
				});
			});
			
			function contentRenderer(data) {
				console.log('content',data);
				var $content = $("#content-section");
				$content.find("#title").text(data.title);
				$content.find("#writer").text(data.writer.email);
				$content.find("#updatedDate").text(data.updatedDate);
				$content.find("#content").text(data.content);
			}

			/**
				코멘트 렌더링
			*/
			function commentRenderer(data) {
				console.log('comment',data);
				var $commentListWrap = $("#comment-list-wrap");
				$commentListWrap.empty();
				var temp = $("#comment-template")[0];
				var clon = temp.content.cloneNode(true);
				var comment = clon.querySelector("#comment");
				
				//list
				if(data && data.totalElements > 0) {
					console.log(data);
					$.each(data.content, function(ind, obj) {	
						console.log('obj'+ind, obj);
						var $comment = $(comment);
						$comment.find("#comment-id").val(obj.id)
						$comment.find("#writer").text(obj.writer.email);
						$comment.find("#content").text(obj.content);
						$comment.find("#createdDate").text(obj.createdDate);
						$commentListWrap.append($comment.clone());
					});
				}
			}
		});
	</script>
</body>
</html>

