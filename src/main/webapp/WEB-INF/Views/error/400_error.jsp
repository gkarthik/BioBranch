<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
	<div class="container">
		<div class="row">
			<div class="jumbotron">
			  <h1>400</h1>
			  <h3>File not found!</h3>
			  <p>Please check the URL again and try refreshing your browser.</p>
			</div>			
			
			<div class="col-md-12">
			 	<b>
			    Exception:  ${exception}<br>
			    </b>
			    <c:forEach items="${exception.stackTrace}" var="ste">    
			        ${ste}<br> 
			    </c:forEach>
		    </div>
	    </div>
    </div>
 
</body>
</html>