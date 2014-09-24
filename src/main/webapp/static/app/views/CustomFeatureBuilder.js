define([
  //Libraries
	'jquery',
	'marionette',
	//Templates
	'text!static/app/templates/CustomFeatureBuilder.html',
	//View
	'app/views/GeneListCollectionView',
	'app/collections/GeneCollection',
	'app/views/distributionChartView',
	'app/models/DistributionData',
	//Templates
	'text!static/app/templates/GeneSummary.html',
	'text!static/app/templates/ClinicalFeatureSummary.html',
	//Plugins
	'myGeneAutocomplete',
	'jqueryui'
    ], function($, Marionette, FeatureBuilderTmpl, GeneCollView, GeneCollection, DistributionChartView, DistributionData, geneinfosummary, cfsummary) {
FeatureBuilderView = Marionette.Layout.extend({
	className: 'panel panel-default',
	ui: {
		gene_query: '.gene_query',
		customfeature_query: '.customfeature_query',
		cf_query: '.cf_query',
		equation: '#feature-builder-equation',
		testWidth: '#test-span-width',
		equationWrapper: '#feature-builder-equation-wrapper',
		name: "#cf-name",
		description: "#cf-description",
		refWrapper: '.ref-feature-wrapper',
		refDetails: '.ref-feature',
		setRef: ".set-ref-feature",
		chooseRef: '.ref-choose',
		message: ".msg-wrapper"
	},
	events: {
		'click .choose-gene': 'openGene',
		'click .choose-cf': 'openCf',
		'click .choose-customfeature': 'openCustomFeature',
		'keyup #feature-builder-equation': 'highlightFeatures',
		'click .preview-custom-feature': 'previewCustomFeature',
		'click .add-custom-feature': 'addCustomFeature',
		'click .close': 'closeView',
		'click .ref-choose': 'chooseRef',
		'click .ref-remove': 'removeRef'
	},
	regions: {
		geneCollRegion: '.gene-coll-region',
		cFeatureDistribution: '#cfeature-class-distribution-wrapper'
	},
	closeView: function(){
		this.remove();
	},
	chooseRef: function(){
		$(this.ui.refWrapper).toggle();
		this.setReference = (this.setReference) ? false: true;
		if(this.setReference){
			$(this.ui.chooseRef).html("Save Reference");
		} else {
			$(this.ui.chooseRef).html("Choose Reference");
		}
	},
	removeRef: function(){
		this.setReference = false;
		$(this.ui.refDetails).html("");
		$(this.ui.refDetails).data('ref_id', undefined);
		$(this.ui.chooseRef).html("Choose Reference");
		$(this.ui.refWrapper).toggle();
	},
	setReference: false,
	url: base_url+'MetaServer',
	initialize: function(){
		this.geneColl = new GeneCollection();
	},
	addCustomFeature: function(){
		var exp = $(this.ui.equation).val().toUpperCase();
		var components = [];
		var reg;
		var thisView = this;
		var geneArr = this.geneColl.toJSON();
		geneArr.sort(function(a,b){
			return b.short_name.length > a.short_name.length;
		});
		
		var lLimit;
		var uLimit;
		for(var temp in geneArr){
			lLimit = null;
			uLimit = null;
			if(geneArr[temp].unique_id!="Unique ID"){
				reg = new RegExp(geneArr[temp].short_name, 'g');
				exp = exp.replace(reg,"@"+geneArr[temp].unique_id);
				if(geneArr[temp].setUpperLimit){
					uLimit = geneArr[temp].uLimit
				}
				if(geneArr[temp].setLowerLimit){
					lLimit = geneArr[temp].lLimit
				}
				components.push({
					id: geneArr[temp].unique_id,
					uLimit: uLimit,
					lLimit: lLimit
				});
			}
		}
		var args = {
    	        command : "custom_feature_create",
    	        expression: exp,
    	        components: components,
    	        description: $(this.ui.description).val(),
    	        user_id: Cure.Player.get('id'),
    	        name: $(this.ui.name).val(),
    	        dataset: Cure.dataset.get('id'),
    	        ref_id: $(this.ui.refDetails).data('ref_id') || null 
    	      };
		console.log(args);
		if(this.validateExpression($(this.ui.equation).val())){
			Cure.utils.showLoading(null);
			 $.ajax({
   	          type : 'POST',
   	          url : this.url,
   	          data : JSON.stringify(args),
   	          dataType : 'json',
   	          contentType : "application/json; charset=utf-8",
   	          success : function(data){
   	        	  Cure.utils.hideLoading();
   	        	  var msg = "";
   	        	  if(data.success){
   	        		if(!data.exists){
     	        		  msg = "Success: Custom feature has been saved.";
     	        	  } else {
     	        		msg = data.message;
     	        	  }
   	        	  } else {
   	        		  msg = "Failure: Saving could not be completed. Please try again in a while.";
   	        	  }
   	        	  $(thisView.ui.message).html(msg);
   	        	  console.log(data);
   	        },
   	        error: this.error
   	      });
		}
	},
	previewCustomFeature: function(){
		var exp = $(this.ui.equation).val().toUpperCase();
		var components = [];
		var reg;
		var thieView = this;
		var geneArr = this.geneColl.toJSON();
		geneArr.sort(function(a,b){
			return b.short_name.length > a.short_name.length;
		});
		
		var uLimit = null;
		var lLimit = null;
		
		for(var temp in geneArr){
			lLimit = null;
			uLimit = null;
			if(geneArr[temp].unique_id!="Unique ID"){
				reg = new RegExp(geneArr[temp].short_name, 'g');
				exp = exp.replace(reg,"@"+geneArr[temp].unique_id);
				if(geneArr[temp].setUpperLimit){
					uLimit = geneArr[temp].uLimit
				}
				if(geneArr[temp].setLowerLimit){
					lLimit = geneArr[temp].lLimit
				}
				components.push({
					id: geneArr[temp].unique_id,
					uLimit: uLimit,
					lLimit: lLimit
				});
			}
		}
		
		var args = {
    	        command : "custom_feature_preview",
    	        expression: exp,
    	        components: components,
    	        name: $(this.ui.name).val(),
    	        dataset: Cure.dataset.get('id'),
    	        ref_id: $(this.ui.refDetails).data('ref_id') || null
    	      };
		console.log(args);
		console.log(this.validateExpression($(this.ui.equation).val()));
		if(this.validateExpression($(this.ui.equation).val())){
			Cure.utils.showLoading(null);
			$.ajax({
  	          type : 'POST',
  	          url : this.url,
  	          data : JSON.stringify(args),
  	          dataType : 'json',
  	          contentType : "application/json; charset=utf-8",
  	          success : function(data){
  	        	  Cure.utils.hideLoading();
  	          	console.log(data);
  	          	var newModel = new DistributionData(data);
  	          	console.log(newModel);
  	          	thieView.cFeatureDistribution.show(new DistributionChartView({model: newModel}));
  	        },
  	        error: this.error
  	      });
		}
	},
	error: function(){
		Cure.utils
	    .showAlert("<strong>Server Error</strong><br>Please try saving again in a while.", 0);
	},
	validateExpression: function(exp){
		if($(this.ui.equation).val()==""){
			Cure.utils.showAlert("Equation is empty", false);
			return false;
		}
		if($(this.ui.description).val()==""){
			Cure.utils.showAlert("Description is empty", false);
			return false;
		}
		if($(this.ui.name).val()==""){
			Cure.utils.showAlert("Name is empty", false);
			return false;
		}
		var split = $(this.ui.equation).val().match(/([A-Za-z0-9 ])+/g);
		for(var t in split){
			if(!this.geneColl.findWhere({"short_name":split[t].toUpperCase()}) && isNaN(parseInt(split[t]))){
				Cure.utils.showAlert("Equation not valid", false);
				return false;
			}
		}
		return true;
	},
	highlightFeatures: function(){
		var split = $(this.ui.equation).val().match(/([A-Za-z0-9 ])+/g);
		var termstring = $(this.ui.equation).val();
		var index = 0;
		var mp = {};
		var buffer = 0;
		var el;
		var indices;
		var splits;
		$(".feature-tag").remove();
		for(var i =0;i<split.length;i++){
			split[i] = split[i].trim();
		}
		for(var i=0;i<split.length;i++){
			for(var j =0; j< split.length;j++){
				if(i!=j && split[i] == split[j]){
					split.splice(i,1);
					i--;
					j--;
				}
			}
		}
		for(var temp in split){
			if(this.geneColl.findWhere({"short_name":split[temp].toUpperCase()})){
				indices = [];
				termstring = $(this.ui.equation).val();
				console.log(split[temp].toLowerCase());
				indices = this.getAllIndices(split[temp], termstring);
				console.log(indices);
				for(var i in indices){
					if(termstring.substring(indices[i], indices[i] + split[temp].length).indexOf(" ")==-1){
						splits = termstring.substring(indices[i], indices[i] + split[temp].length);
						mp = this.getWidthInSpan(termstring.substring(0, indices[i]));
						console.log(mp);
						el = $("<span>").html(splits).attr("class","feature-tag").css({'margin-left':mp.w+"px",'margin-top':mp.h+"px"});
						$(this.ui.equationWrapper).prepend(el);
					} else {
						buffer = 0;
						splits = termstring.substring(indices[i], indices[i] + split[temp].length).split(" ");
						for(var t in splits){
							mp = this.getWidthInSpan(termstring.substring(0, buffer + indices[i]));
							el = $("<span>").html(splits[t]).attr("class","feature-tag").css({'margin-left':mp.w+"px",'margin-top':mp.h+"px"});
							$(this.ui.equationWrapper).prepend(el);
							buffer+=splits[t].length+1;
						}
					}
				}
			}
		}
	},
	getAllIndices: function(searchStr, str) {
	    var startIndex = 0, searchStrLen = searchStr.length;
	    var index, indices = [];
	    str = str.toLowerCase();
	    searchStr = searchStr.toLowerCase();
	    while ((index = str.indexOf(searchStr, startIndex)) > -1) {
	        indices.push(index);
	        startIndex = index + searchStrLen;
	    }
	    return indices;
	},
	getWidthInSpan: function(tString){
		$(this.ui.testWidth).html(tString);
		wSpan = $(this.ui.testWidth).outerWidth() % $(this.ui.equation).width();
		hSpan = $(".feature-tag").height() * parseInt($(this.ui.testWidth).outerWidth()/$(this.ui.equation).width());
		return {w: wSpan, h:hSpan};
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
	    	        query: request.term,
	    	        dataset: Cure.dataset.get('id')
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
	  					if(ui.item.label != undefined && !thisView.setReference){//To ensure "no gene name has been selected" is not accepted.
	  						if(!Cure.initTour.ended()){
	  							Cure.initTour.end();
	  						}
	  						$("#SpeechBubble").remove();
	  						thisView.geneColl.add([{
	  							unique_id: "custom_feature_"+ui.item.data.id,
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
	  					} else if (ui.item.label != undefined && thisView.setReference){
	  						$(thisView.ui.refDetails).html(ui.item.data.name.toUpperCase());
	  						$(thisView.ui.refDetails).data('ref_id', "custom_feature_"+ui.item.data.id);
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
				if(ui.item.long_name != undefined && !thisView.setReference){//To ensure "no gene name has been selected" is not accepted.
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
				}  else if (ui.item.label != undefined && thisView.setReference){
						$(thisView.ui.refDetails).html(ui.item.short_name.replace(/_/g," ").toUpperCase());
  						$(thisView.ui.refDetails).data('ref_id', ui.item.unique_id);
  					}
			},
		}).bind('focus', function(){ $(this).autocomplete("search"); } );
	},
	onRender: function(){
		this.geneCollRegion.show(new GeneCollView({collection: this.geneColl, options: {showLimits:true}}));
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
				if(ui.item.name != undefined && !thisView.setReference){//To ensure "no gene name has been selected" is not accepted.
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
				} else if(ui.item.name != undefined && thisView.setReference) {
					$(thisView.ui.refDetails).html(ui.item.symbol.toUpperCase());
						$(thisView.ui.refDetails).data('ref_id', ui.item.entrezgene);
				}
			}
		});
		$(this.ui.gene_query).focus();
	}
});


return FeatureBuilderView;
});
