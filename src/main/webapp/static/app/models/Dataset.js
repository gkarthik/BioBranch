define([
    	'backbone',
    	'backboneRelational'
    ], function(Backbone) {
Dataset = Backbone.RelationalModel.extend({
	defaults: {
		setTest: false
	}	
});
return Dataset;
});
