define([
  //Libraries
	'jquery',
	'marionette',
	'backbone',
	//Templates
	'text!static/app/templates/GeneListItem.html',
	'jqueryui'
    ], function($, Marionette, Backbone, GeneItemTmpl) {
GeneItemView = Marionette.ItemView.extend({
	tagName: 'tr',
	events: {
		'click .delete': 'deleteThisItem',
	},
	showLimit: false,
	initialize: function(args){
		console.log(args);
		if(args.showLimits){
			this.showLimits = args.showLimits;
		}
	},
	ui: {
		'keepAll': '.keepAll',
		'keepInCollection': '.keepInCollection'
	},
	template: function(serialized_model){
		return GeneItemTmpl(serialized_model);
	},
	templateHelpers: function(){
		return {
		   	showLimits: this.showLimits
		}
	},
	deleteThisItem: function(){
		this.model.destroy();
	}
});

return GeneItemView;
});
