
<!DOCTYPE html>
<%@page import="org.scripps.branch.service.GoogleAuthHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<div class="page-header">
	<h1>
		<spring:message code="label.homepage.title" />
		<sec:authentication property="principal.firstName" />
		<sec:authentication property="principal.lastName" />
	</h1>
</div>
<div>
	<p>
		<spring:message code="text.homepage.greeting" />
	</p>
</div>
<div class="oauthDemo">
	<%
			/*
			 * The GoogleAuthHelper handles all the heavy lifting, and contains all "secrets"
			 * required for constructing a google login url.
			 */
			final GoogleAuthHelper helper = new GoogleAuthHelper();

			if (request.getParameter("code") == null
					|| request.getParameter("state") == null) {

				/*
				 * initial visit to the page
				 */
				out.println("<a href='" + helper.buildLoginUrl()
						+ "'>log in with google</a>");

				/*
				 * set the secure state token in session to be able to track what we sent to google
				 */
				session.setAttribute("state", helper.getStateToken());

			} else if (request.getParameter("code") != null
					&& request.getParameter("state") != null
					&& request.getParameter("state").equals(
							session.getAttribute("state"))) {

				session.removeAttribute("state");

				out.println("<pre>");
				/*
				 * Executes after google redirects to the callback url.
				 * Please note that the state request parameter is for convenience to differentiate
				 * between authentication methods (ex. facebook oauth, google oauth, twitter, in-house).
				 *
				 * GoogleAuthHelper()#getUserInfoJson(String) method returns a String containing
				 * the json representation of the authenticated user's information.
				 * At this point you should parse and persist the info.
				 */

				out.println(helper.getUserInfoJson(request.getParameter("code")));

				out.println("</pre>");

			}
		%>



</div>
<br />