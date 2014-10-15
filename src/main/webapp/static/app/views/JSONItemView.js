define([
	'jquery',
	'marionette',
	//Model
	'app/models/Node',
	//Templates
	'text!static/app/templates/JSONSplitNodeGeneSummary.html',	
	'text!static/app/templates/JSONSplitValueSummary.html',
	'text!static/app/templates/JSONSplitNodeCfSummary.html',
	'text!static/app/templates/CustomSplitNode.html',
	'text!static/app/templates/ClassifierInString.html',
	'text!static/app/templates/TreeDetails.html',
	'text!static/app/templates/CustomFeatureSummary.html'
    ], function($, Marionette, Node, splitNodeGeneSummary, splitValueSummary, splitNodeCfSummary, customNodeSummaryTmpl, classifierInString, treeDetails, customFeatureSummary) {
JSONItemView = Marionette.ItemView.extend({
	model : Node,
	ui : {
		jsondata : ".jsonview_data",
		showjson : ".showjson",
		sampleTable: '#sample-wrapper',
		featuresInClassifier: ".features-in-classifier",
		treeStructure: ".tree-structure .tree-details",
		SvgPreview: ".tree-structure svg",
		componentSummary: ".component_summary"
	},
	events : {
		'click .showjson' : 'ShowJSON',
		'click button.close' : 'HideJSON',
		'click #text-exp': 'testExp',
		'click .close-json-view': 'HideView'
	},
	tagName : "tr",
	url:base_url+"MetaServer",
	initialize : function() {
		_.bindAll(this, 'getSummary', 'ShowJSON', 'HideJSON', 'renderTestCase', 'drawTreeStructure', 'HideView', 'drawCustomSetChart', 'getCustomSetInstances');
		this.listenTo(this.model,'change:gene_summary', this.render);
		this.model.bind('change:showJSON', function() {
			if (this.model.get('showJSON') != 0) {
				this.ShowJSON();
			} else {
				this.HideJSON();
			}
		}, this);
		var thisView = this;
		$(document).mouseup(function(e){
			var classToclose = $(".jsonview_data");
			if (!classToclose.is(e.target)	&& classToclose.has(e.target).length == 0) 
			{
				thisView.HideJSON();
			}
		});
	},
	onRender : function() {
		if (this.model.get('showJSON') != 0) {
			this.ShowJSON();
		}
		if(this.model.get('options').toJSON().hasOwnProperty("unique_id")){
			if(this.model.get('options').get('unique_id').indexOf("custom_classifier")!=-1){
				this.getCustomClassifierFeatures();
			} else if(this.model.get('options').get('unique_id').indexOf("custom_tree")!=-1) {
				this.getCustomTreeStructure();
			} else if(this.model.get('options').get('unique_id').indexOf("custom_set")!=-1) {
				this.getCustomSet();
			} else if(this.model.get('options').get('unique_id').indexOf("custom_feature")!=-1) {
				this.getCustomFeature();
			}
		}
	},
	getCustomFeature: function(){
		var thisView = this;
		var args = {
    	        command : "custom_feature_getById",
    	        id: this.model.get('options').get('unique_id').replace("custom_feature_",""),
    	        dataset: Cure.dataset.get('id')
    	      };
    	      $.ajax({
    	          type : 'POST',
    	          url : this.url,
    	          data : JSON.stringify(args),
    	          dataType : 'json',
    	          contentType : "application/json; charset=utf-8",
    	          success : function(data){
    	        	  $(thisView.ui.componentSummary).html(customFeatureSummary(data));
    	          },
    	          error: this.error
    	});
	},
	testExp: function(){
		var args = {
    	        command : "custom_feature_testcase",
    	        id: this.model.get('options').get('unique_id'),
    	        dataset: Cure.dataset.get('id')
    	      };
    	      $.ajax({
    	          type : 'POST',
    	          url : this.url,
    	          data : JSON.stringify(args),
    	          dataType : 'json',
    	          contentType : "application/json; charset=utf-8",
    	          success : this.renderTestCase,
    	          error: this.error
    	});
	},
	renderTestCase: function(data){
		var html = "<h3>Sample No: "+data.sample+"</h3>"; 
		html += "<table id='sample-data' class='table pull-right'><tr><th>Attribute</th><th>Value</th></tr>";
		for(var temp in data.features){
			html+="<tr><td>";
			html+=temp;
			html+="</td><td>";
			html+=data.features[temp];
			html+="</td></tr>";
		}
		html+="<tr class='info'><td>"+this.model.get('name')+"</td><td>"+data.custom_feature+"</td></tr></table>";
		$(this.ui.sampleTable).html(html);
	},
	error : function(data) {
		Cure.utils
    .showAlert("<strong>Server Error</strong><br>Please try saving again in a while.", 0);
	},
	getSummary : function() {
		var thisView = this,
			summary = this.model.get("gene_summary").summaryText || "";
		if (summary.length == 0) {
			$.getJSON("http://mygene.info/v2/gene/" + thisView.model.get("options").get('unique_id') +"?callback=?",
					function(data) {
						var summary = {
							"summaryText" : data.summary,
							"goTerms" : data.go,
							"generif" : data.generif,
							"name" : data.name
						};
						thisView.model.set("gene_summary", summary);
						thisView.model.set("showJSON", 1);
					});
		}
	},
	template : function(serialized_model) {
		var name = serialized_model.name;
		var options = serialized_model.options;
		if(serialized_model.options.hasOwnProperty("unique_id") && !isNaN(serialized_model.options.unique_id)){
			if(serialized_model.options.kind == "split_node" && !isNaN(serialized_model.options.unique_id)) {
				return splitNodeGeneSummary({
					id: serialized_model.cid,
					name : name,
					summary : serialized_model.gene_summary,
					kind : serialized_model.options.kind
				});
			} else if (serialized_model.options.kind == "split_node" && isNaN(serialized_model.options.unique_id)){
				return splitNodeCfSummary({
					id: serialized_model.cid,
					name : name,
					summary : serialized_model.gene_summary,
					kind : serialized_model.options.kind
				});
			} else {
				return splitValueSummary({
					id: serialized_model.cid,
					name : name,
					summary : serialized_model.gene_summary,
					kind : serialized_model.options.kind
				});
			} 
		} else if(!serialized_model.options.hasOwnProperty("unique_id")) {
			return splitValueSummary({
				id: serialized_model.cid,
				name : name,
				summary : serialized_model.gene_summary,
				kind : serialized_model.options.kind
			});
		} else {
			return customNodeSummaryTmpl({
				id: serialized_model.options.cid,
				name : name,
				description : serialized_model.options.description,
				kind : serialized_model.options.kind,
				options: options
			});
		}
		
	},
	getCustomSet: function(){
		var args = {
				command:"custom_set_get",
				customset_id: this.model.get('options').get('unique_id').replace("custom_set_",""),
    	        dataset: Cure.dataset.get('id')
    	      };
    	      $.ajax({
    	          type : 'POST',
    	          url : this.url,
    	          data : JSON.stringify(args),
    	          dataType : 'json',
    	          contentType : "application/json; charset=utf-8",
    	          success : this.getCustomSetInstances,
    	          error: this.error
    	});
	},
	getCustomSetInstances: function(data){
		this.model.set('pickInst',true);
		var tree = [];
		var thisView = this;
		var cSetData = data;
		if(Cure.PlayerNodeCollection.length>0){
			tree = Cure.PlayerNodeCollection.at(0).toJSON();
		}
		Cure.utils.showLoading(null);
		var testOptions = {
				value: $("input[name='testOptions']:checked").val(),
				percentSplit:  $("input[name='percent-split']").val()
		};
		var pickedAttrs = [];
		for(var temp in data.features){
			pickedAttrs.push(data.features[temp].unique_id);
		}
		var args = {
				command : "scoretree",
				dataset : Cure.dataset.get('id'),
				treestruct : tree,
				comment: Cure.Comment.get("content"),
				player_id : Cure.Player.get('id'),
				previous_tree_id: Cure.PlayerNodeCollection.prevTreeId,
				testOptions: testOptions,
				pickedAttrs: pickedAttrs
			};
		
		//POST request to server.		
		$.ajax({
			type : 'POST',
			url : this.url,
			data : JSON.stringify(args),
			dataType : 'json',
			contentType : "application/json; charset=utf-8",
			success : function(data){
				thisView.drawCustomSetChart(data, cSetData);
			},
			error : this.error
		});
	},
	drawCustomSetChart: function(data, cSetData){
		Cure.utils.hideLoading();
		var attr = data.instances_data;
		var requiredModel = Cure.PlayerNodeCollection.findWhere({pickInst: true});
		if(requiredModel){
			requiredModel.set('pickInst',false);
		}
		var id = "#custom-set-instances-"+this.model.get('options').get('cid');
		d3.select(id).select(".instance-data-chart-wrapper").remove();
		var max = [Number.MIN_VALUE, Number.MIN_VALUE],
			min = [Number.MAX_VALUE, Number.MAX_VALUE],
			h = 200,
			w = window.innerWidth * 0.4,
			mX = 40,
			mY = 20,
			SVG = d3.select(id).attr({"height":h+60,"width":w+60}).append("svg:g").attr("class","instance-data-chart-wrapper"),
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
		
		var polygonVertices = JSON.parse(cSetData.constraints);
		var line;
		var indicatorCircle;
		
		var arc = d3.svg.symbol().type('circle').size(10);
		var buffer = 0;
		buffer = (max[0]-min[0])/10;
		var attrScale1 = d3.scale.linear().domain([min[0]-buffer,max[0]+buffer]).range([h,0]);
		var revAttrScale1 = d3.scale.linear().domain([h,0]).range([min[0]-buffer,max[0]+buffer]);
		var yAxis = d3.svg.axis().tickFormat(function(d) { return Math.round(d*100)/100;}).scale(attrScale1).orient("left");
		SVG.append("g").attr("class","axis yaxis").attr("transform", "translate("+mX+","+mY+")").call(yAxis)
		.append("svg:text").text(cSetData.features[0].short_name).attr("transform","translate(-25,"+parseInt(h+mY)+")rotate(-90)").style("fill","#808080");
	
		buffer = 0;
		buffer = (max[1]-min[1])/10;
		var attrScale2 = d3.scale.linear().domain([min[1]-buffer,max[1]+buffer]).range([0,w]);
		var revAttrScale2 = d3.scale.linear().domain([0,w]).range([min[1]-buffer,max[1]+buffer]);
		var xAxis = d3.svg.axis().tickFormat(function(d) { return Math.round(d*100)/100;}).scale(attrScale2).orient("bottom");
		SVG.append("g").attr("class","axis xaxis").attr("transform", "translate("+mX+","+parseInt(h+mY)+")").call(xAxis)
		.append("svg:text").attr("transform","translate(0,30)").text(cSetData.features[1].short_name).style("fill","#808080");
		
		SVG.append("svg:g").attr("class","instance-points");
		var layer = SVG.selectAll(".data-point").data(attr);
		
		var layerEnter = layer.enter().append("g").attr("class","data-point")
		.attr("transform",function(d){
			return "translate("+parseFloat(mX+attrScale2(d[1]))+","+parseFloat(mY+attrScale1(d[0]))+")";
		});

		layerEnter.append('path')
		.attr('d',arc)
		.attr("class","data-point-circle")
		.attr("id", function(d, i){
			return "data-point-"+i;
		})
		.style('fill',function(d){return (d[2]==1) ? "blue" : "red";});
		
		var vertices = [];
		for(var temp in polygonVertices){
			vertices.push({"x":attrScale2(polygonVertices[temp][0]),"y":attrScale1(polygonVertices[temp][1])});
		}
		console.log(polygonVertices);
		console.log(vertices);
		
		SVG.selectAll("polygon")
	    .data([vertices])
	    .enter()
	    .append("polygon")
	    .attr("points",function(d) { 
	    	return d.map(function(d) {
	            return [parseFloat(mX+d.x),parseFloat(mY+d.y)].join(",");
	        }).join(" ");
	    })
	    .attr("stroke","black")
	    .attr("stroke-width",1)
	    .style("fill","none");
	},
	getCustomTreeStructure: function(){
		var args = {
				command:"get_tree_by_id",
				treeid: this.model.get('options').get('unique_id').replace("custom_tree_",""),
    	        dataset: Cure.dataset.get('id')
    	      };
    	      $.ajax({
    	          type : 'POST',
    	          url : this.url,
    	          data : JSON.stringify(args),
    	          dataType : 'json',
    	          contentType : "application/json; charset=utf-8",
    	          success : this.drawTreeStructure,
    	          error: this.error
    	});
	},
	drawTreeStructure: function(data){
		data.json_tree = JSON.parse(data.json_tree);
		$(this.ui.treeStructure).html(treeDetails(data));
		var treestruct = data.json_tree.treestruct;
		var id = $(this.ui.SvgPreview).attr('id');
		var svg = d3.select("#"+id)
			.attr("width",300)
			.attr("height",300)
			.append("g")
			.attr("transform","translate(0,20)");
		var cluster = d3.layout.tree().size([ 250, 250 ]);
		var diagonal = d3.svg.diagonal().projection(function(d) { return [d.x, d.y]; });
		var json = JSON.stringify(treestruct);
		var nodes = cluster.nodes(JSON.parse(json)),
    links = cluster.links(nodes);
	  var link = svg.selectAll(".link")
	      .data(links)
	    .enter().append("path")
	      .attr("class", "link")
	      .attr("d", diagonal)
	      .style("stroke","steelblue")
	      .style("stroke-width", "2");
	
	  var node = svg.selectAll(".node")
	      .data(nodes)
	    .enter().append("g")
	      .attr("class", "node")
	      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
	
	  node.append("text")
	      .attr("dx", function(d) { return d.children ? -8 : 8; })
	      .attr("dy", 3)
	      .style("text-anchor", "middle")
	      .text(function(d) { return d.name; });
	},
	getCustomClassifierFeatures: function(){
		var thisView = this;
		var args = {
				command:"custom_classifier_getById",
				id: this.model.get('options').get('unique_id').replace("custom_classifier_",""),
    	        dataset: Cure.dataset.get('id')
    	      };
		if(this.model.get('options').get('unique_id').indexOf("new")==-1){
			$.ajax({
  	          type : 'POST',
  	          url : this.url,
  	          data : JSON.stringify(args),
  	          dataType : 'json',
  	          contentType : "application/json; charset=utf-8",
  	          success : function(data){
  	        	  $(thisView.ui.featuresInClassifier).html(classifierInString(data));
  	          },
  	          error: this.error
  	});
		}
	},
	ShowJSON : function() {
		var description =null;
		var idFlag = 1;
		if(this.model.get('options').get('kind') == "split_node"){
			if(this.model.get('options').get('unique_id')!="" && this.model.get('options').get('unique_id')!=null && this.model.get('options').get('unique_id').indexOf("custom_")==-1){
				if(!isNaN(this.model.get('options').get('unique_id'))){
					idFlag = 0;
					this.getSummary();
				} else {
					description = this.model.get('options').get('description').replace("\\n","<br>");
				}
			} else {
				//For Custom Features
			}
		} else if(this.model.get('options').get('kind') == "split_value") {
				if(isNaN(this.model.get('parentNode').get('options').get('unique_id'))){
					var summaryTextArray = this.model.get('parentNode').get('options').get('description').split("\n");
					for(var temp in summaryTextArray){
						if(summaryTextArray[temp].match(this.model.get('name').replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&"))){//To escape +
							description = summaryTextArray[temp]; 
						}
					}
					if(description == null || description.split(" ").length < 3){
						description = this.model.get('name') + " " + this.model.get('parentNode').get('name');
					}
				} else {
					description = this.model.get('name')+" expression level of "+ this.model.get('parentNode').get('name');
				}
		} else {
			if(this.model.get('majClass').toUpperCase()==Cure.posNodeName){
				description = "Predicted to survive beyond ten years.";
			} else {
				description = "<b>Not</b> Predicted to survive beyond ten years.";
			}
		}
		if(idFlag){
			var summary = {};
			summary.name = this.model.get('name');
			summary.summaryText = description;
			this.model.set("gene_summary",summary);
			this.model.set("showJSON", 1);
		}
		
		this.$el.find(this.ui.showjson).addClass("disabled");
		this.$el.find(this.ui.jsondata).css({
			'display' : 'block'
		});
		return 1;
	},
	HideView: function(e){
		e.preventDefault();
		this.HideJSON();
	},
	HideJSON : function() {
		$(this.ui.jsondata).css({
			'display' : 'none'
		});
		$(this.ui.showjson).removeClass("disabled");
		this.model.set("showJSON", 0);
	}
});

return JSONItemView;
});
