define([
  //Libraries
	'jquery',
	'marionette',
	//Templates
	'text!static/app/templates/ConfusionMatrix.html',
    ], function($, Marionette, CfTmpl) {
CfMatrixView = Backbone.Marionette.ItemView.extend({
	tagName: 'table',
	className: 'table table-bordered table-condensed',
	template : CfTmpl,
	initialize : function(){
		this.model.bind('change', this.render);
	}
});

return CfMatrixView;
});
