<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<title></title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/static/js/app/user.form.js"></script>
</head>
<body>
<div class="container">
<div class="col-md-6" style="float:none; margin:0 auto;">
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
	<sec:authorize access="isAnonymous()">
		<div class="panel panel-primary">
			<div class="panel-heading">
					<div class="page-header">
				<h1>
					<spring:message code="label.user.registration.page.title" />
				</h1>
			</div>
			</div>
			<div class="panel-body">
				<form:form action="${contextPath}/user/register" commandName="user"
					method="POST" enctype="utf8" role="form">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />
					<c:if test="${user.signInProvider != null}">
						<form:hidden path="signInProvider" />
					</c:if>
					<div>
						<div id="form-group-firstName" class="form-group">
							<label class="control-label" for="user-firstName"><spring:message
									code="label.user.firstName" />:</label>
							<form:input id="user-firstName" path="firstName"
								cssClass="form-control" />
							<form:errors id="error-firstName" path="firstName"
								cssClass="help-block" />
						</div>
					</div>
					<div>
						<div id="form-group-lastName" class="form-group">
							<label class="control-label" for="user-lastName"><spring:message
									code="label.user.lastName" />:</label>
							<form:input id="user-lastName" path="lastName"
								cssClass="form-control" />
							<form:errors id="error-lastName" path="lastName"
								cssClass="help-block" />
						</div>
					</div>
					<div>
						<div id="form-group-email" class="form-group">
							<label class="control-label" for="user-email"><spring:message
									code="label.user.email" />:</label>
							<form:input id="user-email" path="email" cssClass="form-control" />
							<form:errors id="error-email" path="email" cssClass="help-block" />
						</div>
					</div>

					<div>
						<div id="form-group-firstName " class="form-group">
							<label class="control-label" for="background">Education
								Background:</label>
							<form:input id="background" path="background"
								cssClass="form-control" />

						</div>
					</div>


					<div>
						<div id="form-group-firstName " class="form-group">
							<label class="control-label" for="purpose">Purpose:</label>
							<form:input id="purpose" path="purpose" cssClass="form-control" />

						</div>
					</div>


					<c:if test="${user.signInProvider == null}">
						<div>
							<div id="form-group-password" class="form-group">
								<label class="control-label" for="user-password"><spring:message
										code="label.user.password" />:</label>
								<form:password id="user-password" path="password"
									cssClass="form-control" />
								<form:errors id="error-password" path="password"
									cssClass="help-block" />
							</div>
						</div>
						<div>
							<div id="form-group-passwordVerification"
								class="form-group">
								<label class="control-label" for="user-passwordVerification"><spring:message
										code="label.user.passwordVerification" />:</label>
								<form:password id="user-passwordVerification"
									path="passwordVerification" cssClass="form-control" />
								<form:errors id="error-passwordVerification"
									path="passwordVerification" cssClass="help-block" />
							</div>
						</div>
					</c:if>
					<button type="submit" class="btn btn-default btn-primary">
						<spring:message code="label.user.registration.submit.button" />
					</button>
				</form:form>
			</div>
		</div>
	</sec:authorize>
	<sec:authorize access="isAuthenticated()">
		<p>
			<spring:message code="text.registration.page.authenticated.user.help" />
		</p>
	</sec:authorize>
	</div>
	</div>
</body>
</html>