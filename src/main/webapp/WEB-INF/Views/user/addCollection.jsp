<div class="container">
	<div class="col-md-6" style="float: none; margin: 0 auto;">
		<form method="POST" action="add">
			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}" />

			<h3>
				<span class="label label-default">Collection Name</span>
			</h3>
			<span class="btn btn-default btn-file"> <input type="text"
				name="name">
			</span>

			<h3>
				<span class="label label-default">Description</span>
			</h3>
			<span class="btn btn-default btn-file"> <input type="text"
				name="description">
			</span>

			<button type="submit" class="btn btn-default">
				<span class="glyphicon glyphicon-cloud-upload"></span> Press here to
				add collection.
			</button>
		</form>
	</div>
</div>