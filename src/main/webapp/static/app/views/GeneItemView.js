define([
  //Libraries
	'jquery',
	'marionette',
	'backbone',
	//Templates
	'text!static/app/templates/GeneItem.html',
	'jqueryui'
    ], function($, Marionette, Backbone, GeneItemTmpl) {
GeneItemView = Marionette.ItemView.extend({
	tagName: 'tr',
	events: {
			'click .keepInCollection':'selectGene'
	},
	ui: {
		'keepAll': '.keepAll',
		'keepInCollection': '.keepInCollection'
	},
	initialize : function() {
		this.listenTo(this.model,'change', this.render);
		this.$el.attr("id",this.cid);
	},
	template: GeneItemTmpl,
	selectGene: function(){
		if($(this.ui.keepInCollection).is(':checked')){
			this.model.set('keepInCollection',1);
		} else {
			this.model.set('keepInCollection',0);
		}
	}
});

return GeneItemView;
});
