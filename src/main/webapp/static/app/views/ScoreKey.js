define([
  //Libraries
	'jquery',
	'marionette',
	'backbone',
	//Templates
	'text!static/app/templates/ScoreKey.html'
    ], function($, Marionette, Backbone, ScoreKeyTemplate) {
ScoreKeyView = Marionette.ItemView.extend({
	tagName: 'table',
	className: 'table table-condensed',
	initialize : function() {
		this.model.bind('change', this.render);
	},
	template: ScoreKeyTemplate
});

return ScoreKeyView;
});
