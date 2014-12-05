<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
	<div class="container">
		<div class="row">
			<div class="jumbotron">
			<h1>500</h1>
			  <h3>Oops! Something broke!</h3>
			  <p>Try refreshing your browser. 
				<br><b>PS: You could help me debug this while you're at it.</b></p>
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