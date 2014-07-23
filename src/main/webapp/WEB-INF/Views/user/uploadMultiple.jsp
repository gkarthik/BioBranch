
<div class="panel panel-default">
	<div class="panel-body">

		<form method="POST" action="uploadMultipleFile"
			enctype="multipart/form-data">
			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}" /> <label>File1 to upload:</label> <input
				type="file" name="file"><br /> <label>Name1:</label> <input
				type="text" name="name"><br /> <br /> 
				File2 to upload: <input type="file" name="file"><br /> <label>Name2</label>

			<input type="text" name="name"><br /> <br /> <label>
				File3 to upload:</label> <input type="file" name="file"><br /> <label>Name3</label>
			<input type="text" name="name"><br /> <br />

			<section>
				<label>Description</label> <input type="text" name="description"
					height="100" width="100">
			</section>

			<section>
				<label>Dataset Name </label> <input type="text" name="datasetName"
					height="100" width="100">
			</section>


			<button type="submit" class="btn btn-default">
				<span class="glyphicon glyphicon-cloud-upload"></span> Press here to
				upload the file!
			</button>
		</form>

	</div>
</div>


