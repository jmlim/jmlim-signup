<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>로그인</title>
</head>
<body>
${user}
<a class="facebook-login-text" href="/login/facebook">facebook 으로 로그인</a><br/>
<a class="facebook-login-text" href="/login/google">google 로 로그인</a><br/>
<form name='f' action="/signin" method='POST' class="login-form" >
	<input type="hidden" name="${_csrf.parameterName}"
		value="${_csrf.token}" />
	<div class="row">
		<div class="input-field col s12">
			<label for="username">Email</label>
			<input id="username" type="text" name="username" class="validate" /> 
		</div>
	</div>
	<div class="row">
		<div class="input-field col s12">
			<label for="password">Password</label>
			<input id="password" type="password" name="password" class="validate" />
		</div>
	</div>
	<input class="login-btn waves-effect waves-light btn" type="submit"
		value="로그인" />
</form>
</body>
</html>


