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
		this.model.set('setTest',true);
		$(Cure.appRegion.currentView.$el.find('input[name="testOptions"]')[1]).attr('checked',true);
	}
});

return DatasetItemView;
});
