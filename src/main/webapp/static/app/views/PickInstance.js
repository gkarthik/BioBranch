define([
	'jquery',
	'marionette',
	//Templates
	'text!static/app/templates/PickInstances.html',
	'text!static/app/templates/GeneSummary.html',
	'text!static/app/templates/ClinicalFeatureSummary.html',
	'jqueryui'
    ], function($, Marionette, pickInstTmpl, geneinfosummary, cfsummary) {
PickInstanceView = Marionette.ItemView.extend({
	template: pickInstTmpl,
	className: 'pick-instance-wrapper',
	ui: {
		gene_query: "#pick_gene_instances",
		cf_query: "#pick_cf_instances"
	},
	events: {
		'click #get-instances': 'getInstances'
	},
	getInstances: function(){
		if(this.model){
			this.model.set('pickInst',true);
		} 
		var args = {};
		var attrs = [];
		var splits = [];
		$(".pick-attribute-uniqueid").each(function(){
			attrs.push($(this).val());
		});
		$(".pick-split").each(function(){
			splits.push($(this).val());
		});
		args.pickedAttrs = attrs;
		args.splits = splits;
		console.log(args);
		Cure.PlayerNodeCollection.sync(args);
	},
	onShow: function(){
		var thisUi = this.ui;
    	this.showCf();
    	$(this.ui.gene_query).genequery_autocomplete({
			open: function(event){
				var scrollTop = $(event.target).offset().top-400;
				$("html, body").animate({scrollTop:scrollTop}, '500');
			},
			minLength: 1,
			focus: function( event, ui ) {
				focueElement = $(event.currentTarget);//Adding PopUp to .ui-auocomplete
				if($("#SpeechBubble")){
					$("#SpeechBubble").remove();
				}
				focueElement.append("<div id='SpeechBubble'></div>")
				$.getJSON("http://mygene.info/v2/gene/"+ui.item.id+"?callback=?",function(data){
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
					$("#pick-attribute-uniqueid-gene").val(ui.item.entrezgene);
					$(this).val("");
				}
			}
		});
		$(this.ui.gene_query).focus();
	},
	showCf: function(){
		var thisUi = this.ui;
		var thisCollection = this.newGeneCollection;
		
		//Clinical Features Autocomplete
		var availableTags = Cure.ClinicalFeatureCollection.toJSON();
		
		$(this.ui.cf_query).autocomplete({
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
				console.log(ui.item);
				if(ui.item.short_name != undefined){//To ensure "no gene name has been selected" is not accepted.
						$("#SpeechBubble").remove();
						$("#pick-attribute-uniqueid-cf").val(ui.item.unique_id);
					}
			},
		}).bind('focus', function(){ $(this).autocomplete("search"); } )
			.data("ui-autocomplete")._renderItem = function (ul, item) {
		    return $("<li></li>")
	        .data("item.autocomplete", item)
	        .append("<a>" + item.label + "</a>")
	        .appendTo(ul);
	    };
	}
});

return PickInstanceView;
});
