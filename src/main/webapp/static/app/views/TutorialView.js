define([
	'jquery',
	'marionette',
	//Model
	'app/models/Tutorial',
	//Templates
	'text!static/app/templates/Tutorial.html'
    ], function($, Marionette, Tutorial, TutorialTmpl) {
TutorialView = Marionette.ItemView.extend({
	template: TutorialTmpl,
	model: Tutorial,
	className: "tutorial-wrapper",
	events: {
		"click .close-tutorial": "remove"
	},
	initialize: function(){
		
	},
	onRender: function(){
		this.model.markComplete();
	}
});

return TutorialView;
});
