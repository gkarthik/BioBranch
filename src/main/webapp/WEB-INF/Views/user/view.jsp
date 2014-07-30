<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<sec:authorize access="isAuthenticated()">
	<div class="container">
		<div class="col-md-6" style="float: none; margin: 0 auto;">
			<ul>
				<c:forEach var="o" items="${result}">
					<li>${o.name}<br>${o.description}
						<ol>
							<c:forEach var="j" items="${o.datasets}">
								<li>${j.name}</li>
							</c:forEach>
						</ol>
					</li>
				</c:forEach>
			</ul>

			<form method="GET" action="upload">
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" /> <select id="names" name="collectionId">
					<c:forEach var="o" items="${result}">
						<option value="${o.id}" selected="selected">${o.name}</option>

					</c:forEach>
				</select>

				<button type="submit" class="btn btn-default">
					<span class="glyphicon glyphicon-cloud-upload"></span> Click here
					to choose your upload files.
				</button>

			</form>
		</div>
	</div>

	<script src="./static/lib/jquery-1.10.1.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#toggleDiv').click(function() {
				$('#toggleContent').animate({
					height : 'toggle'
				}, 100);
			});
		});
	</script>
	<br>
	<br>
	<br>
	<div id="toggleDiv" class="container"
		style="background-color: #333333; color: #FFFFFF; padding: 10px; width: 200px; cursor: pointer;">
		View Other Collections</div>
	<div id="toggleContent" class="container" style="display: none;">

		<h1>Public Collections</h1>



		<div class="container">
			<table border="1">
				<tr>
					<th>UserID</th>
					<th>User Name</th>
					<th>Collection Name</th>
					<th>Collection Description</th>
					<th>Dataset Name</th>
					<th>Dataset Description</th>
					<th>Dataset Privacy</th>
				</tr>
				<tr>
					<c:forEach items="${publicCollection}" var="value">


						<tr>
							<td><a
								href="/branch/view?user_id=<c:out value="${value[2].id}"/>"><c:out
										value="${value[2].id}" /></a></td>
							<td><c:out value="${value[2].firstName}" /></td>
							<td><c:out value="${value[1].name}" /></td>
							<td><c:out value="${value[1].description}" /></td>
							<td><c:out value="${value[0].name}" /></td>
							<td><c:out value="${value[0].description}" /></td>

							<td><c:out value="${value[0].privateset}" /></td>
						</tr>
					</c:forEach>
				</tr>
			</table>
		</div>

	</div>

</sec:authorize>