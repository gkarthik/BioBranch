define([
  //Libraries
	'jquery',
	'marionette',
	'd3',
	//View
	'app/views/optionsView',
	'app/views/distributionChartView',
	'app/views/PickInstance',
	//Templates
	'text!static/app/templates/LeafNode.html',
	'text!static/app/templates/SplitValue.html',
	'text!static/app/templates/SplitNode.html'
    ], function($, Marionette, d3, optionsView, distributionChartView, pickInstView, LeafNodeTemplate, splitValueTemplate, splitNodeTemplate) {
NodeView = Marionette.Layout.extend({
	tagName : 'div',
	className : 'node dragHtmlGroup',
	ui : {
		input : ".edit",
		addgeneinfo : ".addgeneinfo",
		name : ".name",
		chart : ".chart",
		collaboratorIcon: ".collaborator-icon",
		distributionChart: ".distribution-chart",
		addGeneRegion : ".addgeneinfo"
	},
	url: base_url+"MetaServer",
	template : function(serialized_model) {
		if (serialized_model.options.kind == "split_value") {
			return splitValueTemplate(serialized_model);
		} else if (serialized_model.options.kind == "split_node") {
			return splitNodeTemplate(serialized_model);
		} else if(serialized_model.options.kind=="leaf_node"){
			return LeafNodeTemplate(serialized_model);
		}
	},
	regions: {
		chartRegion: ".chartRegion",
		addGeneRegion : ".addgeneinfo",
		distributionChartRegion: ".distributionChartRegion"
	},
	events : {
		'click button.addchildren' : 'addChildren',
		'click button.delete' : 'removeChildren',
		'click .name' : 'showSummary',
		'blur .name': 'setHighlight',
		'click .chart': 'showNodeDetails',
		'click .showDistribution': 'getDistributionData'
	},
	initialize : function(args) {
		this.condensed = args.condensed;
		var _this = this;
		_.bindAll(this, 'remove', 'addChildren', 'showSummary', 'renderPlaceholder');
		this.listenTo(this.model, 'change:x', this.setWidthandPos);
		this.listenTo(this.model, 'change:y', this.setWidthandPos);
		this.listenTo(this.model, 'remove', this.remove);
		this.listenTo(this.model, 'change:collaborator',this.renderPlaceholder);
		this.listenTo(this.model, 'change:showDistChart',this.showDistView);
		this.listenTo(this.model, 'change:showPickInst',this.showPickInst);
		this.listenTo(this.model.get('options'), 'change:kind', this.render);
		this.listenTo(this.model, 'change:name', this.render);
		this.listenTo(this.model, 'window:resized', this.render);
		this.listenTo(this.model.get('options'), 'change:accLimit', this.setNodeClass);
		this.listenTo(this.model, 'change:highlight', this.setHighlight);
		this.listenTo(Cure.vent, 'condensed:true', function(){
			_this.condensed = true;
			_this.$el.addClass("condensed-node");
			_this.chartRegion.close();
		});
		this.listenTo(Cure.vent, 'condensed:false', function(){
			_this.condensed = false;
			_this.$el.removeClass("condensed-node");
			_this.showOptionsView();
		});
		
		var options = this.model.get('options');
		options.set('cid',this.cid);
		
		//Mouse close events
		 $(document).on("mouseup",
        function(e) {
          classToclose = $('.distribution-chart-wrapper');
          if (!classToclose.is(e.target)
              && classToclose.has(e.target).length == 0) {
          	if (_this.distributionChartRegion && _this.distributionChartRegion.currentView) {
          		var el = "."+_this.distributionChartRegion.currentView.className+" .distribution-chart-wrapper";
          		if(!$(el).hasClass("ui-draggable-dragging")){
            		_this.distributionChartRegion.close();
            		_this.$el.css({'z-index':'3'});
            		//Replace IDs with variables.
            		$("#PlayerTreeRegionTree").css({'z-index':4});
          		}
            }
          }
          
          classToclose = $('.pick-instance-wrapper');
          var geneList = $(".ui-autocomplete");
          if (!classToclose.is(e.target)
                  && classToclose.has(e.target).length == 0
                  && !geneList.is(e.target)
                  && geneList.has(e.target).length == 0) {
        	  if (_this.pickInstRegion && _this.pickInstRegion.currentView) {
            		var el = "."+_this.pickInstRegion.currentView.className+" .pick-instance-wrapper";
              		_this.pickInstRegion.close();
              		_this.$el.css({'z-index':'3'});
              		//Replace IDs with variables.
              		$("#PlayerTreeRegionTree").css({'z-index':4});
              }
              }
          
          var container = $(".addnode_wrapper");
          if (!container.is(e.target)
              && container.has(e.target).length == 0
              && !geneList.is(e.target)
              && geneList.has(e.target).length == 0) {
            $("input.mygene_query_target").val("");
            if (_this.addGeneRegion) {
            	_this.addGeneRegion.close();
            }
          }
      });
	},
	setHighlight: function(){
		if(this.model.get('highlight')){
			this.$el.addClass("highlightNode");
		} else {
			this.$el.removeClass("highlightNode");
		}
	},
	getDistributionData: function(){
		if(this.model.get("options").get('kind')=="split_node"){
			this.model.set("getSplitData",true);
			Cure.PlayerNodeCollection.sync();
		}
	},
	showDistView: function(){
		if(this.model.get('showDistChart')==true){
			this.model.set('showDistChart',false);
			var newdistChartView = new distributionChartView({model: this.model.get('distribution_data')});	
			this.distributionChartRegion.show(newdistChartView);
			this.$el.css({'z-index':'9999'});
			$("#PlayerTreeRegionTree").css({'z-index':6});
		}
	},
	showPickInst: function(){
		if(this.model.get('showPickInst')==true){
			this.model.set('showPickInst',false);
			var newpickInstView = new pickInstView({model: this.model});	
			Cure.appLayout.pickInstanceRegion.close();
			Cure.appLayout.pickInstanceRegion.show(newpickInstView);
		}
	},
	error: function(){
		Cure.utils
    .showAlert("<strong>Server Error</strong><br>Please try again in a while.", 0);
	},
	renderPlaceholder: function(){
		if(this.model.get('collaborator')!=null){
			this.$el.find(".collaborator-icon").css({//Change .find to .ui.el
				color: Cure.colorScale(Cure.CollaboratorCollection.indexOf(this.model.get('collaborator'))),
				border: "2px solid "+Cure.colorScale(Cure.CollaboratorCollection.indexOf(this.model.get('collaborator')))
			});
		}	
	},
	showNodeDetails: function(){
		var datasetBinSize = Cure.PlayerNodeCollection.models[0].get('options').get('bin_size');
		var content = "";
		//% cases
		if(this.model.get('options').get('bin_size') && this.model.get('options').get('kind') =="leaf_node"){
			content+="<p class='binsizeNodeDetail'><span class='percentDetails'>"+Math.round((this.model.get('options').get('bin_size')/datasetBinSize)*10000)/100+"%</span><span class='textDetail'>of cases from the training set fall here.</span></p>";
		} else if(this.model.get('options').get('bin_size') && this.model.get('options').get('kind') =="split_node") {
			content+="<p class='binsizeNodeDetail'><span class='percentDetails'>"+Math.round((this.model.get('options').get('bin_size')/datasetBinSize)*10000)/100+"%</span><span class='textDetail'>of cases from the training set pass through this node.</span></p>";
		}		
		//Accuracy
		if(this.model.get('options').get('pct_correct')){
			content+="<p class='accuracyNodeDetail'><span class='percentDetails'>"+Math.round(this.model.get('options').get('pct_correct')*10000)/100+"%</span><span class='textDetail'>is the percentage accuracy at this node based on the training set.</span></p>";
		}
		Cure.utils.showDetailsOfNode(content, this.$el.offset().top, this.$el.offset().left);
	},
	showSummary : function() {
			this.model.set("showJSON", 1);
	},
	setWidthandPos: function(){
	//Render the positions of each node as obtained from d3.
		var numNodes = Cure.utils.getMaxNodesInLevel(Cure.PlayerNodeCollection.at(0));
		
		if(numNodes * 100 >= Cure.width-100){//TODO: find way to get width of node dynamically.
			this.$el.addClass('shrink_'+this.model.get('options').get('kind'));
			this.$el.css({
				width: (Cure.width - 10*numNodes) /numNodes,//TODO: account for border-width and padding programmatically.	
				height: 'auto',
				'min-width': '0'
			});
		} else {
			if(this.$el.hasClass('shrink_'+this.model.get('options').get('kind'))){
				this.$el.removeClass('shrink_'+this.model.get('options').get('kind'));
			}
			if(this.model.get('options').get('kind')=='leaf_node'||this.model.get('options').get('kind')=='split_node'){
				try{
					this.$el.css({
						'width': parseFloat(this.model.get('parentNode').get('parentNode').get('options').get('viewWidth'))+"px",
						'min-width': '0'
					});
				} catch(e){
						this.$el.css({
							'width': "100px"
						});
					}
				} else {
					if(this.model.get('parentNode')!=null){
						this.$el.css({
							'width': parseFloat(this.model.get('parentNode').get('options').get('viewWidth'))+"px",
							'min-width': '0'
						});
					}
				}
			}
		var width = this.$el.outerWidth();
		var styleObject = {
				"left": (this.model.get('x') - ((width) / 2)) +"px",
				"top": (this.model.get('y')+71) +"px",
				'font-size': ((width/100)*16)+"px",
			};
		this.$el.css(styleObject);
		this.model.get('options').set("viewWidth", width);
	},
	setNodeClass: function(){
		var styleObject = {
			"background": "#FFF",
			"borderColor": "#000"
		};
		if(this.model.get('options').get('kind')=="leaf_node"){
			if(this.model.get('name').toUpperCase()==Cure.negNodeName.toUpperCase()) {
				styleObject.background = "rgba(255,0,0,0.2)";
				styleObject.borderColor = "red";
				this.$el.find(".chartContainer").css({"stroke":"red"});
			} else if(this.model.get('name').toUpperCase()==Cure.posNodeName.toUpperCase()) {
				styleObject.background = "rgba(0,0,255,0.2)";
				styleObject.borderColor = "blue";
				this.$el.find(".chartContainer").css({"stroke":"blue"});
			}
		}
		this.$el.attr('class','node dragHtmlGroup');//To refresh class every time node is rendered.
		this.$el.css(styleObject);
		var options = this.model.get('options');
		this.$el.addClass(options.get('kind'));
		var model = this.model;
		if(this.model.get('options').get('kind')=="leaf_node"){
			this.$el.droppable({
				accept: ".gene-pool-item",
				activeClass: "genepool-drop-active",
				hoverClass: "genepool-drop-hover",
				drop: function( event, ui ) {
					if(model.get("options")){
						model.get("options").unset("split_point");
					}
					
					var uidata = {};
					uidata.short_name = $(ui.draggable).data("shortname");
					uidata.long_name = $(ui.draggable).data("longname");
					uidata.unique_id = String($(ui.draggable).data("uniqueid"));
					
					if(model.get("distribution_data")){
						model.get("distribution_data").set({
							"range": -1
						});
					}
					model.set("previousAttributes", model.toJSON());
					model.set("name", uidata.short_name);
					model.get("options").set({
						"unique_id" : uidata.unique_id,
						"kind" : "split_node",
						"full_name" : uidata.long_name
					});
					Cure.PlayerNodeCollection.sync();
				}
			});
		} else {
			if(this.$el.data('ui-droppable')){
				this.$el.droppable("destroy");
			}
		}
	},
	onBeforeRender : function() {
		this.setWidthandPos();
		this.setNodeClass();
		this.setHighlight();
		if(this.chartRegion.currentView){
			this.chartRegion.currentView.remove();
		}
	}, 
	remove : function() {		
		//Remove and destroy current node.
		this.$el.remove();
		Cure.utils.delete_all_children(this.model);
		this.model.destroy();
	},
	removeChildren : function() {
		//Remove all children from current node.
		if (this.model.get('parentNode') != null) {
			Cure.utils.delete_all_children(this.model);
			var prevAttr = this.model.get("previousAttributes");
				for ( var key in prevAttr) {
					if(key!="options"){
						this.model.set(key, prevAttr[key]);
					} else if(key=="options") {
						this.model.get(key).set(prevAttr[key]);
					}
				}
				this.model.set("previousAttributes", []);
		} else {
			Cure.utils.delete_all_children(this.model);
			this.model.destroy();
		}
		Cure.PlayerNodeCollection.sync();
	},
	addChildren : function() {
		//Adding new children to the node. This function shows the AddRootNodeView to show mygeneinfo autocomplete.
		var ShowGeneInfoWidget = new AddRootNodeView({
			'model' : this.model
		});
		var mX = -125;
		if(this.$el.offset().left < 125){
			mX = 0;
		} else if(this.$el.offset().left+this.$el.outerWidth() > (Cure.width-50)){
			mX = -250
		}
		this.ui.addGeneRegion.css({'margin-left': mX+'px'});
		this.addGeneRegion.show(ShowGeneInfoWidget);
	},
	onRender: function(){
		this.$el.removeClass("condensed-node");
		if((this.model.get('options').get("viewWidth")-24)/10 > 0.5){
			this.showOptionsView();
		}
		this.renderPlaceholder();	
	},
	showOptionsView: function(){
		if(!this.condensed && (this.model.get('options').get('kind')=="split_node" || this.model.get('options').get('kind')=="leaf_node")){
			var newOptionsView = new optionsView({model: this.model.get('options')});
			this.chartRegion.show(newOptionsView);
		} else if(this.condensed) {
			this.$el.addClass("condensed-node");
		}
	},
	onShow: function(){
		this.render();//To draw chart for final element.
	},
});
return NodeView;
});
