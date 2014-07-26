define([
	'jquery',
	'marionette',
	'd3',
	//Templates
	'text!static/app/templates/PickInstances.html',
	'text!static/app/templates/GeneSummary.html',
	'text!static/app/templates/ClinicalFeatureSummary.html',
	'jqueryui'
    ], function($, Marionette, d3, pickInstTmpl, geneinfosummary, cfsummary) {
PickInstanceView = Marionette.ItemView.extend({
	template: pickInstTmpl,
	className: 'pick-instance-wrapper panel panel-default',
	ui: {
		gene_query: ".pick_gene_instances",
		cf_query: ".pick_cf_instances"
	},
	events: {
		'click #get-instances': 'getInstances',
		'click .pick-instance-close': 'closeView'
	},
	closeView: function(e){
		e.preventDefault();
		this.remove();
	},
	height: 200,
	width: 300,
	drawChart: function(attr){
		this.height = this.$el.height() * 0.5;
		this.width = this.$el.width() * 0.9;
		d3.select("#instance-data-chart").select(".instance-data-chart-wrapper").remove();
		var max = [Number.MIN_VALUE, Number.MIN_VALUE],
			min = [Number.MAX_VALUE, Number.MAX_VALUE],
			h=this.height,
			w=this.width,
			mX = 40,
			mY = 20,
			SVG = d3.select("#instance-data-chart").attr({"height":h+60,"width":w+60}).append("svg:g").attr("class","instance-data-chart-wrapper");
		for(var temp in attr){
			for(var i =0; i<2;i++){
				if(max[i]<attr[temp][i]){
					max[i] = attr[temp][i];
				}
				if(min[i]>attr[temp][i]){
					min[i] = attr[temp][i];
				}
			}
		}		
		
		var vertices = [];
		var line;
		var indicatorCircle;
		var SVGParent = d3.select("#instance-data-chart").on("click", startLine);
		
		function startLine() {
			if($("#draw-polygon").is(":checked")){
				if(vertices.length==0){
					SVGParent.selectAll(".polygon-line").remove();
				}
				SVGParent.on("mousemove", mousemove);
			    var m = d3.mouse(this);
			    if(vertices.length>0){
			    	if(Math.sqrt(Math.pow(vertices[0][0]-m[0],2)+Math.pow(vertices[0][1]-m[1],2))<=5){
			    		line.attr("x2",vertices[0][0]).attr("y2",vertices[0][1]);
				    	SVGParent.on("mousemove", null);
				    	vertices = [];
				    	indicatorCircle.remove();
				    } else {
				    	line = SVGParent.append("line")
				    	.attr("class","polygon-line")
				        .attr("x1", m[0])
				        .attr("y1", m[1])
				        .attr("x2", m[0])
				        .attr("y2", m[1]);
				    vertices.push(m);
				    }
			    } else {
				    line = SVGParent.append("line")
			    	.attr("class","polygon-line")
			        .attr("x1", m[0])
			        .attr("y1", m[1])
			        .attr("x2", m[0])
			        .attr("y2", m[1]);
			    vertices.push(m);
			    }
			} else {
				SVGParent.on("mousemove", null);
			}
		}

		function mousemove() {
		    var m = d3.mouse(this);
		    if(Math.sqrt(Math.pow(vertices[0][0]-m[0],2)+Math.pow(vertices[0][1]-m[1],2))<=5 && vertices.length>1){
		    	if(!d3.select(".indicator-circle").empty()){
		    		indicatorCircle.attr("cx",m[0]).attr("cy",m[1]);
		    	} else {
			    	indicatorCircle = SVGParent.append("svg:circle").attr("class","indicator-circle")
			    	.attr("r",5)
			    	.attr("cx",m[0])
			    	.attr("cy",m[1]);
		    	}
		    	m=vertices[0];
		    } else if(!d3.select(".indicator-circle").empty()) {
		    	indicatorCircle.remove();
		    }
		    line.attr("x2", m[0])
	        .attr("y2", m[1]);
		}
		
		var arc = d3.svg.symbol().type('circle').size(10);
		var attrScale1 = d3.scale.linear().domain([min[0]-5,max[0]+5]).range([h,0]);
		var revattrScale1 = d3.scale.linear().domain([h,0]).range([min[0],max[0]]);
		var yAxis = d3.svg.axis().tickFormat(function(d) { return Math.round(d*100)/100;}).scale(attrScale1).orient("left");
		SVG.append("g").attr("class","axis yaxis").attr("transform", "translate("+mX+","+mY+")").call(yAxis)
		.append("svg:text").text("Attribute 1").attr("transform","translate(-25,"+parseInt(h+mY)+")rotate(-90)").style("fill","#808080");
	
		var attrScale2 = d3.scale.linear().domain([min[1]-5,max[1]+5]).range([0,w]);
		var revattrScale2 = d3.scale.linear().domain([0,w]).range([min[1],max[1]]);
		var xAxis = d3.svg.axis().tickFormat(function(d) { return Math.round(d*100)/100;}).scale(attrScale2).orient("bottom");
		SVG.append("g").attr("class","axis xaxis").attr("transform", "translate("+mX+","+parseInt(h+mY)+")").call(xAxis)
		.append("svg:text").attr("transform","translate(0,30)").text("Attribute 2").style("fill","#808080");
		
		SVG.append("svg:g").attr("class","instance-points");
		var layer = SVG.selectAll(".data-point").data(attr);
		
		var layerEnter = layer.enter().append("g").attr("class","data-point")
		.attr("transform",function(d){
			return "translate("+parseFloat(mX+attrScale2(d[1]))+","+parseFloat(mY+attrScale1(d[0]))+")";
		});

		layerEnter.append('path')
		.attr('d',arc)
		.attr('fill',function(d){return (d[2]==1) ? "blue" : "red";});
		
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
					$(this).parent().find(".pick-attribute-uniqueid-gene").val(ui.item.entrezgene);
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
						$(this).parent().find(".pick-attribute-uniqueid").val(ui.item.unique_id);
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
