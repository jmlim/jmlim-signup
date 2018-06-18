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
	<%@ include file="/WEB-INF/jspf/footer.jspf"%>
	<script type="text/javascript">
		$(document).ready(function() {
			coAjax({
				 url: "/article/article/" + ${id},
				 success: contentRenderer
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
							var $field = $('#'+obj.field);
					        if($field && $field.length > 0){
					            $field.siblings('.error-message').remove();
					            $field.after('<div class="error-message">'+obj.defaultMessage+'</div>');
					        }
						});
					}
				});
			});

			function contentRenderer(data) {
				console.log(data);
				var $content = $("#content-section");
				$content.find("#title").text(data.title);
				$content.find("#writer").text(data.writer.email);
				$content.find("#updatedDate").text(data.updatedDate);
				$content.find("#content").text(data.content);
			}
		});
	</script>
</body>
</html>

