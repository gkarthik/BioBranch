
<div class="container">

	<div class="col-md-6" style="float: none; margin: 0 auto;">

		<form method="POST" action="upload" enctype="multipart/form-data">
			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}" />

			<h3>
				<span class="label label-default">Dataset File</span>
			</h3>
			<span class="btn btn-default btn-file"> <input type="file"
				name="file">
			</span>

			<h3>
				<span class="label label-default">Mapping File</span>
			</h3>
			<span class="btn btn-default btn-file"> <input type="file"
				name="file">
			</span>

			<h3>
				<span class="label label-default">Feature File</span>
			</h3>
			<span class="btn btn-default btn-file"> <input type="file"
				name="file">
			</span>

			<h3>
				<span class="label label-default">Description</span>
			</h3>
			<span class="btn btn-default btn-file"> <input type="textarea"
				name="description">
			</span>

			<h3>
				<span class="label label-default">Dataset Name</span>
			</h3>
			<span class="btn btn-default btn-file"> <input type="text"
				name="datasetName">
			</span>

			<button type="submit" class="btn btn-default">
				<span class="glyphicon glyphicon-cloud-upload"></span> Press here to
				upload the file!
			</button>
		</form>
	</div>
</div>



