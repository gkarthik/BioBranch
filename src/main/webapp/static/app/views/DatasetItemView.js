define([
  //Libraries
	'jquery',
	'marionette',
	//Views
	'text!static/app/templates/DatasetItem.html',
    ], function($, Marionette, DatasetTmpl) {
DatasetItemView = Marionette.ItemView.extend({
	template: DatasetTmpl,
	initialize: function(){
		this.listenTo(this.model, 'change', this.render);
	},
	events: {
		'click .choose-training-dataset': 'setTestVal'
	},
	setTestVal: function(){
		$($("input[name='testOptions']")[1]).prop("checked",true);
		this.model.set('setTest',true);
	}
});

return DatasetItemView;
});
