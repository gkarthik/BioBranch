<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<meta name="_csrf" content="${_csrf.token}" />
<!-- default header name is X-CSRF-TOKEN -->
<meta name="_csrf_header" content="${_csrf.headerName}" />
<title>Branch</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="//netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css">
<link href='/branch/static/css/bootstrap-tour.min.css' rel='stylesheet'
	type='text/css'>
<link href='/branch/static/css/bootstrap-switch.css' rel='stylesheet'
	type='text/css'>
<link href='/branch/static/css/style.css' rel='stylesheet'
	type='text/css'>
<link rel="stylesheet"
	href="/branch/static/css/odometer-theme-train-station.css" />
<link rel="stylesheet"
	href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/themes/smoothness/jquery-ui.css" />
<style>
.header-right-menu li {
	border-left: 1px solid #EEE;
	padding-right: 10px;
}
</style>
<sitemesh:write property="head" />
</head>
<body>
	<div class="page">
		<nav class="navbar navbar-default" role="navigation">
			<div class="container-fluid">
				<div class="navbar-header">
					<a class="navbar-brand" href="/branch/">Branch</a>
				</div>

				<ul class="nav navbar-nav navbar-right header-right-menu">
					<sec:authorize access="isAnonymous()">
						<li><a href="/branch/user/login">Login</a></li>
						<li><a href="/branch/user/register">Register</a></li>
					</sec:authorize>
					<sec:authorize access="isAuthenticated()">
						<li>
							<p class="nav navbar-nav navbar-right navbar-text sign-in-text">
								<sec:authentication property="principal.socialSignInProvider"
									var="signInProvider" />
								<c:if test="${signInProvider == 'FACEBOOK'}">
									<i class="icon-facebook-sign"></i>
								</c:if>
								<c:if test="${signInProvider == 'TWITTER'}">
									<i class="icon-twitter-sign"></i>
								</c:if>
								<c:if test="${empty signInProvider}">
									<spring:message code="label.navigation.signed.in.as.text" />
								</c:if>
								<!-- <sec:authentication property="principal.username" /> -->
								<strong><a href="/branch/profile/">${firstName}</a></strong>
							</p>
						</li>
						<li>
							<p class="nav navbar-nav navbar-right navbar-text sign-in-text">
								
								<a href="/branch/collection?user_id=${userId}">My Collection</a>
						
							</p>
						</li>
						<li>
							<p>
							<form action="/branch/logout" method="POST">
								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
								<button type="submit" class="btn btn-link">
									<spring:message code="label.navigation.logout.link" />
								</button>
							</form>
							</p>
						</li>
					</sec:authorize>
				</ul>
			</div>
			<!-- /.navbar-collapse -->
	</div>
	<!-- /.container-fluid -->


	</nav>
	</div>
	<div class="content">
		<div id="view-holder">
			<sitemesh:write property="body" />
		</div>
	</div>
	</div>
</body>
</html>