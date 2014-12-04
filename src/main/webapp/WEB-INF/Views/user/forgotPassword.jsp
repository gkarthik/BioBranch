<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div class="container">
<div class="row">
<sec:authorize access="isAnonymous()" >
<c:if test="${not empty success}">
	<c:if test="${success==true}">
		<div class="alert alert-success" role="alert">${msg}</div>
	</c:if>
	<c:if test="${success==false}">
		<div class="alert alert-danger" role="alert">${msg}</div>
	</c:if>
</c:if>
	<div class="col-md-5">
	 <form:form action="./forgot-password" commandName="forgotPassword" method="POST" enctype="utf8" role="form">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />
						
						<div class="form-group">
							<label class="control-label" for="forgotPassword-email">Please enter your email address</label>
							<form:input path="email" cssClass="form-control" />
							<form:errors path="email" cssClass="help-block" />
						</div>
						<button type="submit" class="btn btn-default btn-primary">
							Reset Password
						</button>
		</form:form>
	</div>
	</sec:authorize>
								<sec:authorize access="isAuthenticated()" >
									<p>
										<spring:message code="text.login.page.authenticated.user.help" />
									</p>
								</sec:authorize>
	</div>
</div>