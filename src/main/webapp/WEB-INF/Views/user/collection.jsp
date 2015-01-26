
<div class="container">
	<div class="col-md-6" style="float: none; margin: 0 auto;">

		<div>
			<a href="/branch/add?user_id=<%=request.getParameter("user_id")%>">Add</a>
		</div>

		<div>
			<a href="/branch/view?user_id=<%=request.getParameter("user_id")%>">View</a>
		</div>

	 <!-- enabling userid as input -->
	<form method="POST" action="view?user_id=<%=request.getParameter("user_id")%>">
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" /> <input type="submit" value="view">
		Click here view your collections.
	</form>
	<!-- enabling userid as input -->
	</div>
</div>

