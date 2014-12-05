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
	<h3>Reset your Password</h3>
	 <form:form action="" commandName="passwordReset" method="POST" enctype="utf8" role="form">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />
						
						<div class="form-group">
							<label class="control-label" for="passwordReset-password">Enter new Password</label>
							<form:input path="password" cssClass="form-control" />
						</div>
						<div class="form-group">
							<label class="control-label" for="passwordReset-passwordVerification">Reenter new password</label>
							<form:input path="passwordVerification" cssClass="form-control" />
							<form:errors path="passwordVerification" cssClass="help-block" />
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