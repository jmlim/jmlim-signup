<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>회원가입</title>

<script src="https://code.jquery.com/jquery-1.12.4.js"
	integrity="sha256-Qw82+bXyGq6MydymqBxNPYTaUXXq7c8v3CwiYwLLNXU="
	crossorigin="anonymous"></script>
</head>
<body>
<a class="facebook-login-text" href="/login/facebook">facebook으로 가입</a><br/>
<a class="facebook-login-text" href="/login/google">google로 가입</a><br/>
<form name='f' id="signup-form" method='POST' class="signup-form" >
	<input id="_csrf" type="hidden" name="${_csrf.parameterName}"
		value="${_csrf.token}" />
      <div class="sminputs">
          <div class="input full">
              <label class="string optional" for="user-email">Email*</label>
              <input class="string optional" maxlength="255" id="email" placeholder="Email" name="email" type="email" size="50" />
          </div>
      </div>
      <div class="sminputs">
          <div class="input string optional">
              <label class="string optional" for="user-pw">Password *</label>
              <input class="string optional" maxlength="255" id="password" name="password" placeholder="Password" type="password" size="50" />
          </div>
          <div class="input string optional">
              <label class="string optional" for="user-pw-repeat">Repeat password *</label>
              <input class="string optional" maxlength="255" id="repeatPassword" name="repeatPassword" placeholder="Repeat password" type="password" size="50" />
          </div>
      </div>
      <div class="simform__actions">
          <input class="ajax_submit" id="ajax_submit" name="commit" type="button" value="회원가입" />
      </div>
</form>

<script src="js/common.js"></script>
<script type="text/javascript">
	$("#ajax_submit").on("click", function(e) {
		console.log(e);
		var form = $("#signup-form");
		var url = '/signup';
		var type = 'POST';
		var _csrf = $('#_csrf').val();
		if (console) {
			console.log(JSON.stringify(form.serializeObject()));
		}

		$.ajax({
			url : url,
			dataType : 'json',
			type : type,
			data : JSON.stringify(form.serializeObject()),
			cache : false,
			contentType : 'application/json',
			beforeSend : function(request) {
				request.setRequestHeader("X-CSRF-TOKEN", _csrf);
			},
			success : function(data) {
				if (data) {
					console.log(data);
					
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
</script>
</body>
</html>



  


