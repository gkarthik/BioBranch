define([
    	'backbone',
    	'backboneRelational'
    ], function(Backbone) {
GeneItem = Backbone.RelationalModel.extend({
	defaults: {
		name: '',
		unique_id: '',
		keepInCollection: 0,
		keepAll: 0,
		uLimit: null,
		lLimit: null,
		setUpperLimit: false,
		setLowerLimit: false
	},
	url:base_url
});
return GeneItem;
});
