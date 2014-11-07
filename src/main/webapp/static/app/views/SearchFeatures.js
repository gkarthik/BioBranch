define([
  //Libraries
	'jquery',
	'marionette',
	//Models
	'app/models/Node',
	'app/models/Collaborator',
	//Views
	'app/views/layouts/PathwaySearchLayout',
	'app/views/layouts/AttributeRankerLayout',
	//Templates
	'text!static/app/templates/GeneSummary.html',
	'text!static/app/templates/ClinicalFeatureSummary.html',
	'text!static/app/templates/AddNode.html',
	'text!static/app/templates/AddNodeCustomClassifier.html',
	'text!static/app/templates/AddNodePickInstance.html',
	'text!static/app/templates/AddNodeFeatureBuilder.html',
	//Plugins
	'myGeneAutocomplete',
	'jqueryui'
    ], function($, Marionette, Node, Collaborator, PathwaySearchLayout, AttrRankLayout, geneinfosummary, cfsummary, AddNodeTemplate, AddNodeCustomClassifierTmpl, AddNodePickInstanceTmpl, AddNodeCfFeatureBuilderTmpl) {
	var searchFeatures = Marionette.ItemView.extend({
		initialize: function(args){
			_.bindAll(this, 'selectTree', 'selectCustomFeature', 'selectCustomClassifier', 'selectCf', 'selectGene');
			this.listenTo(Cure.dataset, 'change:validateGenes', this.render);
			this.listenTo(Cure.dataset, 'change:validateNonGenes', this.render);
			this.listenTo(Cure.ClinicalFeatureCollection, 'add', this.render);
			this.parentViewName = (args) ? args.view : null;
		},
			getTemplate: function(serialized_model){
				if(this.parentViewName != null){
					if(this.parentViewName== "aggNode"){
						return AddNodeCustomClassifierTmpl;
					} else if(this.parentViewName== "pickInst"){
						return AddNodePickInstanceTmpl;
					} else if (this.parentViewName == "cfbuilderview") {
						return AddNodeCfFeatureBuilderTmpl;
					}
				} else {
					return AddNodeTemplate;
				}
			},
			url: base_url+"MetaServer",
			ui : {
				'input' : '.mygene_query_target',
				"gene_query": '#gene_query',
				'cf_query': '#cf_query',
				'customfeature_query': '#customfeature_query',
				'aggregatenode_query':'#aggregatenode_query',
				'trees_query':'#tree_query',
				'categoryWrappers': ".category-wrapper",
				'chooseCategory': '.choose-category',
				'rankAttr': '.rank-attributes'
			},
			events:{
				'click .open-addnode': 'openAggNode',
				'click .choose-category': 'chooseCategory',
				'click .show-pick-instance': 'showPickInstance',
				'click .rank-attributes': 'listAllRanked'
			},
			listAllRanked: function(){
				$(this.ui.gene_query).genequery_autocomplete("search");
			},
			openAggNode: function(){
				Cure.appLayout.AggNodeRegion.close();
				var aggNodeLayout;
				var model = this.model;
				//To avoid circular dependency
				require(['app/views/layouts/AggregateNodeLayout'], function( A ){
			            aggNodeLayout = new A({model: model});
			            Cure.appLayout.AggNodeRegion.show(aggNodeLayout);
			    });
			},
			showPickInstance: function(){
				if(this.model){
					 this.model.set('showPickInst',true);
				 } else {
					 Cure.appLayout.pickInstanceRegion.close();
					 var newpickInstView;
					 require(['app/views/PickInstance'], function( A ){
						Cure.appLayout.pickInstanceRegion.show(new A());
				    });
				 }
			},
			chooseCategory: function(e){
				var id = $(e.currentTarget).data("target");
				$(this.ui.chooseCategory).removeClass("active");
				$(e.currentTarget).addClass("active");
				$(this.ui.categoryWrappers).hide();
				if(id=="clinicalfeatures"){
					this.showCf();
				}
				this.$el.find("."+id+"_wrapper").show();
			},
			showChooseTrees: function(){
				if(this.model){
					 var model = this.model;
				 }
				 var thisView = this;
			      $(this.ui.trees_query).autocomplete({
			  			source: function( request, response ) {
			  				var args = {
			  						command : "get_trees_by_search",
			  						query: $(thisView.ui.trees_query).val(),
			  						dataset: Cure.dataset.get('id')
			  				};
			  				
			  				$.ajax({
				    	          type : 'POST',
				    	          url : thisView.url,
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
			  				select: thisView.selectTree
			  			}).bind('focus', function(){ $(this).autocomplete("search"); } );
			},
			showAggregateNodes: function(){
				if(this.model){
					 var model = this.model;
				 }
				 var thisView = this;
			      $(this.ui.aggregatenode_query).autocomplete({
			  			source: function( request, response ) {
			  					var args = {
			    	        command : "custom_classifier_search",
			    	        query: request.term,
			    	        dataset: Cure.dataset.get('id')
			    	      };
			    	      $.ajax({
			    	          type : 'POST',
			    	          url : thisView.url,
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
			  				select: thisView.selectCustomClassifier
			  			}).bind('focus', function(){ $(this).autocomplete("search"); } );
			},
			showCustomFeatures: function(){
				 if(this.model){
					 var model = this.model;
				 }
				 var thisView = this;
			      $(this.ui.customfeature_query).autocomplete({
			  			source: function( request, response ) {
			  					var args = {
			    	        command : "custom_feature_search",
			    	        query: request.term,
			    	        dataset: Cure.dataset.get('id')
			    	      };
			    	      $.ajax({
			    	          type : 'POST',
			    	          url : thisView.url,
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
			  				select: thisView.selectCustomFeature
			  			}).bind('focus', function(){ $(this).autocomplete("search"); } );
			},
			showCf: function(){
				if (this.model) {
					var model = this.model;
				}
				var thisView = this;
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
							var dropdown = $(thisView.ui.cf_query).data('ui-autocomplete').bindings[1];
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
					select : thisView.selectCf
				}).bind('focus', function(){ $(this).autocomplete("search"); } );
			},
			onRender : function() {
				if (this.model) {
					var model = this.model;
				}
				var thisView = this;
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
							var dropdown = $(thisView.ui.gene_query).data('my-genequery_autocomplete').bindings[0];
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
					select : thisView.selectGene
				});
				$(this.ui.gene_query).focus();
				this.showCustomFeatures();
				this.showCf();
				this.showAggregateNodes();
				this.showChooseTrees();
			},
			selectTree: function(){},
			selectCustomFeature: function(){},
			selectCustomClassifier: function(){},
			selectCf: function(){},
			selectGene: function(){},
	});

	//searchFeatures.extend = Marionette.ItemView.extend;
	
return searchFeatures;
});
