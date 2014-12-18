<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div class="container">
<div class="row">
<c:if test="${not empty success}">
	<c:if test="${success==true}">
		<div class="alert alert-success" role="alert">${msg}</div>
	</c:if>
	<c:if test="${success==false}">
		<div class="alert alert-danger" role="alert">${msg}</div>
	</c:if>
</c:if>
	<div class="col-md-5">
	 <form:form action="./request-dataset" commandName="requestDataset" method="POST" enctype="utf8" role="form">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />
						
						<div class="form-group">
							<label class="control-label" for="requestDataset-firstName">First Name</label>
							<form:input path="firstName" cssClass="form-control" />
							<form:errors path="firstName" cssClass="help-block" />
						</div>
						<div class="form-group">
							<label class="control-label" for="requestDataset-lastName">Last Name</label>
							<form:input path="lastName" cssClass="form-control" />
							<form:errors path="lastName" cssClass="help-block" />
						</div>
						<div class="form-group">
							<label class="control-label" for="requestDataset-dataDescription">Describe the dataset you want setup</label>
							<form:textarea path="dataDescription" cssClass="form-control" />
							<form:errors path="dataDescription" cssClass="help-block" />
						</div>
						<div class="form-group">
							<label class="control-label" for="requestDataset-reason">Why do you want the dataset uploaded to Branch?</label>
							<form:textarea path="reason" cssClass="form-control" />
							<form:errors path="reason" cssClass="help-block" />
						</div>
						<div class="form-group">
							<label class="control-label" for="requestDataset-privateToken">Do you want the dataset to be public?</label>
							<form:radiobutton path="privateToken" value="true" /> Yes
							<form:radiobutton path="privateToken" value="false" /> No
							<form:errors path="privateToken" cssClass="help-block" />
						</div>
						<div class="form-group">
							<label class="control-label" for="requestDataset-email">Email address</label>
							<form:input path="email" cssClass="form-control" />
							<form:errors path="email" cssClass="help-block" />
						</div>
						<button type="submit" class="btn btn-default btn-primary">
							Send Request
						</button>
		</form:form>
	</div>
	</div>
</div>