<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
	<div class="container-fluid" id="profile-container"></div>
	<link href='../static/profile/css/profileStyle.css' rel='stylesheet'
		type='text/css'>
	<script type="text/template" id="main-layout-template">
		<div id="sidebar-fixed" class="col-md-12">
<span class="col-md-12">
		<h3>${firstName}</h3>
</span>
<span class="col-md-6">
		<ul class="nav nav-pills">
		  <li id="user-treecollection-button" class="active"><a href="#">Tree Collection</a></li>
		  <li id="community-treecollection-button"><a href="#">Community</a></li>
		</ul>
<div class="input-group">
	<span class="input-group-addon"><i class="glyphicon glyphicon-search"></i></span>
	<input type="text" class="form-control" id="search_collection" placeholder="Search through users, genes and comments.">
</div>
	<span id="loading-wrapper">Loading ... </span>
</span>
</div>
	<div class="col-md-12 collection-wrapper" id="user-treecollection-wrapper">
	</div>
	<div class="col-md-12 collection-wrapper" id="community-treecollection-wrapper" style="display:none;">
	</div>
	<div class="col-md-12 collection-wrapper" id="search-treecollection-wrapper" style="display:none;">
	</div>
	</script>
	<script type="text/template" id="score-entry-template">
<!--	<td><span class='keyValue'> <@= rank @></span></td> -->
	<td><span class='keyValue'><@ if(private_tree){print("<i title='Private Tree' style='cursor: default;color:red;' class='glyphicon glyphicon-eye-close'></i>")} @> <@= user.firstName @></span></td>
	<!-- <td><span class='keyValue'><@ print(Math.round(score.score)) @></span></td> -->
	<!-- <td><span class='keyValue'><@= json_tree.size @></span></td> -->
	<td><span class='keyValue'><@ print(Math.round(json_tree.pct_correct*10)/10) @></span></td>
	<!-- <td><span class='keyValue'><@ print(Math.round(json_tree.novelty*10)/10) @></span></td> -->
	<td><center><@= comment @></center></td>
	<td><svg id="treePreview<@= cid @>"></svg></td>
	<td><span class="keyValue"><@= score.dataset.name @></span></td>
	<td><span class="keyValue"><@ 
		switch(score.testoption){
			case 0:
				print(score.dataset.name);
				break;
			case 1:
				print(score.testset.name);
				break;
			case 2:
				print(score.dataset.name+"<br><b>Split</b>: "+score.testsplit+"%");
				break;
		}
	 @></span></td>
	<td><@ print(created.hourOfDay+":"+created.minuteOfHour+" "+created.monthOfYear+"/"+created.dayOfMonth+"/"+created.yearOfEra) @></td>
	<td><center><a href="../?treeid=<@= id @>&dataset=<@= score.dataset.id @>"><i class="glyphicon glyphicon-edit"></i></a></center></td>
	</script>
	<script type="text/template" id="sortable-header">
<tr>
	<!--	<th><span class='keyValue'><i class="glyphicon glyphicon-star"></i></span></th> -->
	<th id="sort_by_player_name" class="sort-attr"><span class='keyValue'>User</span> <i class="glyphicon glyphicon-sort"></i></th>
	<!-- <th><span class='keyValue'>Score</span></th> -->
	<!-- <th><span class='keyValue'>Size</span></th> -->
	<th id="sort_by_acc" class="sort-attr"><span class='keyValue'>Accuracy</span> <i class="glyphicon glyphicon-sort"></i></th>
	<!-- <th><span class='keyValue'>Novelty</span></th> -->
	<th id="sort_by_comment" class="sort-attr"><center>Comment</center> <i class="glyphicon glyphicon-sort"></th>
	<th><center>Preview</center></th>
	<th id="sort_by_training" class="sort-attr">Training Set <i class="glyphicon glyphicon-sort"></i></th>
	<th id="sort_by_test" class="sort-attr">Test Set <i class="glyphicon glyphicon-sort"></i></th>
	<th id="sort_by_created" class="sort-attr">Created <i class="glyphicon glyphicon-sort"></i></th>
	<th><center>View Tree</center></th>
</tr>
	</script>
	<script type="text/template" id="main-layout-tmpl">
		<div class="col-md-6">
			<h3>Badges Earned</h3>
			<div id="PlayerBadgeRegion">
			</div>
		</div>
		<div class="col-md-6">
			<h3>Badges to be Earned!</h3>
			<div  id="RecBadgeRegion">
			</div>
		</div>
	</script>
	<script type="text/template" id="badge-entry-template">
		<td><span class="badge player-badge">BADGE <@= id @><span class="pictogram">)</span></span></td>
		<td><@= description @></td>
	</script>
	<script type="text/template" id="rec-badge-entry-template">
		<td><span class="badge player-badge">BADGE <@= id @><span class="pictogram">)</span></span></td>
		<td><@= description @></td>
		<td><a class="btn btn-link" href="/cure/cure2.0/index.jsp?badgeid=<@= id @>">Get Badge!</a></td>
	</script>
	<script type="text/template" id="empty-badge-collection-template">
		<h4><center>You are awarded <span class="badge player-badge">BADGES</span> for building trees and completing milestones.<br><br>Start collecting badges by clicking on "Get Badge!" in the "Badges to be Earned" section.</center></h4>
	</script>
	<script type="text/template" id="empty-collection-template">
		<center><h3 class="well">No Trees Present.</h3></center>
	</script>
	<script type="text/javascript">
    var cure_user_experience =null,
        cure_user_id = ${userId},
        cure_user_name = "${firstName}",
        cure_user_email = "${email}";
	</script>
	<script src="../static/lib/underscore.js"></script>
	<script src="../static/lib/jquery-1.10.1.js"></script>
	<script src="../static/lib/backbone.js"></script>
	<script src="../static/lib/marionette.backbone.min.js"></script>
	<script src="../static/lib/d3.v3.js"></script>
	<script>
    //CSRF
	var token = $("meta[name='_csrf']").attr("content");
	  var header = $("meta[name='_csrf_header']").attr("content");
	  $(document).ajaxSend(function(e, xhr, options) {
	    xhr.setRequestHeader(header, token);
	  });
	</script>
	<script src="../static/profile/js/script.js"></script>