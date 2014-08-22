define([
	'jquery',
  'backbone',
	'app/models/Dataset'
    ], function($, Backbone, Dataset) {
DatasetCollection = Backbone.Collection.extend({
	model: Dataset,
	initialize: function(){
		
	}
});
return DatasetCollection;
});