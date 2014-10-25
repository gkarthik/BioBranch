<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div id="main-app-wrapper"></div>
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