define([
	'jquery',
	'marionette',
	//Views
	'app/views/GeneCollectionView',
	'app/views/GenePoolCollectionView',
	'app/views/layouts/GenePoolLayout',
	//Templates
	'text!static/app/templates/PathwayLayout.html'
    ], function($, Marionette, GeneCollectionView, GenePoolCollectionView, GenePoolLayout, PathwayLayoutTmpl) {
PathwayLayout = Marionette.Layout.extend({
    template: PathwayLayoutTmpl,
    className: "panel panel-default",
    url: base_url+"MetaServer",
    ui: {
  		"pathwaysearch": "#pathwaysearch_query"
    },
    events: {
    	'click .close-pathway-search': 'closePathwaySearch',
    	'click .add-to-gene-pool': 'addGenestoPool'
    },
    regions: {
      GeneCollectionRegion: "#GeneCollectionRegion"
    },
    closePathwaySearch: function(){
    	Cure.sidebarLayout.PathwaySearchRegion.close();
    },
    initialize: function(options){
    	this.aggNode = options.aggNode;
    },
    GeneCollection: null,
    onRender: function(){
    	this.GeneCollection = new GeneCollection();
    	this.GeneCollectionRegion.show(new GeneCollectionView({
    		collection: this.GeneCollection
    	}));
      var thisView = this;
      var thisURL = this.url;
      $(this.ui.pathwaysearch).autocomplete({
  			source: function( request, response ) {
  					var args = {
    	        command : "search_pathways",
    	        query: request.term
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
    	          		  label: item.name+": "+item.source_db,
    	          		  value: item.name,
    	          		  data: item
    	          	  };
    	          	}));
    	        }
    	      });
  				},
  				minLength: 3,
  				select: function( event, ui ) {
  					var args = {
  	  	        command : "get_genes_of_pathway",
  	  	        pathway_id:	ui.item.data.id,
  	  	        dataset: Cure.dataset
  	  	      };
  	  	      $.ajax({
  	  	          type : 'POST',
  	  	          url : thisURL,
  	  	          data : JSON.stringify(args),
  	  	          dataType : 'json',
  	  	          contentType : "application/json; charset=utf-8",
  	  	          success : function(data){
  	  	        	thisView.GeneCollection.reset();
  	  	        	thisView.GeneCollection.add(data);
  	  	          }
  	  	      });
  				},
  				open: function() {
  				$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
  				},
  				close: function() {
  				$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
  				}
  			});
    },
    addGenestoPool: function(){
    	var model;
    	console.log("hello world");
    	for(var i=0;i<this.GeneCollection.length;i++){
    		model = this.GeneCollection.models[i];
    		if(!model.get('keepInCollection')){
    			model.destroy();
    			i--;
    		}
    	}
    	console.log(this.aggNode);
    	if(this.aggNode){
    		var layout = Cure.sidebarLayout.AggNodeRegion.currentView;
    		layout.addToGeneCollection(this.GeneCollection.toArray());
    		this.GeneCollection.reset();
    	} else {
    		Cure.GenePoolRegion.currentView.addGeneCollection(this.GeneCollection);
    	}
    	Cure.sidebarLayout.PathwaySearchRegion.close();
    }
});
return PathwayLayout;
});
