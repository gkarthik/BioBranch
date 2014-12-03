define([
  //Libraries
	'jquery',
	'marionette',
	//Templates
	'text!static/app/templates/ConfusionMatrix.html',
    ], function($, Marionette, CfTmpl) {
CfMatrixView = Backbone.Marionette.ItemView.extend({
	tagName: 'div',
	className: 'table-responsive',
	template : CfTmpl,
	initialize : function(){
		this.model.bind('change', this.render);
	}
});

return CfMatrixView;
});
