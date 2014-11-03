define([
  //Libraries
	'jquery',
	'marionette',
	//Models
	'app/models/Node',
	'app/models/Collaborator',
	//Views
	'app/views/layouts/PathwaySearchLayout',
	'app/views/layouts/AggregateNodeLayout',
	'app/views/layouts/AttributeRankerLayout',
	'app/views/CustomFeatureBuilder',
	'app/views/PickInstance',
	//Templates
	'text!static/app/templates/GeneSummary.html',
	'text!static/app/templates/ClinicalFeatureSummary.html',
	'text!static/app/templates/AddNode.html',
	//Plugins
	'myGeneAutocomplete',
	'jqueryui'
    ], function($, Marionette, Node, Collaborator, PathwaySearchLayout, AggNodeLayout, AttrRankLayout, FeatureBuilder, pickInstView, geneinfosummary, cfsummary, AddNodeTemplate) {
AddRootNodeView = Marionette.ItemView.extend({
	initialize : function() {
		this.listenTo(Cure.dataset, 'change:validateGenes', this.render);
		this.listenTo(Cure.dataset, 'change:validateNonGenes', this.render);
		this.listenTo(Cure.ClinicalFeatureCollection, 'add', this.render);
	},
	ui : {
		'input' : '.mygene_query_target',
		"gene_query": '#gene_query',
		'cf_query': '#cf_query',
		'customfeature_query': '#customfeature_query',
		'aggregatenode_query':'#aggregatenode_query',
		'trees_query':'#tree_query',
		'categoryWrappers': ".category-wrapper",
		'chooseCategory': '.choose-category'
	},
	events:{
		'click .open-addnode': 'openAggNode',
		'click .open-feature-builder': 'openFeatureBuilder',
		'click .choose-category': 'chooseCategory',
		'click .show-pick-instance': 'showPickInstance',
		'click .rank-attributes': 'showRankAttr'
	},
	showRankAttr: function(){
		Cure.appLayout.AttrRankRegion.close();
		Cure.appLayout.AttrRankRegion.show(new AttrRankLayout({model: this.model, aggNode: false}));
	},
	openFeatureBuilder: function(){
//		Cure.FeatureBuilderView = new FeatureBuilder({model:this.model});
//		Cure.FeatureBuilderRegion.show(Cure.FeatureBuilderView);
	},
	openAggNode: function(){
		Cure.appLayout.AggNodeRegion.close();
		Cure.AggNodeLayout = new AggNodeLayout({model: this.model});
		Cure.appLayout.AggNodeRegion.show(Cure.AggNodeLayout);
	},
	showPickInstance: function(){
		if(this.model){
			 this.model.set('showPickInst',true);
		 } else {
			 Cure.appLayout.pickInstanceRegion.close();
			 var newpickInstView = new pickInstView();
			 Cure.appLayout.pickInstanceRegion.show(newpickInstView);
		 }
	},
	chooseCategory: function(e){
		var id = $(e.currentTarget).attr("id");
		$(this.ui.chooseCategory).removeClass("active");
		$(e.currentTarget).addClass("active");
		$(this.ui.categoryWrappers).hide();
		if(id=="clinicalfeatures"){
			this.showCf();
		}
		$("#"+id+"_wrapper").show();
	},
	showChooseTrees: function(){
		if(this.model){
			 var model = this.model;
		 }
		 var thisURL = this.url;
		 var thisUi = this.ui
	      $(this.ui.trees_query).autocomplete({
	  			source: function( request, response ) {
	  				var args = {
	  						command : "get_trees_by_search",
	  						query: $(thisUi.trees_query).val(),
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
		    	          		  label: item.user.firstName+": "+item.comment,
		    	          		  value: item,
		    	          		  data: item
		    	          	  };
		    	          	}));
		    	          }
	  				});
	  			},
	  				minLength: 0,
	  				select: function( event, ui ) {
	  					if(ui.item.label != undefined){//To ensure "no gene name has been selected" is not accepted.
	  						if(!Cure.initTour.ended()){
	  							Cure.initTour.end();
	  						}
	  						$("#SpeechBubble").remove();
	  						var kind_value = "";
	  						var name_node = ui.item.data.user.firstName+" | Tree ID: "+ui.item.data.id;
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
	  							model.set("name", name_node);
	  							model.set('accLimit', 0, {silent:true});
	  							
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
	  								"unique_id" : "custom_tree_"+ui.item.data.id,
	  								"kind" : "split_node",
	  								"full_name" : '',
	  								"description" : name_node+ "| Created: "+ui.item.data.created
	  							});
	  						} else {
	  							new Node({
	  								'name' : name_node,
	  								"options" : {
	  									"unique_id" : "custom_tree_"+ui.item.data.id,
		  								"kind" : "split_node",
		  								"full_name" : '',
		  								"description" : name_node+ "| Created: "+ui.item.data.created
	  								}
	  							});
	  						}
	  						Cure.PlayerNodeCollection.sync();
	  					}
	  				},
	  			}).bind('focus', function(){ $(this).autocomplete("search"); } );
	},
	showAggregateNodes: function(){
		if(this.model){
			 var model = this.model;
		 }
			  
		 var thisURL = this.url;
	      $(this.ui.aggregatenode_query).autocomplete({
	  			source: function( request, response ) {
	  					var args = {
	    	        command : "custom_classifier_search",
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
	  					if(ui.item.label != undefined){//To ensure "no gene name has been selected" is not accepted.
	  						if(!Cure.initTour.ended()){
	  							Cure.initTour.end();
	  						}
	  						$("#SpeechBubble").remove();
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
	  							model.set("name", ui.item.data.name);
	  							model.set('accLimit', 0, {silent:true});
	  							
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
	  								"unique_id" : "custom_classifier_new_"+ui.item.data.id,
	  								"kind" : "split_node",
	  								"full_name" : '',
	  								"description" : ui.item.data.description
	  							});
	  						} else {
	  							new Node({
	  								'name' : ui.item.data.name,
	  								"options" : {
	  									"unique_id" : "custom_classifier_new_"+ui.item.data.id,
		  								"kind" : "split_node",
		  								"full_name" : '',
		  								"description" : ui.item.data.description
	  								}
	  							});
	  						}
	  						Cure.PlayerNodeCollection.sync();
	  					}
	  				},
	  			}).bind('focus', function(){ $(this).autocomplete("search"); } );
	},
	showCustomFeatures: function(){
		 if(this.model){
			 var model = this.model;
		 }
			  
		 var thisURL = this.url;
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
	  					if(ui.item.label != undefined){//To ensure "no gene name has been selected" is not accepted.
	  						if(!Cure.initTour.ended()){
	  							Cure.initTour.end();
	  						}
	  						$("#SpeechBubble").remove();
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
	  							model.set("name", ui.item.data.name);
	  							model.set('accLimit', 0, {silent:true});
	  							
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
	  								"unique_id" : "custom_feature_"+ui.item.data.id,
	  								"kind" : "split_node",
	  								"full_name" : '',
	  								"description" : ui.item.data.description
	  							});
	  						} else {
	  							new Node({
	  								'name' : ui.item.data.name,
	  								"options" : {
	  									"unique_id" : "custom_feature_"+ui.item.data.id,
		  								"kind" : "split_node",
		  								"full_name" : '',
		  								"description" : ui.item.data.description
	  								}
	  							});
	  						}
	  						Cure.PlayerNodeCollection.sync();
	  					}
	  				},
	  			}).bind('focus', function(){ $(this).autocomplete("search"); } );
	},
	showCf: function(){
		if (this.model) {
			var model = this.model;
		}
		var thisUi = this.ui;
		if(Cure.PlayerNodeCollection.length>0){
          	model.set('pickInst', true);
          	tree = Cure.PlayerNodeCollection.at(0).toJSON();
         }
		Cure.ClinicalFeatureCollection.fetch();
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
				if(ui.item.long_name != undefined){//To ensure "no gene name has been selected" is not accepted.
					if(!Cure.initTour.ended()){
						Cure.initTour.end();
					}
					$("#SpeechBubble").remove();
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
						model.set("name", ui.item.short_name.replace(/_/g," "));
						model.set('accLimit', 0, {silent:true});
						
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
							"unique_id" : ui.item.unique_id,
							"kind" : "split_node",
							"full_name" : ui.item.long_name,
							"description" : ui.item.description,
						});
					} else {
						var newNode = new Node({
							'name' : ui.item.short_name.replace(/_/g," "),
							"options" : {
								"unique_id" : ui.item.unique_id,
								"kind" : "split_node",
								"full_name" : ui.item.long_name,
								"description" : ui.item.description,
							}
						});
					}
					Cure.PlayerNodeCollection.sync();
				}
			},
		}).bind('focus', function(){ $(this).autocomplete("search"); } );
//			.data("ui-autocomplete")._renderItem = function (ul, item) {
//		    return $("<li></li>")
//	        .data("item.autocomplete", item)
//	        .append("<a>" + item.label + "</a>")
//	        .appendTo(ul);
//	    };
	},
	template : AddNodeTemplate,
	url: base_url+"MetaServer",
	onRender : function() {
		
		if (this.model) {
			var model = this.model;
		}
		var thisUi = this.ui;
		$(this.ui.gene_query).genequery_autocomplete({
			open: function(event){
				var scrollTop = $(event.target).offset().top-400;
				$("html, body").animate({scrollTop:scrollTop}, '500');
			},
			minLength: 0,
			source:  function( request, response ) {
				var _options = this.options;
                $.ajax({
                    url: _options.mygene_url,
                    dataType: "jsonp",
                    jsonp: 'callback',
                    data: {
                        q: _options.q.format({term:request.term}),
                        sort:_options.sort,
                        limit:_options.limit,
                        fields: _options.fields,
                        species: _options.species,
                        userfilter:_options.userfilter//Added to send userfilter parameter
                    },
                    success: function( data ) {
                        var species_d = {3702: 'thale-cress',
                                         6239: 'nematode',
                                         7227: 'fruitfly',
                                         7955: 'zebrafish',
                                         8364: 'frog',
                                         9606: 'human',
                                         9823: 'pig',
                                         10090: 'mouse',
                                         10116: 'rat'};
                        if (data.total || !data.success){
//                            response( $.map( data.hits, function( item ) {
//                            	  entrezids.push(item.entrezgene);
//                                var obj = {
//                                    id: item._id,
//                                    value: item[_options.value_attr]
//                                }
//                                $.extend(obj, item);
//                                if (species_d[obj.taxid]){
//                                    obj.species = species_d[obj.taxid];
//                                }
//                                obj.label = _options.gene_label.format(obj);
//                                return obj;
//                            }));
                        	var entrezids = [];
                        	var hits = data.hits || [];
                            $.map( hits, function( item ) {
                              entrezids.push(String(item.entrezgene));
                            });
                            var testOptions = {
                    				value: $("input[name='testOptions']:checked").val(),
                    				percentSplit:  $("input[name='percent-split']").val()
                    		};
                            var tree = {};
                            if(Cure.PlayerNodeCollection.length>0){
                            	model.set('pickInst', true);
                            	tree = Cure.PlayerNodeCollection.at(0).toJSON();
                            }
                            var args = {
                    				command : "rank_attributes",
                    				dataset : Cure.dataset.get('id'),
                    				treestruct : tree,
                    				comment: Cure.Comment.get("content"),
                    				testOptions: testOptions,
                    				unique_ids: entrezids
                    			};
                    		
                    		//POST request to server.
                    		$.ajax({
                    			type : 'POST',
                    			url : './MetaServer',
                    			data : JSON.stringify(args),
                    			dataType : 'json',
                    			contentType : "application/json; charset=utf-8",
                    			success : function(data){
                    				var requiredModel = Cure.PlayerNodeCollection.findWhere({pickInst: true});
                    				if(requiredModel){
                    					requiredModel.set('pickInst',false);
                    				}
                    				response($.map(data, function(item){
                    					var obj = {
                            				entrezgene: item.unique_id,
                            				name: item.long_name,
                            				symbol: item.short_name,
                            				id: item.id,
                            				infogain: item.infogain
                    					};
                    					obj.label = _options.gene_label.format(obj);
                    					return obj;
                    				}));
                    			},
                    			error : function(data){
                    				response([{label:'no matched gene found.', value:''}]);
                    			}
                    		});
                        } else {
                        	response([{label:'no matched gene found.', value:''}]);
                        }
                    }
                });
            },
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
				if(ui.item.name != undefined){//To ensure "no gene name has been selected" is not accepted.
					if(!Cure.initTour.ended()){
						Cure.initTour.end();
					}
					$("#SpeechBubble").remove();
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
						model.set("name", ui.item.symbol);
						model.get("options").set({
							"unique_id" : ui.item.entrezgene,
							"kind" : "split_node",
							"full_name" : ui.item.name
						});
					} else {
						var newNode = new Node({
							'name' : ui.item.symbol,
							"options" : {
								"unique_id" : ui.item.entrezgene,
								"kind" : "split_node",
								"full_name" : ui.item.name
							}
						});
					}
					Cure.PlayerNodeCollection.sync();
				}
			}
		}).bind('focus', function(){ $(this).genequery_autocomplete("search"); } );
		$(this.ui.gene_query).focus();
		this.showCustomFeatures();
		this.showCf();
		this.showAggregateNodes();
		this.showChooseTrees();
	}
});


return AddRootNodeView;
});
