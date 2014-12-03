define([
	'jquery',
	'marionette',
	//Templates
	'text!static/app/templates/DatasetDescription.html'
    ], function($, Marionette, ZoomTemplate) {
ZoomView = Marionette.ItemView.extend({
	template: ZoomTemplate,
	initialize : function() {
		var _this = this;
		this.listenTo(Cure.PlayerNodeCollection,'change', this.render);
		this.listenTo(Cure.PlayerNodeCollection,'remove', this.render);
		this.listenTo(Cure.vent, 'window:resized', this.render);
//		$(window).scroll(function(){
//			if($(window).scrollTop() > 50){
//				_this.$el.css({'position':'absolute','top':'0px'});
//			} else {
//				_this.$el.css({'position':'relative','top':'auto'});
//			}
//		});
	},
	events:{
		'click .expand-desc': 'expandDesc',
		'click .reduce-desc': 'reduceDesc',
	},
	expandDescVal: false,
	templateHelpers: function(){
		return {
			expandDesc: this.expandDescVal
		};
	},
	expandDesc: function(){
		this.expandDescVal = true;
		this.render();
	},
	reduceDesc: function(){
		this.expandDescVal = false;
		this.render();
	}
});

return ZoomView;
});
		        
