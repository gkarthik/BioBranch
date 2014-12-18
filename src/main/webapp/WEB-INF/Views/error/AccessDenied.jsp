<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
	<div class="container">
		<div class="row">
			<div class="jumbotron">
			  <h1>Access Denied</h1>
			  <h3>
				  <ul>
				  	<li>Your session might have just expired. Please login again.</li>
				  	<li>You don't have access.</li>
				  </ul>
			  </h3>
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