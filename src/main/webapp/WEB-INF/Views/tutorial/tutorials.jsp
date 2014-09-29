<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<body>
	<div class="container">
		<div class="row">
			<% if(request.getParameter("message").equals("success")){ %>
				<div class="alert alert-success alert-dismissible" role="alert">
				  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				  <strong>Success!</strong> Tutorial Added.
				</div>
			<% } %>
		</div>
		<div class="row">
			<h2>Datasets</h2>
			<table class="table table-striped">
				<tr>
					<th>Title</th>
					<th>Description</th>
					<th>Url</th>
				</tr>
				<c:forEach var="o" items="${tutorials}">
					<tr>
						<td>${o.title}</td>
						<td>${o.description}</td>
						<td><a href="${o.url}" target="_blank">${o.url}</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>