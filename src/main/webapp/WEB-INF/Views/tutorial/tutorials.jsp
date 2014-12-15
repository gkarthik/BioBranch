<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<body>
	<div class="container">
		<div class="row">
		<c:choose>
			<c:when test="${!empty success}">
				<c:choose>
				  <c:when test="${success==true}">
				  <div class="alert alert-success alert-dismissible" role="alert">
				  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				  ${message}
				  </div>
				  </c:when>
				   <c:otherwise>
				  <div class="alert alert-warning alert-dismissible" role="alert">
				  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				  ${message}
				  </div>
				  </c:otherwise>
				  </c:choose>
			</c:when>
		</c:choose>
		</div>
		<div class="row">
			<h1>Admin Page</h1>
			<hr>
		</div>
		<div class="row">
			<h3>Populate Pathway</h3>
			<form method="POST" action="${pageContext.request.contextPath}/populate-pathway">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				Source: 
				<select name="source">
					<option value="cpdb">CPDB</option>
				</select>
				<br><br>
				<input type="submit" class="btn btn-primary">
			</form>
		</div>
		<div class="row">
			<h3>Tutorials</h3>
			<a href="${pageContext.request.contextPath}/tutorial/new" class="btn btn-link">Add Tutorial</a>
			<table class="table table-striped">
				<tr>
					<th>Title</th>
					<th>Description</th>
					<th>Url</th>
					<th>--- ---</th>
				</tr>
				<c:forEach var="o" items="${tutorials}">
					<tr>
						<td>${o.title}</td>
						<td>${o.description}</td>
						<td><a href="${o.url}" target="_blank">${o.url}</a></td>
						<td><form method="post" url="."><input name="id" type="hidden" value="${o.id}" /><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /><input type="submit" class="btn btn-link" value="Delete"></form></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>