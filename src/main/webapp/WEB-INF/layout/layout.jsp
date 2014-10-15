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
<link href='./static/css/bootstrap-tour.min.css' rel='stylesheet'
	type='text/css'>
<link href='./static/css/bootstrap-switch.css' rel='stylesheet'
	type='text/css'>
<link href='./static/css/style.css' rel='stylesheet'
	type='text/css'>
<link rel="stylesheet"
	href="./static/css/odometer-theme-train-station.css" />
<link rel="stylesheet"
	href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/themes/smoothness/jquery-ui.css" />
<style>
.header-right-menu li {
	border-left: 1px solid #EEE;
	padding-right: 10px;
}

footer{
	background: #F8F8F8;
	padding: 10px 0px;
	margin-top: 70px;
	box-shadow: 0px -1px 0px rgba(255, 255, 255, 0.15) inset, 0px -1px 5px rgba(0, 0, 0, 0.075);
}
</style>
<sitemesh:write property="head" />
</head>
<body>
	<div class="page">
		<nav class="navbar navbar-default" role="navigation">
			<div class="container-fluid">
				<div class="navbar-header">
					<a class="navbar-brand" href="${pageContext.request.contextPath}/">Branch</a>
				</div>

				<ul class="nav navbar-nav navbar-right header-right-menu">
					<sec:authorize access="isAnonymous()">
						<li><a href="${pageContext.request.contextPath}/user/login">Login</a></li>
						<li><a href="${pageContext.request.contextPath}/user/register">Register</a></li>
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
								<strong><a href="${pageContext.request.contextPath}/profile/"><sec:authentication
											property="principal.firstName" /></a></strong>
							</p>
						</li>
						<li>
							<p class="nav navbar-nav navbar-right navbar-text sign-in-text">
								<a href="${pageContext.request.contextPath}/datasets">Datasets</a>
							</p>
						</li>
						<li>
							<p class="nav navbar-nav navbar-right navbar-text sign-in-text">
								<a href="${pageContext.request.contextPath}/profile/">Tree Collection</a>
							</p>
						</li>
<!-- 
						<li>
							<p class="nav navbar-nav navbar-right navbar-text sign-in-text">

								<a
									href="./collection?user_id=<sec:authentication property="principal.id" />">My
									Collection</a>

							</p>
						</li>
 -->
 					</sec:authorize>
 					<li>
 						<p class="nav navbar-nav navbar-right navbar-text sign-in-text">
 							<a href="${pageContext.request.contextPath}/contact">Contact</a>
 						</p>
 					</li>
 					<sec:authorize access="isAuthenticated()">
						<li>
							<p>
							<form action="${pageContext.request.contextPath}/logout" method="POST">
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

	<div class="content">
		<div id="view-holder">
			<sitemesh:write property="body" />
		</div>
	</div>
	
	<footer>
	<div class="container">
	  <div class="col-md-6 row">
	    <p><a href="http://sulab.org">The Su Laboratory</a></p>
	    <p><b>Project Team</b>: Benjamin Good, Max Nanis, Salvatore Loguercio, Chunlei Wu, Andrew Su, Karthik G, Vyshakh Babji.</p>
	    <p>An <a href="https://bitbucket.org/sulab/branch/" target="_blank"><span class="pink">Open Source</span></a> Project</p>
	  </div>
	  
	  <div class="col-md-6 row">
	  	<div class="pull-right">
	    <p><a href="http://www.scripps.edu/" target="_blank">The Scripps Research Institute</a></p>
	    <p>10550 North Torrey Pines Road</p>
	    <p>La Jolla, CA 92037</p>
	    </div>
	  </div>
	  <div class="col-md-12 row">
	  <center>
	  <h5><b>Disclaimers</b></h5>
	  <ol>
	  <li>This resource is intended for purely research, educational and entertainment purposes. It should not be used for medical or professional advice.</li>
	  <li>Unless otherwise noted, all non-personally identifiable data entered into this site is stored in a database that will be publicly accessible.</li>
	  </ol>
	  </center>
	</div>
	</div>
	</footer>
</body>
</html>