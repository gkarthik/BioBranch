define([
    	'backbone',
    	'backboneRelational'
    ], function(Backbone) {
Dataset = Backbone.RelationalModel.extend({
	defaults: {
		setTest: false,
		cc: false,
		cf: false,
		t: false
	}	
});
return Dataset;
});
