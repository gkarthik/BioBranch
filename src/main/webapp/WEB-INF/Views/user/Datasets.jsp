<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<body>
	<div class="container">
		<div class="row">
			<h2>Datasets</h2>
			<table class="table table-striped">
				<tr>
					<th>Name</th>
					<th>Description</th>
					<th><span>--- ---</span></th>
				</tr>
				<c:forEach var="o" items="${datasets}">
					<tr>
						<td>${o.name}</td>
						<td>${o.description}</td>
						<td><a href="./?dataset=${o.id}" class='btn btn-link'>Use</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>