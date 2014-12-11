define([
  //Libraries
  	'd3',
	'jquery',
	'marionette',
	'backbone',
	//Templates
	'text!static/app/templates/RocCurve.html'
    ], function(d3, $, Marionette, Backbone, RocCurveTemplate) {
RocCurve = Marionette.ItemView.extend({
	initialize : function() {
		_.bindAll(this, 'drawChart', 'drawAxis');
		this.listenTo(this.model, 'change:auc', this.drawChart);
	},
	ui:{
		rocChart: "#roc-curve",
		toggleCurve: ".toggleCurve"
	},
	events: {
		'click .toggleCurve': 'toggleCurve'
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
	points : [],
	template: RocCurveTemplate,
	width: 270,
	height: 200,
	_x: 50,
	_y: 50,
	drawChart: function(){
		var data = this.model.get('auc_data_points'),
			w = parseFloat(this.width),
			h = parseFloat(this.height),
			SVGParent = d3.select(".roc-line-wrapper");
		this.points = [];
		if(data.length>0){
			this.points.push([{x: 0, y:0}, {x: data[0][1], y:data[0][0]}]);
			for(var i = 0; i<data.length-1;i++){
				this.points.push([{x: data[i][1], y: data[i][0]}, {x: data[i+1][1], y: data[i+1][0]}]);
			}
			this.points.push([ {x: data[data.length-1][1], y:data[data.length-1][0]}, {x: 1, y:1}]);
		}
		console.log(this.points);
		w-=10;
		var _x= parseFloat(this._x),
			_y= parseFloat(this._y),
			xScale = d3.scale.linear().domain([0, 1]).range([0, parseFloat(w-_x)]),
			yScale = d3.scale.linear().domain([0, 1]).range([parseFloat(h-_y), 0]);
		
		 var SVG = d3.select(".roc-lines-wrapper");
		 
		 var dP = SVG.selectAll(".roc-line").data(this.points);
		 
		 var dpEnter = dP.enter().append("svg:line")
		 	.attr("class","roc-line").attr("x1",function(d){
				return xScale(0);
			}).attr("y1",function(d){
				return yScale(0);
			}).attr("x2",function(d){
				return xScale(1);
			}).attr("y2",function(d){
				return yScale(1);
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
				return xScale(0);
			}).attr("y1",function(d){
				return yScale(0);
			}).attr("x2",function(d){
				return xScale(1);
			}).attr("y2",function(d){
				return yScale(1);
			}).remove();
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
		this.drawAxis();
		this.drawChart();
		if(window.innerHeight > Cure.sidebarHeight){
			$(this.ui.toggleCurve).trigger('click');
		}
	}
});

return RocCurve;
});
