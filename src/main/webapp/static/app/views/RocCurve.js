define([
  //Libraries
  	'd3',
	'jquery',
	'marionette',
	'backbone',
	//Templates
	'text!static/app/templates/RocCurve.html',
	'text!static/app/templates/RocPointDetails.html'
    ], function(d3, $, Marionette, Backbone, RocCurveTemplate, RocDetailsTmpl) {
RocCurve = Marionette.ItemView.extend({
	initialize : function() {
		_.bindAll(this, 'drawChart', 'drawAxis');
		this.listenTo(this.model, 'change:auc', this.drawChart);
		var thisView = this;
		$(document).mouseup(function(e){
			var classToclose = $(".roc-point-details");
			if (!classToclose.is(e.target)	&& classToclose.has(e.target).length == 0) 
			{
				classToclose.hide();
			}
		});
	},
	ui:{
		rocChart: "#roc-curve",
		toggleCurve: ".toggleCurve",
		rocClass: ".roc-class",
		rocPointDetails: '.roc-point-details'
	},
	events: {
		'click .toggleCurve': 'toggleCurve',
		'change .roc-class': 'changeIndex'
	},
	toggleCurve: function(){
		if($(this.ui.toggleCurve).hasClass("showCurve")){
			$(this.ui.rocChart).show();
			$(this.ui.toggleCurve).removeClass("showCurve");
			$(this.ui.toggleCurve).addClass("hideCurve");
			$(this.ui.toggleCurve).html("Hide ROC Curve");
		} else if($(this.ui.toggleCurve).hasClass("hideCurve")){
			$(this.ui.rocChart).hide();
			$(this.ui.toggleCurve).removeClass("hideCurve");
			$(this.ui.toggleCurve).addClass("showCurve");
			$(this.ui.toggleCurve).html("Show ROC Curve");
		}
	},
	changeIndex: function(){
		this.model.set('auc_max_index', $(this.ui.rocClass).val());
		this.drawChart();
	},
	points : [],
	rocPoints: [],
	template: RocCurveTemplate,
	width: 270,
	height: 200,
	index: 0,
	_x: 50,
	_y: 50,
	drawChart: function(){
		var detailsEl = $(this.ui.rocPointDetails);
		this.index = this.model.get('auc_max_index');
		$(this.ui.rocClass).val(this.index);
		var color = (classValues[this.index] == Cure.posNodeName) ? "blue" : "red";
		$(this.ui.rocClass).css({"color":color});
		var data = this.model.get('auc_data_points'),
			w = parseFloat(this.width),
			h = parseFloat(this.height),
			SVGParent = d3.select(".roc-line-wrapper");
		this.points = [];
		this.rocPoints = [];
		if(data.length>0){
			data = this.model.get('auc_data_points')[1-this.index];
			data.reverse();
			this.rocPoints = data;
			for(var i = 0; i<data.length-1;i++){				
				this.points.push([{x: data[i]["False Positive Rate"], y: data[i]["True Positive Rate"]}, {x: data[i+1]["False Positive Rate"], y: data[i+1]["True Positive Rate"]}]);
			}
		}
		console.log(this.rocPoints);
		w-=10;
		var _x= parseFloat(this._x),
			_y= parseFloat(this._y),
			xScale = d3.scale.linear().domain([0, 1]).range([0, parseFloat(w-_x)]),
			yScale = d3.scale.linear().domain([0, 1]).range([parseFloat(h-_y), 0]);
		
		 var SVG = d3.select(".roc-lines-wrapper");
		 
		 var dP = SVG.selectAll(".roc-line").data(this.points);
		 
		 var dpEnter = dP.enter().append("svg:line")
		 	.attr("class","roc-line").attr("x1",function(d){
				return xScale(d[0].x);
			}).attr("y1",function(d){
				return yScale(d[0].y);
			}).attr("x2",function(d){
				return xScale(d[0].x);
			}).attr("y2",function(d){
				return yScale(d[0].y);
			}).transition().duration(500).attr("x1",function(d){
				return xScale(d[0].x);
			}).attr("y1",function(d){
				return yScale(d[0].y);
			}).attr("x2",function(d){
				return xScale(d[1].x);
			}).attr("y2",function(d){
				return yScale(d[1].y);
			}).attr("stroke","#000");
		 
		 dP.transition().duration(500).attr("x1",function(d){
				return xScale(d[0].x);
			}).attr("y1",function(d){
				return yScale(d[0].y);
			}).attr("x2",function(d){
				return xScale(d[1].x);
			}).attr("y2",function(d){
				return yScale(d[1].y);
			});
		 
		 dP.exit().transition().duration(500).attr("x1",function(d){
				return xScale(d[1].x);
			}).attr("y1",function(d){
				return yScale(d[1].y);
			}).attr("x2",function(d){
				return xScale(d[1].x);
			}).attr("y2",function(d){
				return yScale(d[1].y);
			}).remove();
		 
		 var P = SVG.selectAll(".roc-point").data(this.rocPoints);
		 
		 P.enter()
		  .append("svg:circle")
		  .attr("class","roc-point")
		  .style("cursor","pointer")
		  .attr("r",0)
		  .attr("fill","white")
		  .attr("stroke","steelblue")
		  .attr("stroke-width",2)
		  .attr("cx",function(d){
			  return xScale(d["False Positive Rate"]);
		  }).attr("cy",function(d){
			  return yScale(d["True Positive Rate"]);
		  }).on("click", function(d){
			  detailsEl.show();
			  detailsEl.html(RocDetailsTmpl({d:d}));
		  });
		 
		 P.transition().duration(500)
		  .attr("r",5)
		  .attr("cx",function(d){
			  return xScale(d["False Positive Rate"]);
		  }).attr("cy",function(d){
			  return yScale(d["True Positive Rate"]);
		  });
		 
		 P.exit().transition().duration(500)
		  .attr("r",0)
		  .remove();
		 
	},
	drawAxis: function(){
		console.log(this.ui.rocChart);
		var data = this.model.get('auc_data_points'),
		w = parseFloat(this.width),
		h = parseFloat(this.height),
		SVGParent = d3.select(this.ui.rocChart[0]).attr("width", w).attr("height", h).append("svg:g").attr("class","roc-line-wrapper").attr("transform","translate(0,10)");
	w-=10;
	var _x= parseFloat(this._x),
		_y= parseFloat(this._y),
		xScale = d3.scale.linear().domain([0, 1]).range([0, parseFloat(w-_x)]),
		yScale = d3.scale.linear().domain([0, 1]).range([parseFloat(h-_y), 0]),
		xAxis = d3.svg.axis().scale(xScale).orient("bottom"),
		yAxis = d3.svg.axis().scale(yScale).orient("left");
	
	this.xaxis = SVGParent.append("svg:g").attr("transform","translate("+_x+", "+parseFloat(h-_y)+")")
										 .attr("class", "xaxis axis").call(xAxis);
	this.yaxis = SVGParent.append("svg:g").attr("transform","translate("+_x+",0)")
	 .attr("class", "yaxis axis").call(yAxis);
	
	SVGParent.append("svg:text").text("False Positive Rate").attr("transform","translate("+_x+","+parseFloat(h-_y+30)+")");
	SVGParent.append("svg:text").text("True Positive Rate").attr("transform","translate(20,"+parseFloat(h-_y)+") rotate(-90)");
	
	SVGParent.append("svg:line")
		.attr("class","middle-line")
		.attr("transform","translate("+_x+",0)").attr("x1",function(d){
			return xScale(0);
		}).attr("y1",function(d){
			return yScale(0);
		}).attr("x2",function(d){
			return xScale(1);
		}).attr("y2",function(d){
			return yScale(1);
		}).attr("stroke","#ddd");
	
	var SVG = SVGParent.append("svg:g").attr("class","roc-lines-wrapper");
	 SVG.attr("transform", "translate("+_x+",0)");
	},
	onShow: function(){
		this.index = this.model.get('auc_max_index');
		$(this.ui.rocClass).val(this.index);
		this.drawAxis();
		this.drawChart();
		if(window.innerHeight > Cure.sidebarHeight){
			$(this.ui.toggleCurve).trigger('click');
		}
	}
});

return RocCurve;
});
