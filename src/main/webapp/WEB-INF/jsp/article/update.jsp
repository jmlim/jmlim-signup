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
	<a href="/article/list">목록</a>
    <div id="edit-form-wrap">
        <h3 id="edit-title">수정</h3>
        <form id="article-upload" method="PUT">
            <input type="hidden" id="_csrf" name="_csrf" value="${_csrf.token}"></input>
            <input type="text" id="title" name="title" /><br/><br/>
            <textarea class="form-control" id="content" name="content" rows="3"></textarea>
            <input id="article-upload-btn" type="button" value="올리기" />
        </form>
    </div>
    
	<%@ include file="/WEB-INF/jspf/footer.jspf"%>
	<script type="text/javascript">
		$(document).ready(function() {
			var url = "/article/article/" + ${id};
			coAjax({
				 url: url,
				 success: updateDataRenderer
			});
		
			$("#article-upload-btn").on("click", function(e) {
				console.log(e);
				var $form = $("#article-upload");
				var type = 'PUT';
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
							//redirect
							location.href = "/article/list";
						}
					}.bind(this),
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
						//<div class="error-message">이메일 에러</span>
					}.bind(this)
				});
			});
			
			function updateDataRenderer(data) {
				var $articleUpload = $("#article-upload");
				$articleUpload.find("#title").val(data.title);
				$articleUpload.find("#content").val(data.content);
			}
		});
	</script>
</body>
</html>

