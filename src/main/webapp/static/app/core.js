define([
        // Libraries
        'marionette', 'd3', 'jquery',
        // Collection
        'app/collections/ClinicalFeatureCollection',
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
        'app/views/LoginView',
        // Utilitites
        'app/utilities/utilities',
        //Tour
        'app/tour/tour',
        'app/tour/tree'
        ],
        function(Marionette, d3, $, ClinicalFeatureCollection, NodeCollection,
        		ScoreBoard, TreeBranchCollection, CollaboratorCollection, BadgeCollection, GeneCollection, DatasetCollection, TutorialCollection, Comment, Score, Zoom, Player, CfMatrix, Dataset, JSONCollectionView,
        		NodeCollectionView, sidebarLayout, GenePoolLayout, ZoomView, LoginView, CureUtils, InitTour, TreeTour) {

	//CSRF
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});
	Cure = new Marionette.Application();
	Cure.utils = CureUtils;
	//Tour Init
	Cure.initTour = InitTour;
	Cure.treeTour = TreeTour;

	Cure
	.addInitializer(function(options) {
		// JSP Uses <% %> to render elements and this clashes
		// with default underscore templates.
		_.templateSettings = {
				interpolate : /\<\@\=(.+?)\@\>/gim,
				escape : /\<\@\-(.+?)\@\>/gim
		};
		Backbone.emulateHTTP = true;

		var args = {
				command : "get_trees_all",
		};

		$(options.regions.PlayerTreeRegion).html(
				"<div id='" + options.regions.PlayerTreeRegion.replace("#", "")
				+ "Tree'></div><svg id='"
				+ options.regions.PlayerTreeRegion.replace("#", "")
				+ "SVG'></svg>");
		Cure.width = options["width"];
		Cure.height = options["height"];
		Cure.posNodeName = options["posNodeName"];
		Cure.negNodeName = options["negNodeName"];
		Cure.scoreWeights = options.scoreWeights;
		Cure.startTour = options.startTour;
		
		//Dataset
		Cure.dataset = new Dataset();
		Cure.dataset.set(dataset);
		Cure.validateFeature = {};
		var args = {
				command : "validate_features",
				dataset : Cure.dataset.get('id')
		};
		//Default Node Attributes
		Cure.NodeDefaults = {
				'name' : '',
				'cid' : 0,
				getSplitData: false,
				edit : 0,
				highlight : 0,
				majClass: '', 
				modifyAccLimit: 1,
				pickInst: false,
				getRankedAttr: false,
				children : [],
				manual_pct_correct: 0,
				gene_summary : {
					"summaryText" : "",
					"goTerms" : {},
					"generif" : {},
					"name" : ""
				},
				accLimit: 0,
				showJSON : 0,
				x: 0,
				y: 0,
				x0 : 0,
				y0: 0
			};
		
		Cure.utils.showLoading(null);
		//POST request to server.		
		$.ajax({
			type : 'POST',
			url : base_url+'MetaServer',
			data : JSON.stringify(args),
			dataType : 'json',
			contentType : "application/json; charset=utf-8",
			success : function(data){
				Cure.utils.hideLoading(null);
				Cure.dataset.set('validateGenes', data.genes);
				Cure.dataset.set('validateNonGenes', data.non_genes);
				Cure.dataset.set('infoGainMin',data.infoGainMin);
				Cure.dataset.set('infoGainMax',data.infoGainMax);
				Cure.infogainScale = d3.scale.linear().domain([data.infoGainMin, data.infoGainMax]).range(["#E5F5E0","#00441B"]);
			},
			error : function(){
				Cure.utils
				.showAlert("<strong>Server Error</strong><br>Please try again in a while.", 0);
			}
		});
		

		// Scales
		Cure.accuracyScale = d3.scale.linear().domain([ 0, 100 ]).range(
				[ 0, 100 ]);
		Cure.noveltyScale = d3.scale.linear().domain([ 0, 1 ]).range(
				[ 0, 100 ]);
		Cure.sizeScale = d3.scale.linear().domain([ 0, 1 ]).range(
				[ 0, 100 ]);

		function zoomed() {
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
		}

		$(options.regions.PlayerTreeRegion + "Tree").css({
			"width" : Cure.width
		});

		var zoom = d3.behavior.zoom().scaleExtent([ 1, 1 ]).on("zoom",
				zoomed);

		Cure.PlayerSvg = d3
		.select(options.regions.PlayerTreeRegion + "SVG").attr("width",
				Cure.width).attr("height", Cure.height).call(zoom).append(
				"svg:g").attr("transform", "translate(0,0)").attr("class",
				"dragSvgGroup");

		// Event Initializers
		$("body").delegate(".close", "click", function() {
			if ($(this).parent().hasClass("alert")) {
				$(this).parent().hide();
			} else if(!$(this).hasClass("close-json-view")) {
				$(this).parent().parent().parent().hide();
			}
		});

		$(document).on("mouseup",
				function(e) {
			var classToclose = $('.blurCloseElement');
			if (!classToclose.is(e.target)
					&& classToclose.has(e.target).length == 0) {
				classToclose.hide();
			}
		});

		//TODO: MOVE TO ScoreBoardView
		Cure.ScoreBoardRequestSent = false;
		$("#scoreboard_wrapper").scroll(
				function() {
					if ($("#scoreboard_wrapper").scrollTop()+$("#scoreboard_wrapper").height() >= $("#scoreboard_innerwrapper").height()) {
						var t = window.setTimeout(function(){
							if (!Cure.ScoreBoardRequestSent) {
								window.clearTimeout(t);
								Cure.ScoreBoard.fetch();
								Cure.ScoreBoardRequestSent = true;
							}
						}, 500);
					}
				});

		options.regions.PlayerTreeRegion += "Tree";
		Cure.addRegions(options.regions);
		Cure.colorScale = d3.scale.category10();
		Cure.edgeColor = d3.scale.category20();
		Cure.Scorewidth = options["Scorewidth"];
		Cure.Scoreheight = options["Scoreheight"];
		Cure.duration = 500;
		Cure.cluster = d3.layout.tree().size([ (Cure.width-100), "auto" ])
		.separation(function(a, b) {
			try {
				if (a.children.length > 2) {
					return a.children.length;
				}
			} catch (e) {

			}
			return (a.parent == b.parent) ? 1 : 2;
		});
		Cure.diagonal = d3.svg.diagonal().projection(function(d) {
			return [ d.x, d.y ];
		});
		Cure.TestSets = new DatasetCollection();
		Cure.ClinicalFeatureCollection = new ClinicalFeatureCollection();
		Cure.ClinicalFeatureCollection.fetch();
		Cure.ScoreBoard = new ScoreBoard();
		Cure.PlayerNodeCollection = new NodeCollection();
		Cure.TreeBranchCollection = new TreeBranchCollection();
		Cure.CollaboratorCollection = new CollaboratorCollection();
		Cure.GenePoolRegion.show(new GenePoolLayout());
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
		Cure.sidebarLayout = new sidebarLayout();
		Cure.PlayerTreeRegion.show(Cure.PlayerNodeCollectionView);
		Cure.JSONSummaryRegion.show(Cure.JSONCollectionView);
		Cure.SideBarRegion.show(Cure.sidebarLayout);
		Cure.ZoomControlsRegion.show(Cure.ZoomView);
		Cure.LoginRegion.show(Cure.LoginView);
		Cure.relCoord = $('#PlayerTreeRegionSVG').offset();
		Cure.instanceData = {};
		
		if(cure_tree_id!=undefined){
			var args = {
					command : "get_tree_by_id",
					dataset : "metabric_with_clinical",
					treeid : cure_tree_id
			};

			//POST request to server.		
			$.ajax({
				type : 'POST',
				url : base_url+'MetaServer',
				data : JSON.stringify(args),
				dataType : 'json',
				contentType : "application/json; charset=utf-8",
				success : function(data){
					if(data.json_tree){
						Cure.PlayerNodeCollection.parseTreeinList(data);
					} else {
						Cure.utils
						.showAlert("<strong>Server Error</strong><br>Please try again in a while.", 0);
					}
				},
				error : function(){
					Cure.utils
					.showAlert("<strong>Server Error</strong><br>Please try again in a while.", 0);
				}
			});
		}
	});
	return Cure;
});