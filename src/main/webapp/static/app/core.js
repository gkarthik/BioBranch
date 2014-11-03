define([
        // Libraries
        'marionette', 'd3', 'jquery',
        //Layouts
        'app/views/layouts/appLayout',
        // Utilitites
        'app/utilities/utilities',
        //Tour
        'app/tour/tour',
        'app/tour/tree'
        ],
        function(Marionette, d3, $, appLayout, CureUtils, InitTour, TreeTour) {

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
		Backbone.emulateHTTP = true;
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
				Cure.dataset.set('validatecf',true);
				Cure.dataset.set('validatecc',true);
				Cure.dataset.set('validatet',true);
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

		Cure.colorScale = d3.scale.category10();
		Cure.edgeColor = d3.scale.category20();
		Cure.Scorewidth = options["Scorewidth"];
		Cure.Scoreheight = options["Scoreheight"];
		Cure.duration = 500;
		Cure.cluster = d3.layout.tree().size([ (Cure.width-10), "auto" ])
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
		Cure.ScoreBoard = new ScoreBoard();
		
		Cure.addRegions(options.regions);
		Cure.appLayout = new appLayout();
		Cure.appRegion.show(Cure.appLayout);
		
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