<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html lang="ko-KR">
<head>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
</head>
<body id="top">
<%--<spring:message code="alert.loginEmail" />--%>
	<!-- 회원 권한이 없을 때 -->
	<sec:authorize access="!hasRole('ROLE_USER')">
      	<a href="/signin">로그인</a><br/>
		<a href="/signup">회원가입</a><br/>
    </sec:authorize>

    <!-- 회원 권한이 있을 때 -->
    <sec:authorize access="hasRole('ROLE_USER')">
    	<a href="/article/list">게시판</a>
        <form action="/signout" method="post">
		    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		    <input type="submit" value="Logout" />
		</form>
    </sec:authorize>

	<%@ include file="/WEB-INF/jspf/footer.jspf"%>
	<!-- custom script-->
	<script type="text/javascript">
	</script>
</body>
</html>