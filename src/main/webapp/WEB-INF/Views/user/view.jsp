
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<form method="GET" action="upload">
	<input type="hidden" name="${_csrf.parameterName}"
		value="${_csrf.token}" />


	<c:forEach var="o" items="${result}">
		<ul>
			<tr>
				<td><c:out value="${o.id}" /></td>
				<td><c:out value="${o.name}" /></td>
				<td><c:out value="${o.description}" /></td>
			</tr>
		</ul>
	</c:forEach>


	<select id="names" name="collectionId">
		<c:forEach var="o" items="${result}">
			<option value="<c:out  value="${o.id}" />" selected="selected">${o.name}</option>
					
		</c:forEach>
	</select>


	<button type="submit" class="btn btn-default">
		<span class="glyphicon glyphicon-cloud-upload"></span> Press here to
		upload the file!
	</button>



</form>


