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
		rocChart: "#roc-curve"
	},
	points : [],
	template: RocCurveTemplate,
	drawChart: function(){
		var data = this.model.get('auc_data_points'),
			w = 300,
			h = 300,
			SVGParent = d3.select(".roc-line-wrapper");
		var t=[0 ,0];//X - FPR, Y - TPR
		for(var temp in data){
			if(data[temp]){
				t[1]++;
				continue;
			} 
			t[0]++;
		}
		var dX = 1/t[0], dY = 1/t[1], pX = 0 , pY = 0;
		for(var temp in data){
			this.points[temp] = [{x: pY, y:pX}, {}];
			if(data[temp]){
				pY+=dY;
			} else {
				pX += dX;
			}
			this.points[temp][1] = {x: pY, y:pX};
		}
		this.points = this.points.slice(0,data.length);
		w-=10;
		var _x= 30,
			_y= 30,
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
		w = 300,
		h = 300,
		SVGParent = d3.select(this.ui.rocChart[0]).attr("width", w).attr("height", h).append("svg:g").attr("class","roc-line-wrapper").attr("transform","translate(0,10)");
	w-=10;
	var _x= 30,
		_y= 30,
		xScale = d3.scale.linear().domain([0, 1]).range([0, parseFloat(w-_x)]),
		yScale = d3.scale.linear().domain([0, 1]).range([parseFloat(h-_y), 0]),
		xAxis = d3.svg.axis().scale(xScale).orient("bottom"),
		yAxis = d3.svg.axis().scale(yScale).orient("left");
	
	this.xaxis = SVGParent.append("svg:g").attr("transform","translate("+_x+", "+parseFloat(h-_y)+")")
										 .attr("class", "xaxis axis").call(xAxis);
	this.yaxis = SVGParent.append("svg:g").attr("transform","translate("+_x+",0)")
	 .attr("class", "yaxis axis").call(yAxis);
	
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
	}
});

return RocCurve;
});
