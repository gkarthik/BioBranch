
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container">
	<div class="col-md-6" style="float: none; margin: 0 auto;">

		<form method="GET" action="upload">
			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}" /> <select id="names" name="collectionId">
				<c:forEach var="o" items="${result}">
					<option value="<c:out  value="${o.id}" />" selected="selected">${o.name}</option>

				</c:forEach>
			</select>

			<button type="submit" class="btn btn-default">
				<span class="glyphicon glyphicon-cloud-upload"></span> Click here to
				choose your upload files.
			</button>

		</form>
	</div>
</div>
