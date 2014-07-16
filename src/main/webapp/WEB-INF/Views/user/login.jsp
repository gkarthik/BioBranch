<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@page import="org.scripps.branch.service.GoogleAuthHelper"%>



<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="../../favicon.ico">


<!-- Bootstrap core CSS -->
<link href="../../dist/css/bootstrap.min.css" rel="stylesheet">
<link href="static/oldStyles/css/landing-style.css" rel="stylesheet" >

<!-- Custom styles for this template -->
<link href="jumbotron-narrow.css" rel="stylesheet">

<title></title>
<link rel="stylesheet" type="text/css"
	href="static/oldStyles/css/social-buttons-3.css" />
<link rel="stylesheet" type="text/css"
	href="static/oldStyles/css/bootstrap-theme.css" />
<link rel="stylesheet" type="text/css"
	href="static/oldStyles/css/bootstrap.css" />


</head>

<body>

	<div class="container" style="width: 800px; margin: 0 auto;">
		<div class="header"></div>

		<div>
			<section>
				<img style="display: inline-block; height: auto max-width: 100%;"
					alt="branch-logo" src="static/img/logo.png" width="1000px">
			</section>

			<section id="head" class="working">
				<div class="inner">

					<span id="toggleDiv"> <a class="btn btn-lg btn-primary one"
						href="#" role="button">Login</a>
					</span>

				</div>
			</section>

			<div id="toggleContent">
				<div class="container">
					<div id="loginbox" style="margin-top: 50px;"
						class="mainbox col-md-6  col-sm-8 col-sm-offset-2">

						<sec:authorize access="isAnonymous()">
							<div class="panel">
								<div>
									<h2>Login by using your Email Id</h2>
									<c:if test="${param.error eq 'bad_credentials'}">
										<div class="alert alert-danger alert-dismissable">
											<button type="button" class="close" data-dismiss="alert"
												aria-hidden="true">&times;</button>
											<spring:message code="text.login.page.login.failed.error" />
										</div>
									</c:if>
									<form action="/branch/login/authenticate" method="POST"
										role="form">
										<input type="hidden" name="${_csrf.parameterName}"
											value="${_csrf.token}" /> <input type="hidden" name="scope"
											value="https://www.googleapis.com/auth/userinfo.profile" />

										<div class="row">
											<div id="form-group-email" class="form-group col-lg-4">
												<input id="user-email" name="username" type="text"
													class="form-control" placeholder="email@example.com" />
											</div>
										</div>

										<div class="row center">
											<div id="form-group-password" class="form-group col-lg-4">
												<input id="user-password" name="password" type="password"
													class="form-control" placeholder="password" />
											</div>
										</div>
										<div class="row">
											<div class="form-group col-lg-4">
												<button type="submit" class="btn btn-default"
													class="btn btn-lg btn-success one">
													<spring:message code="label.user.login.submit.button" />
												</button>
											</div>
										</div>
									</form>
									<div class="row">
										<div class="form-group col-lg-4">
											<a href="/branch/user/register"><spring:message
													code="label.navigation.registration.link" /></a> <span
												id="toggleSocial"> 
											</span>

										</div>
									</div>

								</div>
							</div>

							<div class="panel panel-default">
								<div class="panel">
									<h2>
										<spring:message code="label.social.sign.in.title" />
									</h2>
									<div class="row social-button-row">
										<div class="col-lg-4">
											<a href="<c:url value="/auth/facebook"/>"><button
													class="btn btn-facebook">
													<i class="icon-facebook"></i> |
													<spring:message code="label.facebook.sign.in.button" />
												</button></a>
										</div>
									</div>
									<div class="row social-button-row">
										<div class="col-lg-4">
											<a href="<c:url value="/auth/twitter"/>">

												<button class="btn btn-twitter">
													<i class="icon-twitter"></i> |

													<spring:message code="label.twitter.sign.in.button" />
												</button>


											</a>
										</div>
									</div>


									<div class="row social-button-row">
										<div class="col-lg-4">

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
														out.println("<a href='"
																+ helper.buildLoginUrl()
																+ "'> <button class= 'btn btn-google-plus'> <i class= 'icon-google-plus' ></i> | Sign in with Google + </button> </a>");

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

														out.println(helper.getUserInfoJson(request
																.getParameter("code")));

														out.println("</pre>");

													}
											%>




										</div>
									</div>
								</div>
							</div>
						</sec:authorize>
						<sec:authorize access="isAuthenticated()">
							<p>
								<spring:message code="text.login.page.authenticated.user.help" />
							</p>
						</sec:authorize>


					</div>

				</div>




			</div>

			<div id="column1">
          <div id="sections" class="section">
            <h3 class="about">About</h3>
            <div id="about" >
              <p>Branch is a new Web-based tool for the interactive construction of decision trees from genomic datasets. Branch offers the ability to</p>
              <ol class="unordered-list"> 
	              <li>Construct decision trees by manually selecting features such as genes for a gene expression dataset.</li> 
	              <li>Collaboratively edit and built decision trees. </li>
	              <li>Create feature functions that aggregate content from multiple independent features into single decision nodes (e.g. pathways)</li> 
	              <li>Evaluate decision tree classifiers in terms of precision and recall.</li> 
              </ol>
              <p>The tool is optimized for genomic use cases through the inclusion of gene and pathway-based search functions. </p>
            </div>
            <h3 class="background">Background</h3>
            <div id="background">
              <p>A crucial task in modern biology is the prediction of complex phenotypes, such as breast cancer prognosis, from genome-wide measurements.  Machine learning algorithms can sometimes infer predictive patterns, but there is rarely enough data to train and test them effectively and the patterns that they identify are often expressed in forms (e.g. support vector machines, neural networks, random forests composed of 10s of thousands of trees) that are highly difficult to understand. </p>
            </div>
            
            <h3 class="contact">Contact</h3>
            <div id="contact" >
            <p>Please feel free to get in touch with us via email, twitter, messenger pigeon etc.  See our details on the <a href="contact.jsp">contact page</a>.
            </div>
            <!--
            <h3 class="faq">FAQ</h3>
            <div id="faq" style="display: none;">
              <ol>
                <li><h4>Who can play?</h4>
                  <p>Anyone is welcome to play.  The more you know about biology and disease at the level of gene function, the better you are likely to do, but you can also learn as you go.  The game provides a lot of information about the genes as well as links off to related Web resources.  We hope that anyone who plays will learn something about gene function.</p>
                </li>
                <li><h4>How do you evaluate the quality of the data provided by game players?</h4>
                  <p>The predictors generated using The Cure data are evaluated for accuracy on independent test datasets - just like any other predictor inferred by experts or by statistics would be.
              By testing on real data, we can tell the good players apart from those that are guessing randomly.  Since each player action in the game is associated with their account, it is then very easy to filter out data that is not useful.  
              This approach, while it may seem strange for a scientific project, follows the &lsquo;publish then filter&rsquo; approach that has made the Web so successful.  We hope that it encourages many people to share their time and their intelligence with the project.</p>
              </li>
              </ol>
              </div>
			-->
          </div>
          </div>
          <div id="column2">
	
				<div class="footer">
					<p>&copy; Company 2014</p>
				</div>
			</div>
		</div>

	</div>
	<script src="./static/lib/jquery-1.10.1.js"></script>
	<script type="text/javascript">
		$("#toggleDiv").on("click", function() {
			$('#toggleContent').slideToggle();
		});
		
		if($(".alert-danger").length==0){
			$('#toggleContent').css({'display':'none'});
		}
	</script>
</body>
</html>