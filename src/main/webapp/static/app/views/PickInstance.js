define([
	'jquery',
	'marionette',
	'd3',
	'app/models/Node',
	//Templates
	'text!static/app/templates/PickInstances.html',
	'text!static/app/templates/GeneSummary.html',
	'text!static/app/templates/ClinicalFeatureSummary.html',
	'app/models/ConfusionMatrix',
	'app/views/ConfusionMatrixView',
	'app/views/AddNodePickInstanceView',
	'jqueryui'
    ], function($, Marionette, d3, Node, pickInstTmpl, geneinfosummary, cfsummary, cfMatrix, cfMatrixView, searchFeature) {
PickInstanceView = Marionette.Layout.extend({
	template: pickInstTmpl,
	url:base_url+'MetaServer',
	ui: {
		gene_query: ".pick_gene_instances",
		cf_query: ".pick_cf_instances",
		chartWrapper: '#instance-data-chart-wrapper',
		chartFunctions:".chart-functions",
		panel: '.pick-instance-wrapper',
		previewResults: '.preview-results',
		acc: '#preview-acc',
		auc: '#preview-auc'
	},
	events: {
		'click #get-instances': 'getInstances',
		'click .pick-instance-close': 'closeView',
		'click #build-selection': 'createCustomSet',
		'click #clear-selection': 'clearSelection',
		'click #preview-selection': 'previewSelection',
		'click #preview-close': 'closePreview'
	},
	regions: {
		cfMatrixRegion: "#preview-cfmatrix",
		pickXaxis: "#pick-xaxis",
		pickYaxis: "#pick-yaxis"
	},
	closeView: function(e){
		e.preventDefault();
		Cure.appLayout.pickInstanceRegion.close();
	},
	initialize: function(){
		_.bindAll(this,'getInstances', 'addNode', 'drawChart');
	},
	preview: false,
	attrs: [],
	attributeVertices: [],
	uniqueIds: [],
	closePreview: function(){
		this.preview = false;
	},
	previewSelection: function(){
		this.preview = true;
		$(this.ui.previewResults).show();
		this.createCustomSet();
	},
	createCustomSet: function(){
		var unique_ids = [];
		$(".pick-attribute-uniqueid").each(function(){
			unique_ids.push($(this).val());
		});
		var args = {
				command : "custom_set_create",
				constraints: this.attributeVertices,
				unique_ids: unique_ids,
				player_id : Cure.Player.get('id')
			};
		
		if(args.constraints.length>0){
			$.ajax({
				type : 'POST',
				url : this.url,
				data : JSON.stringify(args),
				dataType : 'json',
				contentType : "application/json; charset=utf-8",
				success : this.addNode,
				error : function(){
					console.log(error);
				}
			});
		}
	},
	clearSelection: function(){
		d3.select("#instance-data-chart").selectAll(".polygon-line").remove();
		d3.select("#instance-data-chart").selectAll(".data-polygon").remove();
    	this.attributeVertices = [];
	},
	addNode: function(data){
		var model = this.model;
		var thisView = this;
		if(!this.preview){
			var kind_value = "";
				try {
					kind_value = model.get("options").get('kind');
				} catch (exception) {
				}
				if (kind_value == "leaf_node") {
						if(model.get("options")){
							model.get("options").unset("split_point");
						}
						
						if(model.get("distribution_data")){
							model.get("distribution_data").set({
								"range": -1
							});
						}
					model.set("previousAttributes", model.toJSON());
					model.set("name", "Selection");
					model.set('accLimit', 0, {silent:true});
					model.set('pickInst', false);
					
					var index = Cure.CollaboratorCollection.pluck("id").indexOf(Cure.Player.get('id'));
					var newCollaborator;
					if(index!=-1){
						newCollaborator = Cure.CollaboratorCollection.at(index);
					} else {
						newCollaborator = new Collaborator({
							"name": cure_user_name,
							"id": Cure.Player.get('id'),
							"created" : new Date()
						});
						Cure.CollaboratorCollection.add(newCollaborator);
						index = Cure.CollaboratorCollection.indexOf(newCollaborator);
					}
					model.get("options").set({
						"unique_id" : "custom_set_"+data.id,
						"kind" : "split_node",
						"full_name" : '',
						"description" : data.constraints
					});
				} else {
					new Node({
						'name' : "Selection",
						"options" : {
							"unique_id" : "custom_set_"+data.id,
							"kind" : "split_node",
							"full_name" : '',
							"description" : data.constraints
						},
						pickInst: false
					});
				}	
				Cure.PlayerNodeCollection.sync();
				Cure.appLayout.pickInstanceRegion.close();
			} else {
				var json = (Cure.PlayerNodeCollection.length>0) ? Cure.PlayerNodeCollection.at(0).toJSON() : {};
				var node = {};
				if(Cure.PlayerNodeCollection.length==0){
					json = Cure.NodeDefaults;
					json.options = {};
					json.options.unique_id = "custom_set_"+data.id;
					json.options.kind="split_node";
				} else {
					thisView.changeUIDinJSON(json, model.get('options').get('cid'), data.id);
				}
				Cure.utils.showLoading(null);
				var testOptions = {
						value: $("input[name='testOptions']:checked").val(),
						percentSplit:  $("input[name='percent-split']").val()
				};
				var args = {
						command : "scoretree",
						dataset : Cure.dataset.get('id'),
						treestruct : json,
						comment: Cure.Comment.get("content"),
						player_id : Cure.Player.get('id'),
						previous_tree_id: Cure.PlayerNodeCollection.prevTreeId,
						testOptions: testOptions
					};
				
				//POST request to server.		
				$.ajax({
					type : 'POST',
					url : this.url,
					data : JSON.stringify(args),
					dataType : 'json',
					contentType : "application/json; charset=utf-8",
					success : function(data){
						Cure.utils.hideLoading();
						thisView.preview = false;
						thisView.cfMatrix.setupMatrix(data.confusion_matrix);
						$(thisView.ui.acc).html(Math.floor(data.pct_correct*100)/100);
						$(thisView.ui.auc).html(Math.floor(data.auc*100)/100);
					},
					error : this.error
				});
			}
	},
	changeUIDinJSON: function(node, cid, id){
		if(node.options.cid == cid){
			node.options.unique_id = "custom_set_"+id;
			node.options.kind="split_node";
		} else {
			for(var temp in node.children){
				this.changeUIDinJSON(node.children[temp], cid, id);
			}
		}
		
	},
	height: function(){
		return $(this.ui.panel).height() - 350;
	},
	width: function(){
		return $(this.ui.panel).width() * (9/12)+50;
	},
	drawChart: function(attr){
		d3.select("#instance-data-chart").selectAll("*").remove();
		var max = [Number.MIN_VALUE, Number.MIN_VALUE],
			min = [Number.MAX_VALUE, Number.MAX_VALUE],
			h=this.height(),
			w=this.width(),
			mX = 40,
			mY = 20,
			SVG = d3.select("#instance-data-chart").attr({"height":h+50,"width":w+50}).append("svg:g").attr("class","instance-data-chart-wrapper"),
			thisView = this;
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
		
		var vertices = this.attributeVertices;
		var attrPos = [];
		var line;
		var indicatorCircle;
		var SVGParent = d3.select("#instance-data-chart")
							.on("click", startLine)
							.on("mousemove", mousemove)
							.on("mouseenter", addLineMarkers)
							.on("mouseleave", rmLineMarkers);
		
		function addLineMarkers(){
			SVGParent.append("line").attr("class","x-axis-marker axis-marker");
			SVGParent.append("line").attr("class","y-axis-marker axis-marker");
		}
		
		function rmLineMarkers(){
			SVGParent.select(".x-axis-marker").remove();
			SVGParent.select(".y-axis-marker").remove();
		}
		
		function startLine() {
			if(SVGParent.selectAll(".polygon-line")[0].length==0){
				vertices = [];
			}
				if(vertices.length==0){
					SVGParent.selectAll(".polygon-line").remove();
					SVGParent.selectAll(".data-polygon").remove();
			    	SVGParent.selectAll(".data-point-circle").style("stroke","none");
				}
			    var m = d3.mouse(this);
			    if(vertices.length>0){
			    	if(Math.sqrt(Math.pow(vertices[0][0]-m[0],2)+Math.pow(vertices[0][1]-m[1],2))<=5){
			    		line.attr("x2",vertices[0][0]).attr("y2",vertices[0][1]);
				    	SVGParent.append("polygon").attr("class","data-polygon").attr("points", function(){
				    		return vertices.map(function(d){ return [d[0], d[1]].join(",")}).join(" ");
				    	}).style('fill',thisView.highlightDataPoints(attrPos, vertices));
				    	for(var temp in vertices){
				    		vertices[temp][0] = revAttrScale2(vertices[temp][0]-mX);
				    		vertices[temp][1] = revAttrScale1(vertices[temp][1]-mY);
				    	}
				    	thisView.attributeVertices = vertices;
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
		}

		function mousemove() {
		    var m = d3.mouse(this);
		    if(vertices.length > 0){
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
		    d3.select(".x-axis-marker").attr("x1", mX).attr("x2",m[0]).attr("y1", m[1]).attr("y2", m[1]);
		    d3.select(".y-axis-marker").attr("x1", m[0]).attr("x2",m[0]).attr("y1", parseInt(h+mY)).attr("y2", m[1]);
		}
		
		var arc = d3.svg.symbol().type('circle').size(10);
		var buffer = 0;
		buffer = (max[0]-min[0])/10;
		var attrScale1 = d3.scale.linear().domain([min[0]-buffer,max[0]+buffer]).range([h,0]);
		var revAttrScale1 = d3.scale.linear().domain([h,0]).range([min[0]-buffer,max[0]+buffer]);
		var yAxis = d3.svg.axis().tickFormat(function(d) { return Math.round(d*100)/100;}).scale(attrScale1).orient("left");
		SVG.append("g").attr("class","axis yaxis").attr("transform", "translate("+mX+","+mY+")").call(yAxis)
		.append("svg:text").text(thisView.attrs[0]).attr("transform","translate(-25,"+parseInt(h+mY)+")rotate(-90)").style("fill","#808080");
		
		buffer = 0;
		buffer = (max[1]-min[1])/10;
		var attrScale2 = d3.scale.linear().domain([min[1]-buffer,max[1]+buffer]).range([0,w]);
		var revAttrScale2 = d3.scale.linear().domain([0,w]).range([min[1]-buffer,max[1]+buffer]);
		var xAxis = d3.svg.axis().tickFormat(function(d) { return Math.round(d*100)/100;}).scale(attrScale2).orient("bottom");
		SVG.append("g").attr("class","axis xaxis").attr("transform", "translate("+mX+","+parseInt(h+mY)+")").call(xAxis)
		.append("svg:text").attr("transform","translate(0,30)").text(thisView.attrs[1]).style("fill","#808080");
		
		SVG.append("svg:g").attr("class","instance-points");
		var layer = SVG.selectAll(".data-point").data(attr);
		
		var layerEnter = layer.enter().append("g").attr("class","data-point")
		.attr("transform",function(d){
			attrPos.push([parseFloat(mX+attrScale2(d[1])), parseFloat(mY+attrScale1(d[0]))]);
			return "translate("+parseFloat(mX+attrScale2(d[1]))+","+parseFloat(mY+attrScale1(d[0]))+")";
		});

		layerEnter.append('path')
		.attr('d',arc)
		.attr("class","data-point-circle")
		.attr("id", function(d, i){
			return "data-point-"+i;
		})
		.style('fill',function(d){return (d[2]==1) ? "blue" : "red";});
		
	},
	highlightDataPoints: function(attr, vertices){
		var c;
		var classValue = [0,0];
		var el;
		for(var temp in attr){
			if(!isNaN(attr[temp][0]) && !isNaN(attr[temp][1])){
				c = this.pointInPolygon(vertices,attr[temp]);
				if(c){
					el = d3.select("#data-point-"+temp);
					console.log(el.data()[0][2]);
					classValue[el.data()[0][2]]++;
					//el.style("stroke","lightgreen");
				}
			}
		}
//		if(classValue[0]>classValue[1]){
//			return "rgba(255,0,0,0.15)";
//		} else if (classValue[0]<classValue[1]) {
//			return "rgba(0,0,255,0.15)";
//		}
		return "rgba(0,0,0,0.15)";
	},
	pointInPolygon: function(vertices, testPoint){
		  var i, j, c = false;
		  for (i = 0, j = vertices.length-1; i < vertices.length; j = i++) {
		    if ( ((vertices[i][1]>testPoint[1]) != (vertices[j][1]>testPoint[1])) &&
		     (testPoint[0] < (vertices[j][0]-vertices[i][0]) * (testPoint[1]-vertices[i][1]) / (vertices[j][1]-vertices[i][1]) + vertices[i][0]) )
		       c = !c;
		  }
		  return c;
	},
	getInstances: function(){
		if(this.model){
			this.model.set('pickInst',true);
		} 
		var args = {};
		var attrs = [];
		var splits = [];
		var thisView = this;
		thisView.attrs = [];
		$(".pick-attribute-uniqueid").each(function(){
			if($(this).val()!=""){
				attrs.push($(this).val());
				thisView.attrs.push($(this).data('name'));
			}
		});
		$(".pick-split").each(function(){
			splits.push($(this).val());
		});
		args.pickedAttrs = attrs;
		args.splits = splits;
		if(args.pickedAttrs.length==2){
			Cure.PlayerNodeCollection.sync(args);
			$(this.ui.chartFunctions).css({'display':'block'});
		}
	},
	onShow: function(){
		var thisUi = this.ui;
		this.cfMatrix = new cfMatrix();
		this.drawChart([[1,2,3,4,5],[1,2,3,4,5]]);
		this.cfMatrixRegion.show(new cfMatrixView({model: this.cfMatrix}));
		this.pickXaxis.show(new searchFeature({view: 'pickInst'}));
		this.pickYaxis.show(new searchFeature({view: 'pickInst'}));
	}
});

return PickInstanceView;
});
