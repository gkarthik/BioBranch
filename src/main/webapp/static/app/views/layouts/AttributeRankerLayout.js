define([
	'jquery',
	'marionette',
	//Views
	'app/views/GeneCollectionView',
	//Templates
	'text!static/app/templates/AttributeRankerLayout.html'
    ], function($, Marionette, GeneCollectionView,AttributeRankerTmpl) {
AttributeRanker = Marionette.Layout.extend({
    template: AttributeRankerTmpl,
    regions: {
    	"geneList": "#ranked-attributes-region"
    },
    url: base_url+"MetaServer",
    initialize: function(){
    	this.getRankedAttribute();
    },
    getRankedAttributes: function(){
    	this.model.set('getRankedAttr', true);
    	var tree = {};
    	if(Cure.PlayerNodeCollection.length>0){
    		tree = Cure.PlayerNodeCollection.at(0).toJSON();
    	}
    	var args = {
				command : "rank_attributes",
				dataset : Cure.dataset,
				treestruct : tree
			};

		//POST request to server.		
		$.ajax({
			type : 'POST',
			url : this.url,
			data : JSON.stringify(args),
			dataType : 'json',
			contentType : "application/json; charset=utf-8",
			success : this.parseReponse,
			error : this.error
		});
    },
    geneColl: null,
    parseReponse: function(data){
    	console.log(data);
    	this.geneColl.add(data);
    },
    error : function(data) {
		Cure.utils.showAlert("<strong>Server Error</strong><br>Please try again in a while.", 0);
	},
    render: function(){
    	
    }
});
return AttributeRanker;
});
