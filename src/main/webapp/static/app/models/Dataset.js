define([
    	'backbone',
    	'backboneRelational'
    ], function(Backbone) {
Dataset = Backbone.RelationalModel.extend({
	defaults: {
		setTest: false,
		cc: false,
		cf: false,
		t: false,
		split: false
	}	
});
return Dataset;
});
