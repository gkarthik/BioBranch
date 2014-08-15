define([
	'jquery',
	'marionette',
	//Views
	'app/views/GeneCollectionView',
	//Models
	'app/collections/GeneCollection',
	//Templates
	'text!static/app/templates/AttributeRankerLayout.html'
    ], function($, Marionette, GeneCollectionView, GeneCollection, AttributeRankerTmpl) {
AttributeRanker = Marionette.Layout.extend({
    template: AttributeRankerTmpl,
    regions: {
    	"geneList": "#ranked-attributes-region"
    },
    events: {
    	'click .close-attr-rank': 'closeAttrRank'
    },
    className:'panel panel-default',
    url: base_url+"MetaServer",
    initialize: function(){
    	_.bindAll(this,'parseReponse');
    },
    closeAttrRank: function(){
    	Cure.sidebarLayout.AttrRankRegion.close();
    },
    getRankedAttributes: function(){
    	if(this.model){
        	this.model.set('pickInst', true);
    	}
    	var tree = {};
    	if(Cure.PlayerNodeCollection.length>0){
    		tree = Cure.PlayerNodeCollection.at(0).toJSON();
    	}
    	var testOptions = {
				value: $("input[name='testOptions']:checked").val(),
				percentSplit:  $("input[name='percent-split']").val()
		};
    	
    	var args = {
				command : "rank_attributes",
				dataset : Cure.dataset,
				treestruct : tree,
				testOptions: testOptions
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
    	if(this.model){
        	this.model.set('pickInst', false);
    	}
    	this.geneColl = new GeneCollection();
    	this.geneColl.add(data);
    	console.log(this.geneColl);
    	this.geneList.show(new GeneCollectionView({collection: this.geneColl}));
    },
    error : function(data) {
		Cure.utils.showAlert("<strong>Server Error</strong><br>Please try again in a while.", 0);
	},
    onRender: function(){
    	this.getRankedAttributes();
    }
});
return AttributeRanker;
});
