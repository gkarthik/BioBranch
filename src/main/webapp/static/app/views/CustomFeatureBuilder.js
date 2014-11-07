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
	'app/views/AddNodeCustomFeatureBuilderView',
	//Templates
	'text!static/app/templates/GeneSummary.html',
	'text!static/app/templates/ClinicalFeatureSummary.html',
	//Plugins
	'myGeneAutocomplete',
	'jqueryui'
    ], function($, Marionette, FeatureBuilderTmpl, GeneCollView, GeneCollection, DistributionChartView, DistributionData, addNodeView, geneinfosummary, cfsummary) {
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
		message: ".msg-wrapper",
		chooseLabel: '.choose-comp-label'
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
		'click .ref-remove': 'removeRef',
		'click .new-custom-feature': 'resetCf'
	},
	regions: {
		geneCollRegion: '.gene-coll-region',
		cFeatureDistribution: '#cfeature-class-distribution-wrapper',
		selectFeatureRegion: '#select-feature-region'
	},
	resetCf: function(){
		this.render();
	},
	closeView: function(){
		this.remove();
	},
	chooseRef: function(){
		$(this.ui.refWrapper).toggle();
		this.setReference = (this.setReference) ? false: true;
		if(this.setReference){
			$(this.ui.chooseLabel).html("Choose Reference");
			$(this.ui.chooseRef).html("Save Reference");
		} else {
			$(this.ui.chooseLabel).html("Choose Components");
			$(this.ui.chooseRef).html("Choose Reference");
		}
	},
	removeRef: function(){
		this.setReference = false;
		$(this.ui.refDetails).html("");
		$(this.ui.refDetails).data('ref_id', null);
		$(this.ui.chooseLabel).html("Choose Components");
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
		var split = $(this.ui.equation).val().match(/([A-Za-z0-9_-])+/g);
		for(var t in split){
			split[t] = split[t].trim();
			if(!this.geneColl.findWhere({"short_name":split[t].toUpperCase()}) && isNaN(parseInt(split[t]))){
				Cure.utils.showAlert("Equation not valid", false);
				return false;
			}
		}
		return true;
	},
	highlightFeatures: function(){
		var split = $(this.ui.equation).val().match(/([A-Za-z0-9_-])+/g);
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
	template: FeatureBuilderTmpl,
	onRender: function(){
		this.geneCollRegion.show(new GeneCollView({collection: this.geneColl, options: {showLimits:true}}));
		this.selectFeatureRegion.show(new addNodeView({view: "cfbuilderview"}));
	}
});


return FeatureBuilderView;
});
