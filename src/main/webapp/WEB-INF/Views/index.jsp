<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div id="loading-wrapper">
	<div class="panel panel-default">
		<div class="panel-heading">
			<center>LOADING</center>
		</div>
		<div class="panel-content">
			<center>
				<span id="loadingCount"></span>
			</center>
			<div class="progress progress-striped active">
				<div class="progress-bar" role="progressbar" aria-valuenow="45"
					aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
			</div>
			<center>Loading might take a while.</center>
		</div>
	</div>
</div>
<div id="TutorialRegion"></div>
<div id="NodeDetailsWrapper" class="blurCloseElement">
	<div id="NodeDetailsContent"></div>
</div>
<div id="jsonSummary"></div>
<div class="container-fluid CureContainer">
	<div class="alert alert-warning alert-dismissable" id="alertWrapper">
		<button type="button" class="close" data-dismiss="alert"
			aria-hidden="true">&times;</button>
		<strong id="alertMsg"></strong>
	</div>

	<div class="row">
		<div id="zoom-controls"></div>
		<div id="PlayerTreeRegion"></div>
		<div id="cure-panel-wrapper"></div>
		<div id="GenePoolRegion"></div>
		<div id="FeatureBuilderRegion"></div>
		<script type="text/javascript">
					var cure_user_id = ${userId}, 
						cure_user_name = "${firstName}", 
						cure_tree_id = null, 
						badge_desc = null, 
						badge_id = null, 
						base_url = (document.location.href.indexOf("?")!=-1) ? document.location.href.split("?")[0] : document.location.href/*(document.location.href.indexOf("?")!=-1) ? document.location.href.split("?")[0] : document.location.href.split("branch")[0]+"branch/"*/,
						dataset = {},
						_csb = [ [ null, null ] ],
						classValues = ["y","n"],
				<%if (request.getParameter("treeid") != null) {%>
					cure_tree_id =	<%=request.getParameter("treeid")%>;
				<%}%>
				<%if (request.getParameter("dataset") != null) {%>
					dataset = {
						id: ${dataset.id},
						name: "${dataset.name}",
						description: '${dataset.description}'
					};
					classValues = ["${pos}", "${neg}"];
				<%}%>
					
				</script>
		<%
					if (request.getParameter("ref") != null) {
							if (request.getParameter("ref").equals("yako")) {
				%>
		<script>
					_csb = [ [ 'token', 'R6TzQ7' ] ];
				</script>
		<%
					}
						}
				%>
		<script type="text/javascript" data-main="./static/config.js"
			src="./static/lib/require.js" charset="utf-8"></script>