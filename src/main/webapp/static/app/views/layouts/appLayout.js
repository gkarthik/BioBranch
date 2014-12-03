define([
	 'marionette', 'd3', 'jquery',
	// Collection
    'app/collections/NodeCollection', 
    'app/collections/ScoreBoard',
    'app/collections/TreeBranchCollection', 
    'app/collections/CollaboratorCollection',
    'app/collections/BadgeCollection',
    'app/collections/GeneCollection',
    'app/collections/DatasetCollection',
    'app/collections/TutorialCollection',
    // Models
    'app/models/Comment', 
    'app/models/Score',
    'app/models/zoom', 
    'app/models/Player',
    'app/models/ConfusionMatrix',
    'app/models/Dataset',
    // Views
    'app/views/JSONCollectionView',
    'app/views/NodeCollectionView',
    'app/views/layouts/sidebarLayout',
    'app/views/layouts/GenePoolLayout',
    'app/views/zoomView', 
    'app/views/DatasetDescriptionView', 
    'app/views/LoginView',
    'text!static/app/templates/appLayout.html'
    ], function(Marionette, d3, $, NodeCollection,
    		ScoreBoard, TreeBranchCollection, CollaboratorCollection, BadgeCollection, GeneCollection, DatasetCollection, TutorialCollection, Comment, Score, Zoom, Player, CfMatrix, Dataset, JSONCollectionView,
    		NodeCollectionView, sidebarLayout, GenePoolLayout, ZoomView, DDescView, LoginView, appLayoutTemplate) {
appLayout = Marionette.Layout.extend({
    template: appLayoutTemplate,
    regions: {
    	"PlayerTreeRegionTree" : "#PlayerTreeRegionTree",
	    "JSONSummaryRegion" : "#jsonSummary",
	    "SideBarRegion": "#cure-panel-wrapper",
	    "ZoomControlsRegion": "#zoom-controls",
	    "LoginRegion": "#LoginRegion",
	    "ScoreBoardRegion" : "#scoreboard_innerwrapper",
	    "GenePoolRegion": "#GenePoolRegion",
	    "FeatureBuilderRegion": "#FeatureBuilderRegion",
	    "TutorialRegion": "#TutorialRegion",
	    "PathwaySearchRegion": "#PathwaySearchRegion",
	    "pickInstanceRegion": "#pickInstRegion",
	    "AggNodeRegion":"#AggNodeRegion",
	    "AttrRankRegion": "#AttrRankRegion",
	    "DatasetDescriptionRegion": "#DatasetDescriptionRegion"
    },
    ui: {
      	"PlayerTreeRegion" : "#PlayerTreeRegion",
    	"PlayerTreeRegionTree" : "#PlayerTreeRegionTree",
    	"PlayerTreeRegionSVG" : "#PlayerTreeRegionSVG",
    },
    events:{
    	
    },
    initialize: function(){
    	
    },
    onShow: function(){
    	$(document).tooltip({
			show: false,
			hide: false,
			position: { my: "top-50", at: "right center" },
			 using: function( position, feedback ) {
				 $( this ).css( position );
				 $( "<div>" )
				 .addClass( "arrow" )
				 .addClass( feedback.vertical )
				 .addClass( feedback.horizontal )
				 .appendTo( this );
				 },
				 tooltipClass: 'custom-tooltip'
		});
		$(this.ui.PlayerTreeRegion).css({
			"width" : Cure.width
		});
		
		
		var zoom = d3.behavior.zoom().scaleExtent([ 1, 1 ]).on("zoom", function() {
			if(d3.event.sourceEvent.type!="mousemove"){
				var top = $("body").scrollTop();
				$("body").scrollTop(top+d3.event.sourceEvent.deltaY);
			} else {
				if (Cure.PlayerNodeCollection.models.length > 0) {
					var t = d3.event.translate, s = d3.event.scale, height = Cure.height, width = Cure.width;
					if (Cure.PlayerSvg.attr('width') != null
							&& Cure.PlayerSvg.attr('height') != null) {
						width = Cure.PlayerSvg.attr('width') * (8 / 9),
						height = Cure.PlayerSvg.attr('height');
					}
					t[0] = Math.min(width / 2 * (s), Math.max(width / 2 * (-1 * s),
							t[0]));
					t[1] = Math.min(height / 2 * (s), Math.max(height / 2
							* (-1 * s), t[1]));
					zoom.translate(t);
					Cure.PlayerSvg.attr("transform", "translate(" + t + ")scale("+Cure.Zoom.get('scaleLevel')+")");
					var splitTranslate = String(t).match(/-?[0-9\.]+/g);
					$("#PlayerTreeRegionTree").css(
							{
								"transform" : "translate(" + splitTranslate[0] + "px,"
								+ splitTranslate[1] + "px)scale("+Cure.Zoom.get('scaleLevel')+")"
							});
				}
			}
		});
		Cure.PlayerSvgWrapper = d3.select("#PlayerTreeRegionSVG");
		Cure.PlayerSvg = d3.select("#PlayerTreeRegionSVG").attr("width", Cure.width)
			.attr("height", Cure.height).call(zoom).append("svg:g")
			.attr("transform", "translate(0,0)").attr("class", "dragSvgGroup");
    	Cure.PlayerNodeCollection = new NodeCollection();
		Cure.TreeBranchCollection = new TreeBranchCollection();
		Cure.CollaboratorCollection = new CollaboratorCollection();
		Cure.Comment = new Comment();
		Cure.Score = new Score();
		Cure.CfMatrix = new CfMatrix();
		Cure.Zoom = new Zoom();
		Cure.BadgeCollection = new BadgeCollection();
		Cure.GeneCollection = new GeneCollection();
		Cure.ZoomView = new ZoomView({
			model: Cure.Zoom
		});
		Cure.Player = new Player();
		Cure.Player.set("username",cure_user_name);
		Cure.Player.set("id",cure_user_id);
		Cure.TutorialCollection = new TutorialCollection();
		Cure.TutorialCollection.fetch();
		Cure.LoginView = new LoginView({model: Cure.Player});
		Cure.PlayerNodeCollectionView = new NodeCollectionView({
			collection : Cure.PlayerNodeCollection
		});
		Cure.JSONCollectionView = new JSONCollectionView({
			collection : Cure.PlayerNodeCollection
		});
		this.GenePoolRegion.show(new GenePoolLayout());
		this.PlayerTreeRegionTree.show(Cure.PlayerNodeCollectionView);
		this.JSONSummaryRegion.show(Cure.JSONCollectionView);
		this.SideBarRegion.show(new sidebarLayout());
		this.ZoomControlsRegion.show(Cure.ZoomView);
		this.LoginRegion.show(Cure.LoginView);
		this.DatasetDescriptionRegion.show(new DDescView());
    }
});
return appLayout;
});
