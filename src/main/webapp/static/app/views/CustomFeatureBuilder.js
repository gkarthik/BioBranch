define([
  //Libraries
	'jquery',
	'marionette',
	//Templates
	'text!static/app/templates/CustomFeatureBuilder.html',
	//View
	'app/views/GeneListCollectionView',
	'app/collections/GeneCollection',
	//Templates
	'text!static/app/templates/GeneSummary.html',
	'text!static/app/templates/ClinicalFeatureSummary.html',
	//Plugins
	'myGeneAutocomplete',
	'jqueryui'
    ], function($, Marionette, FeatureBuilderTmpl, GeneCollView, GeneCollection, geneinfosummary, cfsummary) {
FeatureBuilderView = Marionette.Layout.extend({
	className: 'panel panel-default',
	ui: {
		gene_query: '.gene_query',
		customfeature_query: '.customfeature_query',
		cf_query: '.cf_query',
		equation: '#feature-builder-equation',
		testWidth: '#test-span-width'
	},
	events: {
		'click .choose-gene': 'openGene',
		'click .choose-cf': 'openCf',
		'click .choose-customfeature': 'openCustomFeature',
		'keypress #feature-builder-equation': 'highlightFeatures'
	},
	regions: {
		geneCollRegion: '.gene-coll-region'
	},
	url: base_url+'MetaServer',
	initialize: function(){
		this.geneColl = new GeneCollection();
	},
	highlightFeatures: function(){
		var split = $(this.ui.equation).val().match(/([A-Za-z0-9])+/g);
		var terms;
		for(var temp in split){
			if(this.geneColl.findWhere({"short_name":split[temp].toUpperCase()})){
				console.log(split[temp].toLowerCase());
				$(this.ui.testWidth).html(split[temp].toUpperCase());
				terms = $(this.ui.equation).val().match(split[temp]);
				for(var term in terms){
					
				}
				$(this.ui.testWidth).width();
			}
		}
	},
	openGene: function(){
		$(".category-wrapper").hide();
		$(".category-gene").show();
	},
	openCf: function(){
		$(".category-wrapper").hide();
		$(".category-cf").show();
	},
	openCustomFeature: function(){
		$(".category-wrapper").hide();
		$(".category-customfeature").show();
	},
	showCustomFeatures: function(){
		 var thisURL = this.url;
		 var thisView = this;
	      $(this.ui.customfeature_query).autocomplete({
	  			source: function( request, response ) {
	  					var args = {
	    	        command : "custom_feature_search",
	    	        query: request.term
	    	      };
	    	      $.ajax({
	    	          type : 'POST',
	    	          url : thisURL,
	    	          data : JSON.stringify(args),
	    	          dataType : 'json',
	    	          contentType : "application/json; charset=utf-8",
	    	          success : function(data){
	    	          	response( $.map( data, function( item ) {
	    	          		return {
	    	          		  label: item.name+": "+item.description,
	    	          		  value: item.name,
	    	          		  data: item
	    	          	  };
	    	          	}));
	    	        }
	    	      });
	  				},
	  				minLength: 0,
	  				select: function( event, ui ) {
	  					if(ui.item.label != undefined){//To ensure "no gene name has been selected" is not accepted.
	  						if(!Cure.initTour.ended()){
	  							Cure.initTour.end();
	  						}
	  						$("#SpeechBubble").remove();
	  						thisView.geneColl.add([{
	  							unique_id: "custom_feature_"+ui.item.data.name,
	  							short_name: ui.item.data.name.toUpperCase(),
	  							long_name: ui.item.data.name
	  						}]);
//	  						 else {
//	  							new Node({
//	  								'name' : ui.item.data.name,
//	  								"options" : {
//	  									"unique_id" : "custom_feature_"+ui.item.data.name,
//		  								"kind" : "split_node",
//		  								"full_name" : '',
//		  								"description" : ui.item.data.description
//	  								}
//	  							});
//	  						}
	  					}
	  				},
	  			}).bind('focus', function(){ $(this).autocomplete("search"); } );
	},
	template: FeatureBuilderTmpl,
	showCf: function(){
		var thisUi = this.ui;
		var thisView = this;
		//Clinical Features Autocomplete
		var availableTags = Cure.ClinicalFeatureCollection.toJSON();
		
		$(this.ui.cf_query).autocomplete({
			create: function(){
				$(this).data("ui-autocomplete")._renderItem = function( ul, item ) {
					var rankIndicator = $("<div>")
					.css({"background": Cure.infogainScale(item.infogain)})
					.attr("class", "rank-indicator");
					
					var a = $("<a>")
							.attr("tabindex", "-1")
							.attr("class", "ui-corner-all")
							.html(item.label)
							.append(rankIndicator);
					
					return $( "<li>" )
					.attr("role", "presentation")
					.attr("class", "ui-menu-item")
					.append(a)
					.appendTo( ul );
				}
			},
			source : availableTags,
			minLength: 0,
			open: function(event){
				var scrollTop = $(event.target).offset().top-400;
				$("html, body").animate({scrollTop:scrollTop}, '500');
			},
			close: function(){
				$(this).val("");
			},
			minLength: 0,
			focus: function( event, ui ) {
				focueElement = $(event.currentTarget);//Adding PopUp to .ui-auocomplete
				if($("#SpeechBubble")){
					$("#SpeechBubble").remove();
				}
				focueElement.append("<div id='SpeechBubble'></div>")
					var html = cfsummary({
						long_name : ui.item.long_name,
						description : ui.item.description
					});
					var dropdown = $(thisUi.cf_query).data('ui-autocomplete').bindings[1];
					var offset = $(dropdown).offset();
					var uiwidth = $(dropdown).width();
					var width = 0.9 * (offset.left);
					var left = 0;
					if(window.innerWidth - (offset.left+uiwidth) > offset.left ){
						left = offset.left+uiwidth+10;
						width = 0.9 * (window.innerWidth - (offset.left+uiwidth));
					}
					$("#SpeechBubble").css({
						"top": "10%",
						"left": left,
						"height": "50%",
						"width": width,
						"display": "block"
					});
					$("#SpeechBubble").html(html);
					$("#SpeechBubble .summary_header").css({
						"width": (0.9*width)
					});
					$("#SpeechBubble .summary_content").css({
						"margin-top": $("#SpeechBubble .summary_header").height()+10
					});
			},
			search: function( event, ui ) {
				$("#SpeechBubble").remove();
			},
			select : function(event, ui) {
				if(ui.item.long_name != undefined){//To ensure "no gene name has been selected" is not accepted.
					$("#SpeechBubble").remove();
					thisView.geneColl.add([{
							unique_id: ui.item.unique_id,
							short_name: ui.item.short_name.replace(/_/g," ").toUpperCase(),
							long_name: ui.item.long_name
						}]);
//					else {
//						var newNode = new Node({
//							'name' : ui.item.short_name.replace(/_/g," "),
//							"options" : {
//								"unique_id" : ui.item.unique_id,					if(!Cure.initTour.ended()){
//								"kind" : "split_node",
//								"full_name" : ui.item.long_name,
//								"description" : ui.item.description,
//							}
//						});
//					}
				}
			},
		}).bind('focus', function(){ $(this).autocomplete("search"); } );
	},
	onRender: function(){
		this.geneCollRegion.show(new GeneCollView({collection: this.geneColl}));
	},
	onShow: function(){
		this.showCustomFeatures();
		this.showCf();
		var thisUi = this.ui;
		var thisView = this;
		$(this.ui.gene_query).genequery_autocomplete({
			open: function(event){
				var scrollTop = $(event.target).offset().top-400;
				$("html, body").animate({scrollTop:scrollTop}, '500');
			},
			minLength: 0,
			focus: function( event, ui ) {
				focueElement = $(event.currentTarget);//Adding PopUp to .ui-auocomplete
				if($("#SpeechBubble")){
					$("#SpeechBubble").remove();
				}
				focueElement.append("<div id='SpeechBubble'></div>")
				$.getJSON("http://mygene.info/v2/gene/"+ui.item.entrezgene+"?callback=?",function(data){
					var summary = {
							summaryText: data.summary,
							goTerms: data.go,
							generif: data.generif,
							name: data.name
					};
					var html = geneinfosummary({
						symbol : data.symbol,
						summary : summary
					}, {
						variable : 'args'
					});
					var dropdown = $(thisUi.gene_query).data('my-genequery_autocomplete').bindings[0];
					var offset = $(dropdown).offset();
					var uiwidth = $(dropdown).width();
					var width = 0.9 * (offset.left);
					var left = 0;
					if(window.innerWidth - (offset.left+uiwidth) > offset.left ){
						left = offset.left+uiwidth+10;
						width = 0.9 * (window.innerWidth - (offset.left+uiwidth));
					}
					$("#SpeechBubble").css({
						"top": "10%",
						"left": left,
						"height": "50%",
						"width": width,
						"display": "block"
					});
					$("#SpeechBubble").html(html);
					$("#SpeechBubble .summary_header").css({
						"width": (0.9*width)
					});
					$("#SpeechBubble .summary_content").css({
						"margin-top": $("#SpeechBubble .summary_header").height()+10
					});
				});
			},
			search: function( event, ui ) {
				$("#SpeechBubble").remove();
			},
			select : function(event, ui) {
				if(ui.item.name != undefined){//To ensure "no gene name has been selected" is not accepted.
					$("#SpeechBubble").remove();
					thisView.geneColl.add([{
						unique_id: ui.item.entrezgene,
						short_name: ui.item.symbol.toUpperCase(),
						long_name: ui.item.name
					}]);
//						var newNode = new Node({
//							'name' : ui.item.symbol,
//							"options" : {
//								"unique_id" : ui.item.entrezgene,
//								"kind" : "split_node",
//								"full_name" : ui.item.name
//							}
//						})
				}
			}
		});
		$(this.ui.gene_query).focus();
	}
});


return FeatureBuilderView;
});
