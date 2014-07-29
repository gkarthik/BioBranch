<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
				<span class="glyphicon glyphicon-cloud-upload"></span> Click here to
				choose your upload files.
			</button>

		</form>
	</div>
</div>

<script src="./static/lib/jquery-1.10.1.js"></script>
<script type="text/javascript">
$(document).ready(function() {
    $('#toggleDiv').click(function() {
         $('#toggleContent').animate({
              height: 'toggle'
              }, 100
         );
    });
});
</script>

<div id="toggleDiv" style="background-color: #333333; color: #FFFFFF; padding: 10px; width: 200px; cursor:pointer;">
  View Other Collections
</div>
<div id="toggleContent" style="display:none;">
<h1>Public Collections</h1>

<ul>
 <c:forEach items="${publicCollection}" var="value">
  <li><c:out value="${value[0]}"/><c:out value="${value[1]}"/> <c:out value="${value[2]}"/> <c:out value="${value[3]}"/><c:out value="${value[4]}"/> </li>
 </c:forEach>
</ul>



</div>